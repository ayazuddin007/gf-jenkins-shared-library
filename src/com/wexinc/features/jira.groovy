package com.wexinc.features
import com.wexinc.config.constant

class jira implements Serializable {
  
   // pipeline global properties
    def steps
    def env
    def params 
    def scm
    def currentBuild
	
    jira(steps, env, params, scm, currentBuild) { 
        this.steps = steps
	this.env = env
        this.params = params
        this.scm = scm
        this.currentBuild = currentBuild
    }
  
    //static final String jiraProjectKeyArray = ['PIP', 'ACDC']
    static final String jiraSite = 'wexinc.atlassian.net'
    //static final String jiraEnvType = 'development' //staging, testing, production, development
     
    def build() {
	 env.jiraBranch = "${env.GIT_BRANCH}"   
	// Checking branch contains slash or not
	if("${env.jiraBranch}".contains('/')) {
            def (v1, v2) = "${env.jiraBranch}".tokenize('/')
            def jiraIssueKeys = v2.substring(0,v2.lastIndexOf("_"))
            def jiraProjectKey = jiraIssueKeys.tokenize('-')[0] 
            // Checking jiraProjectKey is present in jiraProjectKeyArray
            if("${constant.jiraProjectKeyArray}".contains("${jiraProjectKey}")) {
                // Checking jira taskId is present or not in the branch
                if ("${env.jiraBranch}".contains("${jiraProjectKey}")) {
                      steps.jiraSendBuildInfo branch: "${env.jiraBranch}", site: "${jiraSite}"
                }	
            }
	} 
	else {
	    def jiraProjectKey = "Null"
	    def jiraIssueKeys = "Null"
	}
    }
  
    def deploy() {
	//def environmentName = 'wf-euro-dev3'    
	def environmentName = "${params.namespaces}".toString().replace("[", "").replace("]", "") 
	steps.echo "${params.jiraBranch}"    
        // Checking branch contains slash or not
      	if("${params.jiraBranch}".contains('/')) {
	     def (v1, v2) = "${env.jiraBranch}".tokenize('/')
             def jiraIssueKeys = v2.substring(0,v2.lastIndexOf("_"))
             def jiraProjectKey = jiraIssueKeys.tokenize('-')[0] 	
	      // Checking jiraProjectKey is present in jiraProjectKeyArray
	      if("${constant.jiraProjectKeyArray}".contains("${jiraProjectKey}")) {
	       	    // Checking jira taskId is present or not in the branch
	           if ("${params.jiraBranch}".contains("${jiraProjectKey}")) {
		        steps.jiraSendDeploymentInfo environmentId: "${environmentName}", environmentName: "${environmentName}", environmentType: "${constant.jiraEnvType}", issueKeys: ["${jiraIssueKeys}"], site: "${jiraSite}"
	           }
	      }
      	 }
    }
   
  
  
  
  
  
}
