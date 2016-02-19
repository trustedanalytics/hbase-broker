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
package org.trustedanalytics.servicebroker.hbase.service.integration;

import com.google.common.hash.Hashing;
import org.trustedanalytics.cfbroker.store.api.BrokerStore;
import org.trustedanalytics.cfbroker.store.api.Location;
import org.trustedanalytics.cfbroker.store.zookeeper.service.ZookeeperClient;
import org.trustedanalytics.servicebroker.hbase.config.Application;
import org.trustedanalytics.servicebroker.hbase.config.ExternalConfiguration;
import org.trustedanalytics.servicebroker.hbase.config.Profiles;
import org.trustedanalytics.servicebroker.hbase.config.Qualifiers;
import org.trustedanalytics.servicebroker.hbase.service.HBaseServiceInstanceBindingService;
import org.trustedanalytics.servicebroker.hbase.service.HBaseServiceInstanceService;
import org.trustedanalytics.servicebroker.hbase.service.NamespaceHelper;
import org.apache.hadoop.hbase.HBaseTestingUtility;
import org.apache.hadoop.hbase.client.HBaseAdmin;
import org.cloudfoundry.community.servicebroker.model.CreateServiceInstanceBindingRequest;
import org.cloudfoundry.community.servicebroker.model.CreateServiceInstanceRequest;
import org.cloudfoundry.community.servicebroker.model.ServiceInstance;
import org.cloudfoundry.community.servicebroker.model.ServiceInstanceBinding;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.web.WebAppConfiguration;

import java.util.Collections;
import java.util.Optional;

import org.springframework.boot.test.SpringApplicationConfiguration;
import org.springframework.boot.test.IntegrationTest;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.core.IsEqual.equalTo;

@RunWith(SpringJUnit4ClassRunner.class)
@SpringApplicationConfiguration(classes = {Application.class, ZookeeperTestConfig.class, HBaseTestConfiguration.class, HBaseTestingUtilityConfiguration.class})
@WebAppConfiguration
@IntegrationTest("server.port=0")
@ActiveProfiles(Profiles.INTEGRATION_TESTS)
public class HbaseBrokerTest {

  @Autowired
  private HBaseTestingUtility utility;

  @Autowired
  private HBaseServiceInstanceService instanceService;

  @Autowired
  private HBaseServiceInstanceBindingService bindingService;

  @Autowired
  private ZookeeperClient zkClient;

  @Autowired
  private ExternalConfiguration config;

  @Autowired
  @Qualifier(Qualifiers.SERVICE_INSTANCE_BINDING)
  public BrokerStore<CreateServiceInstanceBindingRequest> bindingBrokerStore;

  @Test
  public void testCreateInstance_success_shouldCreateHBaseNamespace() throws Exception {
    instanceService.createServiceInstance(getCreateInstanceRequest("instanceId"));
    HBaseAdmin hBaseAdmin = utility.getHBaseAdmin();
    String createdNSName = hBaseAdmin.getNamespaceDescriptor(NamespaceHelper.getNamespaceName("instanceId")).getName();
    assertThat(createdNSName, equalTo(Hashing.sha1().hashString("instanceId").toString()));
  }

  @Test
  public void testCreateInstance_success_shouldStoreInstanceDataInBrokerStore() throws Exception {
    CreateServiceInstanceRequest request = getCreateInstanceRequest("instanceId2");
    instanceService.createServiceInstance(request);
    ServiceInstance serviceInstance = instanceService.getServiceInstance("instanceId2");
    assertThat(request.getServiceInstanceId(), equalTo(serviceInstance.getServiceInstanceId()));
    //todo: compare all of the fields
  }

  @Test
  public void testCreateInstanceBinding_success_shouldStoreInstanceBindingDataInBrokerStore() throws Exception {
    instanceService.createServiceInstance(getCreateInstanceRequest("instanceId3"));
    CreateServiceInstanceBindingRequest request = getCreateServiceInstanceBindingRequest("instanceId3", "bindingId2");
    bindingService.createServiceInstanceBinding(request);
    Optional<CreateServiceInstanceBindingRequest> bindingInstance = bindingBrokerStore.getById(Location.newInstance("bindingId2", "instanceId3"));
    assertThat(bindingInstance.get().getAppGuid(), equalTo("appGuid"));
    assertThat(bindingInstance.get().getServiceDefinitionId(), equalTo("instanceId3"));
    assertThat(bindingInstance.get().getPlanId(), equalTo("fake-shared-plan"));
  }

  @Test
  public void testCreateInstanceBinding_success_shouldReturnHBaseNamespaceNameInCredentials() throws Exception {
    CreateServiceInstanceBindingRequest request = new CreateServiceInstanceBindingRequest(
        getServiceInstance("serviceId").getServiceDefinitionId(), "fake-shared-plan", "appGuid").
        withBindingId("bindingId").withServiceInstanceId("serviceId");
    ServiceInstanceBinding binding = bindingService.createServiceInstanceBinding(request);
    String namespaceInCredentials = (String) binding.getCredentials().get(HBaseServiceInstanceBindingService.NAMESPACE);
    assertThat(namespaceInCredentials, equalTo(Hashing.sha1().hashString("serviceId").toString()));
  }

  private ServiceInstanceBinding getServiceInstanceBinding(String id) {
    return new ServiceInstanceBinding(id, "serviceId", Collections.emptyMap(), null, "guid");
  }

  private ServiceInstance getServiceInstance(String id) {
    return new ServiceInstance(new CreateServiceInstanceRequest(id, "fake-shared-plan", "organizationGuid", "spaceGuid"));
  }

  private CreateServiceInstanceRequest getCreateInstanceRequest(String serviceId) {
    return new CreateServiceInstanceRequest("serviceDefinitionId", "fake-shared-plan", "organizationGuid", "spaceGuid").
        withServiceInstanceId(serviceId);
  }

  private CreateServiceInstanceBindingRequest getCreateServiceInstanceBindingRequest(String serviceId, String bindingId) {
    return new CreateServiceInstanceBindingRequest(
        getServiceInstance(serviceId).getServiceDefinitionId(), "fake-shared-plan", "appGuid").
        withBindingId(bindingId).withServiceInstanceId(serviceId);
  }

}
