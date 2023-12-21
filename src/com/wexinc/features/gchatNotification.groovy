package com.wexinc.features

class gchatNotification implements Serializable {
  
   // pipeline global properties
    def steps
    def env
    def params 
    def scm
    def currentBuild
	
    gchatNotification(steps, env, params, scm, currentBuild) { 
        this.steps = steps
	this.env = env
        this.params = params
        this.scm = scm
        this.currentBuild = currentBuild
    }
  
  
    static final String jiraProjectKeyArray = ['PIP', 'ACDC']
  
    def getClient() {
	// read env-vars.yaml file and fetch clientID
        def file1 = steps.readYaml file: "env-vars.yaml"
	def client = "${file1["clientID"]}"
        steps.echo "${client}"
	return "${client}"    
    }
	
    def success() {
	 def env_names = "${params.namespaces}"  
	 def client = this.getClient()
	 try {
	      steps.withCredentials([steps.string(credentialsId: "${client}", variable: 'WEBHOOK_URL')]) {
	           steps.googlechatnotification(
			  url: "${env.WEBHOOK_URL}", 
			  message: "Upgraded version to ${env_names.replaceAll("[\\[\\]]","")}-${params.version}.${env.BUILD_NUMBER}\nDeployment completed successfully for ${env_names.replaceAll("[\\[\\]]","")}-${params.version}.${env.BUILD_NUMBER} in ${currentBuild.durationString.minus(' and counting')}\nCheck Deployment Logs: ${env.BUILD_URL}", 
			  notifySuccess: 'true', 
			  sameThreadNotification: 'true' 
	           )
	      }
         } catch(Exception e) {
	      steps.echo "client token id doesn't exist" 
	 }   
    }
	
    def failure() {
	 def env_names = "${params.namespaces}"
	 def client = this.getClient()   
	 try {
	      steps.withCredentials([steps.string(credentialsId: "${client}", variable: 'WEBHOOK_URL')]) {
	           steps.googlechatnotification(
			 url: "${env.WEBHOOK_URL}", 
			 message: "Deployment failed for ${env_names.replaceAll("[\\[\\]]","")}-${params.version}.${env.BUILD_NUMBER}\n\nCheck Deployment Logs: ${env.BUILD_URL}", 
			 notifyFailure: 'true', 
			 sameThreadNotification: 'true'
	           )
	      }
         } catch(Exception e) {
	      steps.echo "client token id doesn't exist" 
	 }     
    }	
	

}
