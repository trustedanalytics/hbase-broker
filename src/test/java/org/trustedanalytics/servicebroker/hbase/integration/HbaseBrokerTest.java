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
package org.trustedanalytics.servicebroker.hbase.integration;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.core.IsEqual.equalTo;
import static org.junit.Assert.assertFalse;
import static org.trustedanalytics.servicebroker.test.cloudfoundry.CfModelsFactory.*;

import java.util.Optional;

import org.apache.hadoop.hbase.HBaseTestingUtility;
import org.apache.hadoop.hbase.client.HBaseAdmin;
import org.cloudfoundry.community.servicebroker.model.CreateServiceInstanceBindingRequest;
import org.cloudfoundry.community.servicebroker.model.CreateServiceInstanceRequest;
import org.cloudfoundry.community.servicebroker.model.ServiceInstance;
import org.cloudfoundry.community.servicebroker.model.ServiceInstanceBinding;
import org.cloudfoundry.community.servicebroker.service.ServiceInstanceBindingService;
import org.cloudfoundry.community.servicebroker.service.ServiceInstanceService;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.test.IntegrationTest;
import org.springframework.boot.test.SpringApplicationConfiguration;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.web.WebAppConfiguration;
import org.trustedanalytics.cfbroker.store.api.BrokerStore;
import org.trustedanalytics.cfbroker.store.api.Location;
import org.trustedanalytics.servicebroker.framework.Qualifiers;
import org.trustedanalytics.servicebroker.hbase.Application;
import org.trustedanalytics.servicebroker.hbase.integration.config.HBaseTestConfiguration;
import org.trustedanalytics.servicebroker.hbase.integration.config.HBaseTestingUtilityConfiguration;
import org.trustedanalytics.servicebroker.hbase.integration.config.ZkLocalConfiguration;

@RunWith(SpringJUnit4ClassRunner.class)
@SpringApplicationConfiguration(classes = {Application.class, ZkLocalConfiguration.class,
    HBaseTestConfiguration.class, HBaseTestingUtilityConfiguration.class})
@WebAppConfiguration
@IntegrationTest("server.port=0")
@ActiveProfiles("integration-test")
public class HbaseBrokerTest {

  @Autowired
  private HBaseTestingUtility utility;

  @Autowired
  private ServiceInstanceService instanceService;

  @Autowired
  private ServiceInstanceBindingService bindingService;

  @Autowired
  @Qualifier(Qualifiers.SERVICE_INSTANCE_BINDING)
  public BrokerStore<CreateServiceInstanceBindingRequest> bindingBrokerStore;

  @Test
  public void testCreateInstance_success_shouldNotCreateHBaseNamespace() throws Exception {
    // given
    HBaseAdmin hBaseAdmin = utility.getHBaseAdmin();
    int numberOfNamespacesBeforeServiceCreation = hBaseAdmin.listNamespaceDescriptors().length;
    // when
    ServiceInstance instance = getServiceInstance("instanceId", "fake-bare-plan");
    instanceService.createServiceInstance(getCreateInstanceRequest(instance));
    // then
    int numberOfNamespacesAfterServiceCreation = hBaseAdmin.listNamespaceDescriptors().length;
    assertThat(numberOfNamespacesBeforeServiceCreation, equalTo(numberOfNamespacesAfterServiceCreation));
  }

  @Test
  public void testCreateInstance_success_shouldStoreInstanceDataInBrokerStore() throws Exception {
    ServiceInstance instance = getServiceInstance("instanceId2", "fake-bare-plan");
    CreateServiceInstanceRequest request = getCreateInstanceRequest(instance);
    instanceService.createServiceInstance(request);
    ServiceInstance serviceInstance = instanceService.getServiceInstance("instanceId2");
    assertThat(request.getServiceInstanceId(), equalTo(serviceInstance.getServiceInstanceId()));
    //todo: compare all of the fields
  }

  @Test
  public void testCreateInstanceBinding_success_shouldStoreInstanceBindingDataInBrokerStore()
      throws Exception {
    ServiceInstance instance = getServiceInstance("instanceId3", "fake-bare-plan");
    instanceService.createServiceInstance(getCreateInstanceRequest(instance));
    CreateServiceInstanceBindingRequest request = getCreateBindingRequest("instanceId3").withBindingId("bindingId2");
    bindingService.createServiceInstanceBinding(request);
    Optional<CreateServiceInstanceBindingRequest> bindingInstance =
        bindingBrokerStore.getById(Location.newInstance("bindingId2", "instanceId3"));
    assertThat(bindingInstance.get().getAppGuid(), equalTo(request.getAppGuid()));
    assertThat(bindingInstance.get().getServiceDefinitionId(), equalTo(request.getServiceDefinitionId()));
    assertThat(bindingInstance.get().getPlanId(), equalTo(request.getPlanId()));
  }

}
