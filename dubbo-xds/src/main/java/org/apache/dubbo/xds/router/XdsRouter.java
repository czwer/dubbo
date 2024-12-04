/*
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.apache.dubbo.xds.router;

import org.apache.dubbo.common.URL;
import org.apache.dubbo.common.utils.Holder;
import org.apache.dubbo.common.utils.StringUtils;
import org.apache.dubbo.rpc.Invocation;
import org.apache.dubbo.rpc.Invoker;
import org.apache.dubbo.rpc.RpcException;
import org.apache.dubbo.rpc.RpcInvocation;
import org.apache.dubbo.rpc.cluster.router.RouterSnapshotNode;
import org.apache.dubbo.rpc.cluster.router.state.AbstractStateRouter;
import org.apache.dubbo.rpc.cluster.router.state.BitList;
import org.apache.dubbo.rpc.support.RpcUtils;
import org.apache.dubbo.xds.resource.route.ClusterWeight;
import org.apache.dubbo.xds.resource.route.Route;
import org.apache.dubbo.xds.resource.route.VirtualHost;
import org.apache.dubbo.xds.resource.update.CdsUpdate;
import org.apache.dubbo.xds.resource.update.CdsUpdate.ClusterType;
import org.apache.dubbo.xds.resource.update.EdsUpdate;

import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ThreadLocalRandom;
import java.util.stream.Collectors;

import static org.apache.dubbo.config.Constants.MESH_KEY;

public class XdsRouter<T> extends AbstractStateRouter<T> {

    private Map<String, VirtualHost> xdsVirtualHostMap = new ConcurrentHashMap<>();
    private Map<String, CdsUpdate> xdsClusterMap = new ConcurrentHashMap<>();
    private Map<String, EdsUpdate> xdsEdsMap = new ConcurrentHashMap<>();
    private final Map<String, BitList<Invoker<T>>> xdsClusterInvokersMap = new ConcurrentHashMap<>();

    public XdsRouter(URL url) {
        super(url);
    }

    @Override
    protected BitList<Invoker<T>> doRoute(
            BitList<Invoker<T>> invokers,
            URL url,
            Invocation invocation,
            boolean needToPrintMessage,
            Holder<RouterSnapshotNode<T>> routerSnapshotNodeHolder,
            Holder<String> messageHolder)
            throws RpcException {

        // return all invokers directly if xds is not used
        String meshType = url.getParameter(MESH_KEY);
        if (StringUtils.isEmpty(meshType)) {
            return invokers;
        }

        // load xds data
        processXdsData((RpcInvocation) invocation);

        // 1. match cluster
        String matchedCluster = matchCluster(invocation);

        // 2. match invokers
        BitList<Invoker<T>> matchedInvokers = matchInvoker(matchedCluster, invokers);

        return matchedInvokers;
    }

    private void processXdsData(RpcInvocation invocation) {
        this.xdsVirtualHostMap = (Map<String, VirtualHost>) invocation.getAttachmentObject("xdsVirtualHostMap");
        this.xdsClusterMap = (Map<String, CdsUpdate>) invocation.getAttachmentObject("xdsClusterMap");
        this.xdsEdsMap = (Map<String, EdsUpdate>) invocation.getAttachmentObject("xdsEdsMap");
    }

    private String matchCluster(Invocation invocation) {
        String cluster = null;
        String serviceName = invocation.getInvoker().getUrl().getParameter("provided-by");
        VirtualHost xdsVirtualHost = xdsVirtualHostMap.get(serviceName);

        // match route
        for (Route xdsRoute : xdsVirtualHost.getRoutes()) {
            // match path
            String path = "/" + invocation.getInvoker().getUrl().getPath() + "/" + RpcUtils.getMethodName(invocation);
            if (xdsRoute.getRouteMatch().isPathMatch(path)) {
                cluster = xdsRoute.getRouteAction().getCluster();
                // if weighted cluster
                if (cluster == null) {
                    cluster = computeWeightCluster(xdsRoute.getRouteAction().getWeightedClusters());
                }
                CdsUpdate xdsCluster = xdsClusterMap.get(cluster);
                cluster = findCluster(xdsCluster);
            }
            if (cluster != null) break;
        }

        return cluster;
    }

    private String findCluster(CdsUpdate xdsCluster) {
        if (ClusterType.EDS.equals(xdsCluster.getClusterType())) {
            return xdsCluster.getEdsServiceName();
        } else if (ClusterType.AGGREGATE.equals(xdsCluster.getClusterType())) {
            String cluster = xdsCluster.getPrioritizedClusterNames().get(0);
            CdsUpdate cdsUpdate = xdsClusterMap.get(cluster);
            return findCluster(cdsUpdate);
        } else {
            return null;
        }
    }

    private String computeWeightCluster(List<ClusterWeight> weightedClusters) {
        int totalWeight = Math.max(
                weightedClusters.stream().mapToInt(ClusterWeight::getWeight).sum(), 1);

        int target = ThreadLocalRandom.current().nextInt(1, totalWeight + 1);
        for (ClusterWeight xdsClusterWeight : weightedClusters) {
            int weight = xdsClusterWeight.getWeight();
            target -= weight;
            if (target <= 0) {
                return xdsClusterWeight.getName();
            }
        }
        return null;
    }

    private BitList<Invoker<T>> matchInvoker(String clusterName, BitList<Invoker<T>> invokers) {

        List<Invoker<T>> filterInvokers = invokers.stream()
                .filter(inv -> inv.getUrl().getParameter("clusterID").equals(clusterName))
                .collect(Collectors.toList());
        return new BitList<>(filterInvokers);
    }
}
