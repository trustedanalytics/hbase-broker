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

import org.apache.hadoop.conf.Configuration;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.trustedanalytics.cfbroker.config.HadoopZipConfiguration;
import org.trustedanalytics.servicebroker.framework.Credentials;
import org.trustedanalytics.servicebroker.framework.kerberos.KerberosProperties;

import com.google.common.collect.ImmutableMap;

@org.springframework.context.annotation.Configuration
public class HBaseBrokerConfig {

  @Value("${hbase.provided.zip}")
  private String hbaseProvidedZip;

  @Autowired
  private KerberosProperties kerberosProperties;

  @Bean
  public Configuration getHadoopConfiguration() throws LoginException, IOException {
    return HadoopZipConfiguration.createHadoopZipConfiguration(hbaseProvidedZip)
        .getAsHadoopConfiguration();
  }

  @Bean
  public Credentials getCredentials() throws IOException {
    HadoopZipConfiguration hadoopZipConfiguration =
        HadoopZipConfiguration.createHadoopZipConfiguration(hbaseProvidedZip);

    ImmutableMap.Builder<String, Object> credentialsBuilder =
        new ImmutableMap.Builder<String, Object>().putAll(hadoopZipConfiguration
            .getBrokerCredentials());

    if(kerberosProperties.isKerberosEnabled()) {
      credentialsBuilder.put("kerberos", kerberosProperties.getCredentials());
    }

    return new Credentials(credentialsBuilder.build());
  }
}
