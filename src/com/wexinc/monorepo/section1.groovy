package com.wexinc.monorepo
import com.wexinc.features.lastChanges
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
    	
    
    def build_deployDockerImage(app) { // code for build and push docker image to jfrog
 
	env.groupId = "com.wexinc.gf.rnd.ms"
	env.majorVersion = "1"
	def version = "${env.majorVersion}.${env.BUILD_NUMBER}"   

	for (int k = 0; k < lastChanges.appUniquePath.size(); k++) {
		steps.dir("${lastChanges.appUniquePath[k]}") {
			// artifact Id
			def artifactId="${lastChanges.artifacts[k]}" //artifactId name should be same as application repo name
			steps.echo "${artifactId}"
			
			// build docker image 
			env.dockerImage = "${registry_id}/${env.groupId}/${artifactId}:${version}"
			steps.sh "docker build -t ${env.dockerImage} ."

			// push to jfrog artifactory
			steps.withCredentials([steps.usernamePassword(credentialsId: "${jfrogCredentialsId}", usernameVariable: 'JFROG_USERNAME', passwordVariable: 'JFROG_PASSWORD')]) {
		            steps.sh "docker login -u ${env.JFROG_USERNAME} -p ${env.JFROG_PASSWORD} ${registry_id}"
			    steps.sh "docker push ${env.dockerImage}"   		
			}
			
			// remove docker image locally
			steps.sh "docker rmi -f ${env.dockerImage}"

			// upload zip to jfrog
			def cr_groupId = env.groupId.replace(".","/")
			def zip_src = 'kubernetes'	
			def zip_name = "${artifactId}-${version}-kubernetes-manifests"
			def zip_upload_path = "${registry_id_maven}/${cr_groupId}/${artifactId}/${version}/"
			steps.sh "cp -rf ${zip_src}/ ${artifactId}"
			    // replace container_image placeholder with value
			    steps.sh """
				    sed -i -e "s#{{container_image}}#${env.dockerImage}#" ${artifactId}/deployment.yaml
				    cat ${artifactId}/deployment.yaml
				    rm -rf kubernetes/
			    """
			steps.sh "zip -r ${zip_name}.zip ${artifactId}"
			steps.sh "jfrog rt u ${zip_name}.zip ${zip_upload_path}"
			steps.sh "rm -rf ${zip_name}.zip"
			steps.sh "rm -rf ${artifactId}"
		 }
      	  }	
    }
	
   def build_deployDockerImageforJava(app) {
	def version = ''
	for (int k = 0; k < lastChanges.appUniquePath.size(); k++) {
		// artifact Id
		def artifactId = "${lastChanges.artifacts[k]}"  //artifactId name should be same as application repo name
		steps.echo "artifactId: ${artifactId}"
		
		steps.dir("${lastChanges.appUniquePath[k]}") {  
	 		def pom = steps.readMavenPom file: "pom.xml"
	 		env.groupId = "${pom.parent.groupId}"
	 		def mv = "${pom.parent.version}".tokenize('.')[0]   	
	 		if ("${pom.parent.version}".contains('.999-SNAPSHOT')) {
	       			version = "${mv}.${env.BUILD_NUMBER}"
	 		} else {
	       			version = "${pom.parent.version}"
	 		}	
	 		steps.echo "${version}"
	 		env.majorVersion = "$mv"
	 		def maven_settings = "/home/ec2-user/.m2/settings.xml"
			
			// Change the version in all the modules from parent pom
			steps.sh "mvn -s ${maven_settings} -f ../pom.xml versions:set -DnewVersion=${version}"
			
			// Sonarqube Analysis i.e (code quality checking)
// 			def appType = 'Monorepo'
// 			sonarqube.analyis(maven_settings, app, appType)   
// 			sonarqube.qualityGate()
			
			// Deploying maven artifact and docker image
			steps.sh "mvn -s ${maven_settings} -U -f ../pom.xml deploy -DskipTests=true"
			
			// remove docker image locally
			env.dockerImage = "${registry_id}/${env.groupId}/${artifactId}:${version}"
			steps.sh "docker rmi -f ${env.dockerImage}"
			
		}
	}	
	
   }
	
   def cascade(controlRepos, app) {
	steps.echo "${app}"
	steps.echo "${controlRepos}"
	steps.echo "${lastChanges.artifacts}"
	for (int m = 0; m < controlRepos.size(); m++) { 
	    for (int n = 0; n < lastChanges.artifacts.size(); n++) {
	        steps.build(job: "${orgName}/${controlRepos[m]}", parameters: [
			steps.string(name: 'controlRepos', value: "${controlRepos[m]}" ),
			steps.string(name: 'groupId', value: "${env.groupId}"), 
			steps.string(name: 'artifactId', value: "${lastChanges.artifacts[n]}"), 
			steps.string(name: 'version', value: "${env.majorVersion}" ), 
			steps.string(name: 'section1_build_number', value: "${env.BUILD_NUMBER}" ),
			steps.string(name: 'dockerImage', value: "${env.dockerImage}" ),
			steps.string(name: 'credentialsId', value: "${credentialsId}" ),
			steps.string(name: 'registry_id_maven', value: "${registry_id_maven}" ),  
			steps.string(name: 'orgName', value: "${orgName}" )  
	        ])
	    }	     
	}  	
    }	
   
	

    	
}
