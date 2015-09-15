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

import org.trustedanalytics.cfbroker.store.zookeeper.service.ZookeeperClient;
import org.trustedanalytics.cfbroker.store.zookeeper.service.ZookeeperClientBuilder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.trustedanalytics.hadoop.config.ConfigurationHelper;
import org.trustedanalytics.hadoop.config.ConfigurationHelperImpl;
import org.trustedanalytics.hadoop.config.PropertyLocator;

import java.io.IOException;


@Profile(Profiles.CLOUD)
@Configuration
public class ZookeeperConfig {
    private FactoryHelper helper;

    @Autowired
    private ExternalConfiguration config;

    @Autowired
    private org.apache.hadoop.conf.Configuration hbaseConf;

    public ZookeeperConfig() {
        this.helper = new FactoryHelper();
    }

    @Bean
    public ZookeeperClient getZKClient() throws IOException {
        ConfigurationHelper confHelper = ConfigurationHelperImpl.getInstance();

        ZookeeperClient zkClient = helper.getZkClientInstance(
                hbaseConf.get("hbase.zookeeper.quorum"),
                //config.getZkClusterHosts(),
                confHelper.getPropertyFromEnv(PropertyLocator.USER)
                        .orElseThrow(() -> new IllegalStateException("USER not found in configuration")),
                confHelper.getPropertyFromEnv(PropertyLocator.PASSWORD)
                        .orElseThrow(() -> new IllegalStateException("PASSWORD not found in configuration")),
                config.getZkMetadataNode());
        zkClient.init();
        return zkClient;
    }

    final static class FactoryHelper {
        ZookeeperClient getZkClientInstance(String zkCluster,
                                            String user,
                                            String pass,
                                            String zkNode) throws IOException {
            ZookeeperClient client = new ZookeeperClientBuilder(zkCluster, user, pass, zkNode).build();
            return client;
        }
    }
}
