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
package org.trustedanalytics.servicebroker.hbase.service;

import org.apache.hadoop.hbase.NamespaceDescriptor;
import org.apache.hadoop.hbase.client.Admin;
import org.cloudfoundry.community.servicebroker.exception.ServiceBrokerException;
import org.cloudfoundry.community.servicebroker.exception.ServiceInstanceExistsException;
import org.cloudfoundry.community.servicebroker.model.CreateServiceInstanceRequest;
import org.cloudfoundry.community.servicebroker.model.ServiceInstance;
import org.cloudfoundry.community.servicebroker.service.ServiceInstanceService;
import org.trustedanalytics.cfbroker.store.impl.ForwardingServiceInstanceServiceStore;
import org.trustedanalytics.servicebroker.hbase.config.catalog.BrokerPlans;

import java.io.IOException;

public class HBaseServiceInstanceService extends ForwardingServiceInstanceServiceStore {

  private final BrokerPlans brokerPlans;

  private final Admin admin;

  public HBaseServiceInstanceService(ServiceInstanceService instanceService, BrokerPlans brokerPlans, Admin admin) {
    super(instanceService);
    this.admin = admin;
    this.brokerPlans = brokerPlans;
  }

  @Override
  public ServiceInstance createServiceInstance(CreateServiceInstanceRequest request)
      throws ServiceInstanceExistsException, ServiceBrokerException {
    ServiceInstance serviceInstance = super.createServiceInstance(request);

    if(brokerPlans.getPlanProvisioning(request.getPlanId())) {
      try {
        provisionNamespace(serviceInstance.getOrganizationGuid(), serviceInstance.getServiceInstanceId());
      } catch (IOException e) {
        throw new ServiceBrokerException(e);
      }
    }

    return serviceInstance;
  }

  private void provisionNamespace(String orgGuid, String instanceId) throws IOException {
    String namespaceName = NamespaceHelper.getNamespaceName(instanceId);
    NamespaceDescriptor descriptor = NamespaceDescriptor.create(namespaceName).build();
    admin.createNamespace(descriptor);
  }

}
