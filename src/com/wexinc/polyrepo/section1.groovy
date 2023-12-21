package com.wexinc.polyrepo
import com.wexinc.features.sonarqube

class section1 implements Serializable {

    // pipeline global properties
    def steps
    def env
    def params 
    def scm
    def currentBuild
    def sonarqube
	
   section1(steps, env, params, scm, currentBuild) {
	this.steps = steps
	this.env = env
        this.params = params
        this.scm = scm
        this.currentBuild = currentBuild
	this.sonarqube = new sonarqube(steps, env, params, scm, currentBuild)
    }
	
   
    static final String credentialsId = 'GitHub_Admin'
    static final String jfrogCredentialsId = 'XW-Jfrog'	
    static final String registry_id = "xoriant-docker.eu-central-1.artifactory.wexapps.com"
    static final String registry_id_maven = "xoriant-maven"
    static final String orgName = 'WEX-ORG'	
	
    	
    
    def build_deployDockerImage() {
        // Reading pom.xml values
        def pom = steps.readMavenPom file: 'pom.xml'
        env.groupId_pom = "${pom.groupId}"
        env.artifactId_pom = "${pom.artifactId}"
        
        // Condition for artifact version updates ; if it contains '.999-SNAPSHOT' strip it and append build number ; else no change 
        def mv = "${pom.version}".tokenize('.')[0]

	def version_pom = ''   	
        if ("${pom.version}".contains('.999-SNAPSHOT')) {
            version_pom = "${mv}.${env.BUILD_NUMBER}"
        } else {
            version_pom = "${pom.version}"
        }	
        steps.echo "${version_pom}"

        env.majorVersion = "$mv"
        
        // Condition for batch framework
    	if ("${env.artifactId_pom}" == "IFCSFramework") {
	      env.artifactId_pom = "ifcs-batch-framework"
        }	
        steps.echo "${env.artifactId_pom}"

	def maven_settings = ''
        if ("${env.artifactId_pom}" == "ifcs-web-ols-ui" || "${env.artifactId_pom}" == "ifcs-web-ajaxswing-angularui") {
              maven_settings = "/home/ec2-user/.m2/settings-with-npm.xml"  
        } else {
              maven_settings = "/home/ec2-user/.m2/settings.xml"
        }	
      
        // Change the version in all the modules 
	steps.sh "mvn -s ${maven_settings} versions:set -DnewVersion=${version_pom}"
      
        // Sonarqube Analysis i.e (code quality checking)
	def app = 'Java'
	def appType = 'Polyrepo'    
	sonarqube.analyis(maven_settings, app, appType)   
	sonarqube.qualityGate()    
        
        // Deploying maven artifact and docker image
        steps.sh "mvn -s ${maven_settings} -U -f pom.xml deploy -DskipTests=true"
      
        // Removing Docker image locally
        def dockerImage = "${registry_id}/${env.groupId_pom}/${env.artifactId_pom}:${version_pom}"
        steps.sh "docker rmi -f ${dockerImage}"
    }
  
    def cascade(controlRepos) {
        for (int i = 0; i < controlRepos.size(); i++) {
	     steps.build(job: "${orgName}/${controlRepos[i]}", parameters: [
		   steps.string(name: 'controlRepos', value: "${controlRepos[i]}"),
                   steps.string(name: 'groupId', value: "${env.groupId_pom}"), 
	           steps.string(name: 'artifactId', value: "${env.artifactId_pom}"), 
                   steps.string(name: 'version', value: "${env.majorVersion}"), 
                   steps.string(name: 'section1_build_number', value: "${env.BUILD_NUMBER}"),
                   steps.string(name: 'credentialsId', value: "${credentialsId}" ),
	           steps.string(name: 'registry_id_maven', value: "${registry_id_maven}"),
	           steps.string(name: 'orgName', value: "${orgName}"),
		   steps.string(name: 'jiraBranch', value: "${env.GIT_BRANCH}") 
            ])
        }			  
    }

}
