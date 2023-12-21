# gf-jenkins-shared-library

Jenkins Pipeline Shared Library Template
---
This project is intended for use with [Jenkins](https://jenkins.io/) and Global Pipeline Libraries through the 
[Pipeline Shared Groovy Libraries Plugin](https://wiki.jenkins.io/display/JENKINS/Pipeline+Shared+Groovy+Libraries+Plugin).

A common scenario when developing Jenkins [declarative pipelines](https://jenkins.io/doc/book/pipeline/syntax/), is
to bundle common custom pipeline tasks in a shared library so that all Jenkins pipeline configurations in the WEX organisation
can leverage from them without the need to reimplement the same logic.

This project provides a project template for developing shared Jenkins pipeline libraries as specified in the Jenkins
[documentation](https://jenkins.io/doc/book/pipeline/shared-libraries/).

Requirements
---
[Apache Groovy](http://groovy-lang.org/)

Structure
---
The directory structure of a Shared Library repository is as follows:
    
      
    ├── src                                         (define the custom method)
    │   |__ com
    |       |__ wexinc
    |           |__ polyrepo
    │           |   |__ section1.groovy        
    |           |   |__ section2.groovy
    |           |   |__ section3.groovy
    |           |
    |           |__ monorepo
    |           |   ...
    |           |   ...
    |           |   ...
    |           |__ features
    |               ...
    |               ...
    |               ...
    |__ vars                                        (call the custom method by creating the object)
    |   |__ master_jenkinsForMsSection1.groovy     
    |   |__ master_jenkinsForMsSection2.groovy
    |   |__ master_jenkinsForMsSection3.groovy
    |       ...
    |       ...
    |       ...
    |
    |__ Jenkinsfile
    
  
The **src** directory should look like standard Java source directory structure. This directory is added to the classpath when executing Pipelines.
    
The **vars** directory hosts script files that are exposed as a variable in Pipelines. The name of the file is the name of the variable in the Pipeline. The basename of each .groovy file should be a Groovy (~ Java) identifier, conventionally camelCased. 

The **Jenkinsfile** is used to include this repo inside the Jenkins Organization Scan.


Groovy List
---
This repo has the following list of groovy script :

|Sr. No|Name|Type|Description|
|------|----|-------|-----------|
|01|master_jenkinsForMsSection1.groovy|Polyrepo|Upload maven build & deploy docker image to JFrog|
|02|master_jenkinsForMsSection2.groovy|Polyrepo|Upload product manifest repo zip to JFrog|
|03|master_jenkinsForMsSection3.groovy|Polyrepo|Deployment|
|04|build_section.groovy|Monorepo|Upload maven build & deploy docker image to JFrog|
|05|product_manifest_build.groovy|Monorepo|Upload product manifest repo zip to JFrog|
|06|deploy_section.groovy|Monorepo|Deployment|
|07|infra_concurrent_slave_creation.groovy|Infra|Creation of Slave|
|08|infra_concurrent_cluster_creation.groovy|Infra|Creation of Cluster|
|09|intergration_test.groovy|Testing|Integration Test|
|10|master_jenkinsForIfcsWebCoreV3Build.groovy|ifcsWebCoreV3|IFCS Web Core V3|
|11|master_jenkinsForIcpLiquibase.groovy|Liquibase|Liquibase|
|12|master_jenkinsForSAMDeploy.groovy|SAM|SAM Deployment|
|13|master_jenkinsforMsLibsBuild.groovy|MsLibs|Libraries|
|14|master_jenkinsForMsArchetype.groovy|Archetype|Archetype|
|15|master_jenkinsForLambdaFunctions.groovy|Lamda|Lambda|
|16|master_jenkinsForMsSection1_naf.groovy|Polyrepo|Upload maven build & deploy docker image to JFrog for NAF|
|17|master_jenkinsForMsSection2_naf.groovy|Polyrepo|Upload product manifest repo zip to JFrog for NAF|
|18|master_jenkinsForMsSection3_naf.groovy|Polyrepo|Deployment for NAF|

Parameters Required for Groovy Script
---
We have listed the following groovy scripts with their parameters.

## 1) master_jenkinsForMsSection1.groovy 
   Run the maven build and upload the artifact on jfrog.


### Inputs

| Name          | Desription          | Type    |
| ------------- | ------------- | -------- |
| jdk   | Java version 8 or 11          | String  |
| controlRepos  |Product manifest repo name/branch| List  |

## 2) master_jenkinsForMsSection2.groovy
  Clone the product manifest repo in which version.json is present and from there checks the applications to be deployed.
  
  
### Inputs
| Name          | Desription          | Type    |
| ------------- | ------------- | -------- |
|controlRepos| Environment repo name/branch | List |
|groupId |Application pom.xml group_id | String |
|artifactId | Application pom.xml artifact id | String |
|version | Application pom.xml version | String |
|section1_build_number| From section1 job build no. | String |
|jiraBranch | Github feature/bugfix branch | String |
|jiraSite | Jira url | String |
|jiraProjectKey | Jira project key | String |
|jiraProjectKeyArray | Jira project key Array | Array |
|jiraIssueKeys | Jira Issue Key | String |
|jiraEnvType | Jira Environment | String |
|orgName | Organisation Name | String |


## 3) master_jenkinsForMsSection3.groovy
   deploy the application on our environment using ansible. 
### Inputs
| Name          | Desription          | Type    |
| ------------- | ------------- | -------- |
|slavename|Name of the slave| String |
|controlRepos| Environment repo name/branch | List |
|cr_groupId|control repo group id|String|
|artifactId|Application pom.xml artifact id|String|
|version|Application pom.xml version|String|
|section1_build_number|From section1 job build no.|String|
|registryId | Registry id where the binaries reside in jfrog | String |
|section2_build_number | From section2 job build no. | String |
|namespaces | list of all the namespaces where the deployment is to be done |List|
|jiraBranch | Github feature/bugfix branch | String |
|jiraSite | Jira url | String |
|jiraProjectKey | Jira project key | String |
|jiraProjectKeyArray | Jira project key Array | Array |
|jiraIssueKeys | Jira Issue Key | String |
|jiraEnvType | Jira Environment | String |


## 4) build_section.groovy 
   Run the maven build and upload the artifact on jfrog for monorepo.


### Inputs

| Name          | Desription          | Type    |
| ------------- | ------------- | -------- |
| controlRepos  |Product manifest repo name/branch| List  |

## 5) product_manifest_build.groovy
  Clone the product manifest repo in which version.json is present and from there checks the applications to be deployed for monorepo.
  
  
### Inputs
| Name          | Desription          | Type    |
| ------------- | ------------- | -------- |
|controlRepos| Environment repo name/branch | List |
|groupId |Application pom.xml group_id | String |
|artifactId | Application pom.xml artifact id | String |
|version | Application pom.xml version | String |
|section1_build_number| From section1 job build no. | String |
|dockerImage | Docker Image | String |


## 6) deploy_section.groovy
   deploy the application on our environment using ansible for monorepo. 
### Inputs
| Name          | Desription          | Type    |
| ------------- | ------------- | -------- |
|clusterName|Name of the cluster| String |
|slaveName|Name of the slave| String |
|controlRepos| Environment repo name/branch | List |
|cr_groupId|control repo group id|String|
|artifactId|Application pom.xml artifact id|String|
|version|Application pom.xml version|String|
|section1_build_number|From section1 job build no.|String|
|registryId | Registry id where the binaries reside in jfrog | String |
|section2_build_number | From section2 job build no. | String |
|namespaces | list of all the namespaces where the deployment is to be done |List|
|dockerImage | Docker Image | String |

## 7) infra_concurrent_slave_creation.groovy
  Create a new slave

### Inputs
| Name          | Desription          | Type    |
| ------------- | ------------- | -------- |
| clusterRepo  |List of repo name | List  |

## 8) infra_concurrent_cluster_creation.groovy
  Create a new cluster

### Inputs
| Name          | Desription          | Type    |
| ------------- | ------------- | -------- |
| slaveIP   | Slave IP  | String  |
| slaveName   | Slave Name  | String  |
| clusterRepoName   | Cluster Repo Name  | String  |
| clusterRepoBranch   | Cluster Repo Branch  | String  |
| dir2_path   | Directory Path  | String  |

## 9) intergration_test.groovy
  Performing a integration test

### Inputs
| Name          | Desription          | Type    |
| ------------- | ------------- | -------- |
| clusterName  |Cluster Name | String |
| slaveName  |Slave Name | String  |
| integrationTestRepo  |List of repo name | List |
| jdkVersion  |Java version 8 or 11 | String |

## 10) master_jenkinsForIfcsWebCoreV3Build.groovy
   Build IFCS Web Core V3

### Inputs
| Name          | Desription          | Type    |
| ------------- | ------------- | -------- |
| jdk  | Java version 8 or 11           | String  |
| IFCSRepos  |IFCS Repo name | List  |


## 11) master_jenkinsForIcpLiquibase.groovy 
  Build and upload liquibase DB Utility to jfrog

### Inputs
| Name          | Desription          | Type    |
| ------------- | ------------- | -------- |
| jdk  | Java version 8 or 11           | String  |
| Repo  |List of repo name | List  |

## 12) master_jenkinsForSAMDeploy.groovy
  Deploy serverless application model

### Inputs
| Name          | Desription          | Type    |
| ------------- | ------------- | -------- |
| agentName  | Agent Name  | String  |
| envStage  | Env Name | String  |


## 13) master_jenkinsforMsLibsBuild.groovy 
  Build micro-service library jars and upload it to jfrog

### Inputs
| Name          | Desription          | Type    |
| ------------- | ------------- | -------- |
| jdk   | Java version 8 or 11           | String  |


## 14) master_jenkinsForMsArchetype.groovy
  Build micro-service library jars and upload it to jfrog

### Inputs
| Name          | Desription          | Type    |
| ------------- | ------------- | -------- |
| jdk   | Java version 8 or 11           | String  |

## 15) master_jenkinsForLambdaFunctions.groovy
  Perform a Lambda operation

### Inputs
| Name          | Desription          | Type    |
| ------------- | ------------- | -------- |
| agentName   | Agent Name| String  |


## 16) master_jenkinsForMsSection1_naf.groovy 
   Run the maven build and upload the artifact on jfrog for NAF.


### Inputs

| Name          | Desription          | Type    |
| ------------- | ------------- | -------- |
| jdk   | Java version 8 or 11          | String  |
| controlRepos  |Product manifest repo name/branch| List  |

## 17) master_jenkinsForMsSection2_naf.groovy
  Clone the product manifest repo in which version.json is present and from there checks the applications to be deployed  for NAF.
  
  
### Inputs
| Name          | Desription          | Type    |
| ------------- | ------------- | -------- |
|controlRepos| Environment repo name/branch | List |
|groupId |Application pom.xml group_id | String |
|artifactId | Application pom.xml artifact id | String |
|version | Application pom.xml version | String |
|section1_build_number| From section1 job build no. | String |
|jiraBranch | Github feature/bugfix branch | String |
|jiraSite | Jira url | String |
|jiraProjectKey | Jira project key | String |
|jiraProjectKeyArray | Jira project key Array | Array |
|jiraIssueKeys | Jira Issue Key | String |
|jiraEnvType | Jira Environment | String |
|orgName | Organisation Name | String |


## 18) master_jenkinsForMsSection3_naf.groovy
   deploy the application on our environment using ansible for NAF. 
### Inputs
| Name          | Desription          | Type    |
| ------------- | ------------- | -------- |
|slavename|Name of the slave| String |
|controlRepos| Environment repo name/branch | List |
|cr_groupId|control repo group id|String|
|artifactId|Application pom.xml artifact id|String|
|version|Application pom.xml version|String|
|section1_build_number|From section1 job build no.|String|
|registryId | Registry id where the binaries reside in jfrog | String |
|section2_build_number | From section2 job build no. | String |
|namespaces | list of all the namespaces where the deployment is to be done |List|
|jiraBranch | Github feature/bugfix branch | String |
|jiraSite | Jira url | String |
|jiraProjectKey | Jira project key | String |
|jiraProjectKeyArray | Jira project key Array | Array |
|jiraIssueKeys | Jira Issue Key | String |
|jiraEnvType | Jira Environment | String |

