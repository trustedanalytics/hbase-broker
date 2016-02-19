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

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;

import javax.validation.constraints.NotNull;

@Configuration
public class ExternalConfiguration {

  @Value("${hbase.provided.zip}")
  private String hbaseProvidedZip;

  @Value("${store.path}")
  @NotNull
  private String brokerStorePath;

  @Value("${cf.servicename}")
  @NotNull
  private String cfServiceName;

  @Value("${cf.serviceid}")
  @NotNull
  private String cfServiceId;

  @Value("${cf.baseId}")
  @NotNull
  private String cfBaseId;

  @Value("${metadata.imageUrl}")
  @NotNull
  private String imageUrl;

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

  public String getHBaseProvidedZip() {
    return hbaseProvidedZip;
  }

  public void setHBaseProvidedZip(String hbaseProvidedParams) {
    this.hbaseProvidedZip = hbaseProvidedParams;
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

  public String getBrokerStorePath() {
    return brokerStorePath;
  }

  public void setBrokerStorePath(String brokerStorePath) {
    this.brokerStorePath = brokerStorePath;
  }
}
