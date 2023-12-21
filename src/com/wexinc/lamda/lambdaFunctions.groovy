package com.wexinc.lamda

class lambdaFunctions implements Serializable {
  
   // pipeline global properties
    def steps
    def env
    def params 
    def scm
    def currentBuild
	
    lambdaFunctions(steps, env, params, scm, currentBuild) { 
        this.steps = steps
	this.env = env
        this.params = params
        this.scm = scm
        this.currentBuild = currentBuild
    }
  
    def build(){
        steps.sh "npm install"
        steps.sh "npm run build"
    }
  
    def deploy(agentName){   // read lambda functionName/zipFileName from package.json
        def packageJson = steps.readJSON file: 'package.json'
        def zipFileName = packageJson['name']
        steps.echo "${zipFileName}"
        def c = "${agentName}".split("_")
        def env = c[c.length - 1]
        def functionName = "${zipFileName}-${env}"
        steps.sh "aws lambda update-function-code \
           --function-name  ${functionName} \
           --zip-file fileb://${zipFileName}.zip"
    }
  
 
}
