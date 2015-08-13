/**
 * Copyright (c) 2015 Intel Corporation
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.trustedanalytics.servicebroker.hbase.config;

import java.io.IOException;

import javax.security.auth.login.LoginException;

import org.trustedanalytics.cfbroker.store.api.BrokerStore;
import org.trustedanalytics.cfbroker.store.impl.ServiceInstanceServiceStore;
import org.apache.hadoop.hbase.client.Admin;
import org.cloudfoundry.community.servicebroker.model.ServiceInstance;
import org.cloudfoundry.community.servicebroker.service.ServiceInstanceService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.trustedanalytics.servicebroker.hbase.service.HBaseServiceInstanceService;

@Configuration
public class ServiceInstanceServiceConfig {

    private static final Logger LOGGER = LoggerFactory
            .getLogger(ServiceInstanceServiceConfig.class);

    @Autowired
    private ExternalConfiguration configuration;

    @Autowired
    private Admin admin;

    @Autowired
    @Qualifier(value = Qualifiers.SERVICE_INSTANCE)
    private BrokerStore<ServiceInstance> store;

    @Bean
    public ServiceInstanceService getServiceInstanceService() throws IllegalArgumentException,
            IOException, LoginException {

        return new HBaseServiceInstanceService(new ServiceInstanceServiceStore(store), admin);
    }
}
