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

import static org.hamcrest.Matchers.hasEntry;
import static org.junit.Assert.assertThat;
import static org.trustedanalytics.servicebroker.test.cloudfoundry.CfModelsFactory.getServiceInstance;

import java.util.Map;

import org.cloudfoundry.community.servicebroker.model.ServiceInstance;
import org.junit.Test;
import org.trustedanalytics.servicebroker.framework.Credentials;
import org.trustedanalytics.servicebroker.hbase.plans.binding.HbaseBindingClientFactory;

import com.google.common.collect.ImmutableMap;

public class HbasePlanBareTest {
  @Test
  public void bind_doNothing_returnCredentialsMap() throws Exception {
    //arrange
    HbasePlanBare plan =
        new HbasePlanBare(HbaseBindingClientFactory.create(new Credentials(ImmutableMap.of(
            "test", "test"))));

    //act
    ServiceInstance serviceInstance = getServiceInstance();
    Map<String, Object> actualOutputCredentials = plan.bind(serviceInstance);

    //assert
    assertThat(actualOutputCredentials, hasEntry("test", "test"));
  }
}
