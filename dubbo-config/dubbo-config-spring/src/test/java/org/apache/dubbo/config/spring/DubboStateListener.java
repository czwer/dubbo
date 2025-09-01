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
package org.apache.dubbo.config.spring;

import org.apache.dubbo.common.deploy.DeployState;
import org.apache.dubbo.common.logger.Logger;
import org.apache.dubbo.common.logger.LoggerFactory;
import org.apache.dubbo.config.spring.context.event.DubboApplicationStateEvent;

import org.springframework.context.ApplicationListener;

public class DubboStateListener implements ApplicationListener<DubboApplicationStateEvent> {

    private static final Logger logger = LoggerFactory.getLogger(DubboStateListener.class);

    private DeployState state;

    @Override
    public void onApplicationEvent(DubboApplicationStateEvent event) {
        logger.info("自定义日志---监听到事件：DubboApplicationStateEvent，timestamp：" + event.getTimestamp());
        state = event.getState();
    }

    public DeployState getState() {
        return state;
    }
}
