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

import javax.validation.constraints.NotNull;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;

@Configuration
public class ExternalConfiguration {

    @Value("${metadata.imageUrl}")
    @NotNull
    private String imageUrl;

    @Value("${zk.cluster}")
    @NotNull
    private String zkClusterHosts;

    @Value("${zk.metadatanode}")
    @NotNull
    private String zkMetadataNode;

    @Value("${hbase.brokerusername}")
    @NotNull
    private String brokerUserName;

    @Value("${hbase.brokeruserpass}")
    @NotNull
    private String brokerUserPassword;

    @Value("${hbase.provided.params}")
    private String hbaseProvidedParams;

    @Value("${kerberos.kdc}")
    @NotNull
    private String kerberosKdc;

    @Value("${kerberos.realm}")
    @NotNull
    private String kerberosRealm;

    @Value("${cf.servicename}")
    @NotNull
    private String cfServiceName;

    @Value("${cf.serviceid}")
    @NotNull
    private String cfServiceId;

    @Value("${cf.baseId}")
    @NotNull
    private String cfBaseId;

    public String getBrokerUserName() {
        return brokerUserName;
    }

    public void setBrokerUserName(String brokerUserName) {
        this.brokerUserName = brokerUserName;
    }

    public String getKerberosKdc() {
        return kerberosKdc;
    }

    public void setKerberosKdc(String kerberosKdc) {
        this.kerberosKdc = kerberosKdc;
    }

    public String getKerberosRealm() {
        return kerberosRealm;
    }

    public void setKerberosRealm(String kerberosRealm) {
        this.kerberosRealm = kerberosRealm;
    }

    public String getCfServiceName() {
        return cfServiceName;
    }

    public void setCfServiceName(String cfServiceName) {
        this.cfServiceName = cfServiceName;
    }

    public String getCfServiceId() {
        return cfServiceId;
    }

    public void setCfServiceId(String cfServiceId) {
        this.cfServiceId = cfServiceId;
    }

    public String getBrokerUserPassword() {
        return brokerUserPassword;
    }

    public void setBrokerUserPassword(String brokerUserPassword) {
        this.brokerUserPassword = brokerUserPassword;
    }

    public String getZkClusterHosts() {
        return zkClusterHosts;
    }

    public void setZkClusterHosts(String zkClusterHosts) {
        this.zkClusterHosts = zkClusterHosts;
    }

    public String getZkMetadataNode() {
        return zkMetadataNode;
    }

    public void setZkMetadataNode(String zkMetadataNode) {
        this.zkMetadataNode = zkMetadataNode;
    }

    public String getHBaseProvidedParams() {
        return hbaseProvidedParams;
    }

    public void setHBaseProvidedParams(String hbaseProvidedParams) {
        this.hbaseProvidedParams = hbaseProvidedParams;
    }

    public String getCfBaseId() {
        return cfBaseId;
    }

    public void setCfBaseId(String cfBaseId) {
        this.cfBaseId = cfBaseId;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }
}
