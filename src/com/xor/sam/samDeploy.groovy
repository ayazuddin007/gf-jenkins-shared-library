package com.xor.sam

class samDeploy implements Serializable {
  
   // pipeline global properties
    def steps
    def env
    def params 
    def scm
    def currentBuild
	
    samDeploy(steps, env, params, scm, currentBuild) { 
        this.steps = steps
	this.env = env
        this.params = params
        this.scm = scm
        this.currentBuild = currentBuild
    }
  
    def install(){
        steps.sh "npm install"
    }
  
    def deploy(agentName){
        def c = "${agentName}".split("-")
        def env = c[c.length - 1]
        steps.sh "serverless deploy --stage ${env}"
        //steps.sh "serverless deploy --stage test"
    }
  
 
}
