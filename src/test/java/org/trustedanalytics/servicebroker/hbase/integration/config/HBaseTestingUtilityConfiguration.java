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
package org.trustedanalytics.servicebroker.hbase.integration.config;

import org.apache.hadoop.hbase.HBaseConfiguration;
import org.apache.hadoop.hbase.HBaseTestingUtility;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class HBaseTestingUtilityConfiguration {

  @Autowired
  private org.apache.hadoop.conf.Configuration hbaseConf;

  @Bean(destroyMethod = "shutdownMiniCluster")
  public HBaseTestingUtility getHBaseTestingUtility() throws Exception {
    org.apache.hadoop.conf.Configuration configuration = HBaseConfiguration.create(hbaseConf);
    configuration.set("fs.hdfs.impl",
        "org.trustedanalytics.servicebroker.hbase.integration.config.FakeFS");
    HBaseTestingUtility utility = new HBaseTestingUtility(configuration);
    utility.startMiniCluster();
    return utility;
  }
}
