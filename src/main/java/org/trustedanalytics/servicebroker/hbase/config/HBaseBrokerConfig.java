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

import org.trustedanalytics.hadoop.HadoopConfigurationHelper;

import java.io.IOException;
import java.util.Map;
import java.util.Optional;

import javax.security.auth.login.LoginException;

import org.apache.hadoop.conf.Configuration;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;


@org.springframework.context.annotation.Configuration
public class HBaseBrokerConfig {

    @Autowired
    private ExternalConfiguration configuration;

    @Bean
    public Configuration getHadoopConfiguration() throws LoginException, IOException {
        Configuration hadoopConf = new Configuration(false);
        Optional<Map<String, String>> hbaseParams =
                HadoopConfigurationHelper.getHadoopConfFromJson(configuration.getHBaseProvidedParams());
        HadoopConfigurationHelper.mergeConfiguration(hadoopConf, hbaseParams.get());
        return hadoopConf;
    }
}
