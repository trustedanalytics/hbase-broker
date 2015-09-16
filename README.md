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

## Kerberos configuration
Broker automatically bind to an existing kerberos provide service. This will provide default kerberos configuration, for REALM and KDC host. Before deploy check:

- if kerberos service does not exists in your space, you can create it with command:
```
cf cups kerberos-service -p '{ "kdc": "kdc-host", "kpassword": "kerberos-password", "krealm": "kerberos-realm", "kuser": "kerberos-user" }'
```

- if kerberos-service exists in your space, you can update it with command:
```
cf uups kerberos-service -p '{ "kdc": "kdc-host", "kpassword": "kerberos-password", "krealm": "kerberos-realm", "kuser": "kerberos-user" }'
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
  * HADOOP_PROVIDED_PARAMS - list of hbase configuration parameters exposed by service (json format, default: {})
* optional :
  * BASE_GUID - base id for catalog plan creation (uuid)
  * CF_CATALOG_SERVICENAME - service name in cloud foundry catalog (default: hbase)
  * CF_CATALOG_SERVICEID - service id in cloud foundry catalog (default: hbase)
  * ZK_BRK_SPACE - (default: /broker)

For instance.:
```
cf se hbase-broker ZKCLUSTER_URL 10.10.9.145:2181,10.10.9.146:2181
```

### Injection of HBASE client configuration
HBASE client configuration must be set via HADOOP_PROVIDED_PARAMS environment variable. Param `hbase.zookeeper.quorum` is required. List of hadoop configuration has to be proper json form:

```json
    {"HADOOP_CONFIG_KEY":
        {
            "hbase.zookeeper.quorum":"host1,host2,host3,...",
            "property1.name":"property1.value",
            "property2.name":"property2.value",
            ...
        }
    }

```
You can prepare this configuration manually and use cf client,  

```
cf se hbase-broker HADOOP_PROVIDED_PARAMS "{\"HADOOP_CONFIG_KEY\": {\"hbase.zookeeper.quorum\": \"cdh-master-0.node.gotapaaseu.consul,cdh-master-1.node.gotapaaseu.consul,cdh-master-2.node.gotapaaseu.consul\"}}"
```
what is rather annoying. To facilitate the HADOOP_PROVIDED_PARAMS settings, you can use **import_hadoop_conf.sh** script available in admin tool kit https://github.com/trustedanalytics/hadoop-admin-tools. There are several ways to use of this util:

Getting hadoop configuration directly from CDH manager.
```
./import_hadoop_conf.sh -cu http://<cloudera_manager_host_name>:7180/cmf/services/4/client-config | xargs -0 cf se hbase-broker HADOOP_PROVIDED_PARAMS
```

Getting hadoop configuration from local archive.
```
./import_hadoop_conf.sh -cu file://path/client-config.zip | xargs -0 cf se hbase-broker HADOOP_PROVIDED_PARAMS
```

Getting hadoop configuration from stdin.
```
cat /path/client-config.zip | ./import_hadoop_conf.sh | xargs -0 cf se hbase-broker HADOOP_PROVIDED_PARAMS
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
