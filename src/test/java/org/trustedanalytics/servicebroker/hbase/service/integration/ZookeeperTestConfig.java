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

import org.trustedanalytics.cfbroker.store.zookeeper.service.ZookeeperClient;
import org.trustedanalytics.cfbroker.store.zookeeper.service.ZookeeperClientBuilder;
import org.trustedanalytics.servicebroker.hbase.config.ExternalConfiguration;
import org.trustedanalytics.servicebroker.hbase.config.Profiles;
import org.apache.curator.framework.CuratorFramework;
import org.apache.curator.framework.CuratorFrameworkFactory;
import org.apache.curator.retry.RetryOneTime;
import org.apache.hadoop.hbase.HBaseTestingUtility;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;

import java.io.IOException;

@Profile(Profiles.INTEGRATION_TESTS)
@Configuration
public class ZookeeperTestConfig {

    @Autowired
    private HBaseTestingUtility utility;

    @Autowired
    private ExternalConfiguration config;

    private FactoryHelper helper;

    public ZookeeperTestConfig() {
        System.out.println("Running ZookeeperTestConfig constructor");
        this.helper = new FactoryHelper();
    }

    @Bean
    public ZookeeperClient getZKClient() throws Exception {

        String zkClusterHosts = "localhost:" + utility.getZkCluster().getClientPort();
        createDir(zkClusterHosts, config.getZkMetadataNode());
        ZookeeperClient zkClient = helper.getZkClientInstance(
                zkClusterHosts,
                config.getBrokerUserName(),
                config.getBrokerUserPassword(),
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

    private void createDir(String connectionString, String path) throws Exception {

        CuratorFramework tempClient = getNewTempClient(connectionString);

        tempClient.create()
                .creatingParentsIfNeeded()
                .forPath(path);

        tempClient.close();
    }

    private static CuratorFramework getNewTempClient(String connectionString) {
        CuratorFramework tempClient = CuratorFrameworkFactory.builder()
                .connectString(connectionString)
                .retryPolicy(new RetryOneTime(100))
                .build();
        tempClient.start();
        return tempClient;
    }

}

