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
package org.trustedanalytics.servicebroker.hbase.config.store;

import org.cloudfoundry.community.servicebroker.model.CreateServiceInstanceBindingRequest;
import org.cloudfoundry.community.servicebroker.model.ServiceInstance;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.trustedanalytics.cfbroker.store.api.BrokerStore;
import org.trustedanalytics.cfbroker.store.serialization.RepositoryDeserializer;
import org.trustedanalytics.cfbroker.store.serialization.RepositorySerializer;
import org.trustedanalytics.cfbroker.store.zookeeper.service.ZookeeperClient;
import org.trustedanalytics.cfbroker.store.zookeeper.service.ZookeeperStore;
import org.trustedanalytics.servicebroker.hbase.config.ExternalConfiguration;
import org.trustedanalytics.servicebroker.hbase.config.Qualifiers;

import java.io.IOException;

@Configuration
public class BrokerStoreConfig {

  @Autowired
  private ZookeeperClient zkClient;

  @Autowired
  private ExternalConfiguration config;

  @Autowired
  @Qualifier(Qualifiers.SERVICE_INSTANCE)
  private RepositorySerializer<ServiceInstance> instanceSerializer;

  @Autowired
  @Qualifier(Qualifiers.SERVICE_INSTANCE)
  private RepositoryDeserializer<ServiceInstance> instanceDeserializer;

  @Autowired
  @Qualifier(Qualifiers.SERVICE_INSTANCE_BINDING)
  private RepositorySerializer<CreateServiceInstanceBindingRequest> bindingSerializer;

  @Autowired
  @Qualifier(Qualifiers.SERVICE_INSTANCE_BINDING)
  private RepositoryDeserializer<CreateServiceInstanceBindingRequest> bindingDeserializer;

  @Bean
  @Qualifier(Qualifiers.SERVICE_INSTANCE)
  public BrokerStore<ServiceInstance> getServiceInstanceStore()
      throws IOException {
    BrokerStore<ServiceInstance> brokerStore =
        new ZookeeperStore<>(zkClient, instanceSerializer, instanceDeserializer);
    return brokerStore;
  }

  @Bean
  @Qualifier(Qualifiers.SERVICE_INSTANCE_BINDING)
  public BrokerStore<CreateServiceInstanceBindingRequest> getServiceInstanceBindingStore() throws IOException {
    BrokerStore<CreateServiceInstanceBindingRequest> brokerStore =
        new ZookeeperStore<>(zkClient, bindingSerializer, bindingDeserializer);
    return brokerStore;
  }
}
