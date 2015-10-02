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

import org.apache.hadoop.hbase.security.User;
import org.apache.hadoop.hbase.security.UserProvider;
import org.apache.hadoop.security.UserGroupInformation;
import org.trustedanalytics.hadoop.config.ConfigurationHelper;
import org.trustedanalytics.hadoop.config.ConfigurationHelperImpl;
import org.trustedanalytics.hadoop.config.PropertyLocator;
import org.trustedanalytics.hadoop.kerberos.KrbLoginManager;
import org.trustedanalytics.hadoop.kerberos.KrbLoginManagerFactory;

import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.hbase.HBaseConfiguration;
import org.apache.hadoop.hbase.client.Admin;
import org.apache.hadoop.hbase.client.Connection;
import org.apache.hadoop.hbase.client.ConnectionFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Profile;

import java.io.IOException;
import java.net.URISyntaxException;

import javax.security.auth.Subject;
import javax.security.auth.login.LoginException;

@Profile(Profiles.CLOUD)
@org.springframework.context.annotation.Configuration
public class HbaseConfiguration {

    private final static Logger LOGGER = LoggerFactory.getLogger(HbaseConfiguration.class);

    private final static String AUTHENTICATION_METHOD = "kerberos";

    private final static String AUTHENTICATION_METHOD_PROPERTY = "hbase.security.authentication";

    //hbase.zookeeper.quorum

    @Autowired
    private ExternalConfiguration configuration;

    @Autowired
    private Configuration hbaseConf;

    @Bean(destroyMethod="close")
    public Admin getHBaseAdmin() throws InterruptedException,
            URISyntaxException, LoginException, IOException {

        if(AUTHENTICATION_METHOD.equals(hbaseConf.get(AUTHENTICATION_METHOD_PROPERTY))) {
            LOGGER.info("Creating hbase client with kerberos support");
            return getSecuredHBaseClient();
        } else {
            LOGGER.info("Creating hbase client without kerberos support");
            return getUnsecuredHBaseClient();
        }
    }

    /**
     * TODO: This method instead of configuration.getBrokerUserName() should have something like :
     * OAuthTicket.getUserName()
     */
    private Admin getUnsecuredHBaseClient() throws InterruptedException,
            URISyntaxException, LoginException, IOException {

        Configuration conf = HBaseConfiguration.create(hbaseConf);
        Connection connection = ConnectionFactory.createConnection(conf);
        return connection.getAdmin();
    }

    private Admin getSecuredHBaseClient() throws InterruptedException,
            URISyntaxException, LoginException, IOException {
        ConfigurationHelper confHelper = ConfigurationHelperImpl.getInstance();
        LOGGER.info("Trying to authenticate");
        KrbLoginManager loginManager =
                KrbLoginManagerFactory.getInstance().getKrbLoginManagerInstance(
                    confHelper.getPropertyFromEnv(PropertyLocator.KRB_KDC)
                        .orElseThrow(() -> new IllegalStateException("KRB_KDC not found in configuration")),
                    confHelper.getPropertyFromEnv(PropertyLocator.KRB_REALM)
                        .orElseThrow(() -> new IllegalStateException(
                            "KRB_REALM not found in configuration")));
        Subject subject =
            loginManager.loginWithCredentials(confHelper.getPropertyFromEnv(PropertyLocator.USER)
                .orElseThrow(() -> new IllegalStateException("USER not found in configuration")),
            confHelper.getPropertyFromEnv(PropertyLocator.PASSWORD)
                .orElseThrow(() -> new IllegalStateException("PASSWORD not found in configuration"))
                .toCharArray());
        loginManager.loginInHadoop(subject, hbaseConf);
        Configuration conf = HBaseConfiguration.create(hbaseConf);
        User user = UserProvider.instantiate(conf)
            .create(UserGroupInformation.getUGIFromSubject(subject));
        Connection connection = ConnectionFactory.createConnection(conf, user);
        return connection.getAdmin();
    }

}
