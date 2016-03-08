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
package org.trustedanalytics.servicebroker.hbase.plans;

import java.io.IOException;
import java.util.Map;
import java.util.UUID;

import org.apache.hadoop.hbase.NamespaceDescriptor;
import org.apache.hadoop.hbase.client.Admin;
import org.cloudfoundry.community.servicebroker.exception.ServiceBrokerException;
import org.cloudfoundry.community.servicebroker.exception.ServiceInstanceExistsException;
import org.cloudfoundry.community.servicebroker.model.ServiceInstance;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.trustedanalytics.servicebroker.framework.service.ServicePlanDefinition;
import org.trustedanalytics.servicebroker.hbase.NamespaceHelper;
import org.trustedanalytics.servicebroker.hbase.plans.binding.HbaseSimpleBindingOperations;

@Component("shared")
class HbasePlanShared implements ServicePlanDefinition {

  @Autowired
  private Admin admin;

  private HbaseSimpleBindingOperations hbaseSimpleBindingOperations;

  @Autowired
  public HbasePlanShared(HbaseSimpleBindingOperations hbaseSimpleBindingOperations){
    this.hbaseSimpleBindingOperations = hbaseSimpleBindingOperations;
  }

  @Override
  public void provision(ServiceInstance serviceInstance) throws ServiceInstanceExistsException,
      ServiceBrokerException {
    try {
      provisionNamespace(serviceInstance.getOrganizationGuid(), serviceInstance.getServiceInstanceId());
    } catch (IOException e) {
      throw new ServiceBrokerException(e);
    }
  }

  @Override
  public Map<String, Object> bind(ServiceInstance serviceInstance) throws ServiceBrokerException {
    return hbaseSimpleBindingOperations.createCredentialsMap(serviceInstance.getServiceInstanceId());
  }

  private void provisionNamespace(String orgGuid, String instanceId) throws IOException {
    String namespaceName = NamespaceHelper.getNamespaceName(instanceId);
    NamespaceDescriptor descriptor = NamespaceDescriptor.create(namespaceName).build();
    admin.createNamespace(descriptor);
  }
}
