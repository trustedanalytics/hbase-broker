hbase-broker
================
Cloud foundry broker for HBase.

# How to use it?
To use hbase-broker, you need to build it from sources configure, deploy, create instance and bind it to your app. Follow steps described below. 

## Build 
Run command for compile and package.: 
```
mvn clean package
```

## Deploy 
Push broker binary code to cloud foundry (use cf client).:
```
cf push hbase-broker -p target/hbase-broker-*.jar -m 512M -i 1 --no-start
```

## Configure
For strict separation of config from code (twelve-factor principle), configuration must be placed in environment variables.
 
Broker configuration params list (environment properties):
* obligatory
  * USER_PASSWORD - password to interact with service broker
* obligatory only when hbase requires kerberos authentication:
  * KRB_KDC_HOST - kerberos kdc host address
  * KRB_REALM - kerberos realm name
* optional :
  * BASE_GUID - base id for catalog plan creation (uuid)
  * HADOOP_PROVIDED_PARAMS - list of hbase configuration parameters exposed by service (json format, default: {})
  * CF_CATALOG_SERVICENAME - service name in cloud foundry catalog (default: hbase)
  * CF_CATALOG_SERVICEID - service id in cloud foundry catalog (default: hbase)
  * ZK_BRK_SPACE - (default: /zkbrk_space)

For instance.:
```
cf se hbase-broker ZKCLUSTER_URL 10.10.9.145:2181,10.10.9.146:2181
```

When hbase requires kerberos authentication set:
```
cf se hbase-broker KRB_KDC_HOST ip-10-10-9-198.us-west-2.compute.internal
cf se hbase-broker KRB_REALM US-WEST-2.COMPUTE.INTERNAL
```

## Start  service broker application

Use cf client :
```
cf start  hbase-broker
```
## Create new service instance 
  
Use cf client : 
```
cf create-service-broker hbase-broker <user> <password> https://hbase-broker.<platform_domain>
cf enable-service-access hbase
cf cs hbase shared  hbase-instance
```

## Binding broker instance

Broker instance can be bind with cf client :
```
cf bs <app> hbase-instance
```
or by configuration in app's manifest.yml : 
```yaml
  services:
    - hbase-instance
```

To check if broker instance is bound, use cf client : 
```
cf env <app>
```
and look for : 
```yaml
  "hbase": [
   {
    "credentials": {
      "org.trustedanalytics.hadoop":{
        "dfs.ha.namenodes.nameservice1":"namenode8,namenode189",
        ...
      },
     "kerberos": {
      "kdc": "ip-10-10-9-198.us-west-2.compute.internal",
      "krealm": "US-WEST-2.COMPUTE.INTERNAL"
     },
     
    },
    
    "label": "hbase",
    "name": "hbase-instance",
    "plan": "shared",
    "tags": []
   }
  ]
```
in VCAP_SERVICES.
