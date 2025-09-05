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
package org.apache.dubbo.spring.boot.actuate.autoconfigure;

import org.apache.dubbo.common.logger.ErrorTypeAwareLogger;
import org.apache.dubbo.common.logger.LoggerFactory;
import org.apache.dubbo.spring.boot.actuate.endpoint.DubboConfigsMetadataEndpoint;
import org.apache.dubbo.spring.boot.actuate.endpoint.DubboPropertiesMetadataEndpoint;
import org.apache.dubbo.spring.boot.actuate.endpoint.DubboQosEndpoints;
import org.apache.dubbo.spring.boot.actuate.endpoint.DubboReferencesMetadataEndpoint;
import org.apache.dubbo.spring.boot.actuate.endpoint.DubboServicesMetadataEndpoint;
import org.apache.dubbo.spring.boot.actuate.endpoint.DubboShutdownEndpoint;
import org.apache.dubbo.spring.boot.actuate.endpoint.condition.CompatibleConditionalOnEnabledEndpoint;

import org.springframework.boot.actuate.autoconfigure.endpoint.condition.ConditionalOnAvailableEndpoint;
import org.springframework.boot.actuate.endpoint.annotation.Endpoint;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;

import static org.apache.dubbo.spring.boot.util.DubboUtils.DUBBO_PREFIX;

/**
 * Dubbo {@link Endpoint @Endpoint} Auto-{@link Configuration} for Spring Boot Actuator 2.0
 *
 * @see Endpoint
 * @see Configuration
 * @since 2.7.0
 */
@ConditionalOnProperty(prefix = DUBBO_PREFIX, name = "enabled", matchIfMissing = true)
@Configuration
@PropertySource(
        name = "Dubbo Endpoints Default Properties",
        value = "classpath:/META-INF/dubbo-endpoints-default.properties")
public class DubboEndpointAnnotationAutoConfiguration {
    public static final ErrorTypeAwareLogger logger =
            LoggerFactory.getErrorTypeAwareLogger(DubboEndpointAnnotationAutoConfiguration.class);

    @Bean
    @ConditionalOnMissingBean
    @ConditionalOnAvailableEndpoint
    @CompatibleConditionalOnEnabledEndpoint
    public DubboQosEndpoints dubboQosEndpoints() {
        logger.info("自定义日志---@Bean方式声明Bean：DubboQosEndpoints");
        return new DubboQosEndpoints();
    }

    @Bean
    @ConditionalOnMissingBean
    @ConditionalOnAvailableEndpoint
    @CompatibleConditionalOnEnabledEndpoint
    public DubboConfigsMetadataEndpoint dubboConfigsMetadataEndpoint() {
        logger.info("自定义日志---@Bean方式声明Bean：DubboConfigsMetadataEndpoint");
        return new DubboConfigsMetadataEndpoint();
    }

    @Bean
    @ConditionalOnMissingBean
    @ConditionalOnAvailableEndpoint
    @CompatibleConditionalOnEnabledEndpoint
    public DubboPropertiesMetadataEndpoint dubboPropertiesEndpoint() {
        logger.info("自定义日志---@Bean方式声明Bean：DubboPropertiesMetadataEndpoint");
        return new DubboPropertiesMetadataEndpoint();
    }

    @Bean
    @ConditionalOnMissingBean
    @ConditionalOnAvailableEndpoint
    @CompatibleConditionalOnEnabledEndpoint
    public DubboReferencesMetadataEndpoint dubboReferencesMetadataEndpoint() {
        logger.info("自定义日志---@Bean方式声明Bean：DubboReferencesMetadataEndpoint");
        return new DubboReferencesMetadataEndpoint();
    }

    @Bean
    @ConditionalOnMissingBean
    @ConditionalOnAvailableEndpoint
    @CompatibleConditionalOnEnabledEndpoint
    public DubboServicesMetadataEndpoint dubboServicesMetadataEndpoint() {
        logger.info("自定义日志---@Bean方式声明Bean：DubboServicesMetadataEndpoint");
        return new DubboServicesMetadataEndpoint();
    }

    @Bean
    @ConditionalOnMissingBean
    @ConditionalOnAvailableEndpoint
    @CompatibleConditionalOnEnabledEndpoint
    public DubboShutdownEndpoint dubboShutdownEndpoint() {
        logger.info("自定义日志---@Bean方式声明Bean：DubboShutdownEndpoint");
        return new DubboShutdownEndpoint();
    }
}
