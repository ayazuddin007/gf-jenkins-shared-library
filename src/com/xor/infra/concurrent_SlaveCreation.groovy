package com.xor.infra

class concurrent_SlaveCreation implements Serializable {
  
   // pipeline global properties
    def steps
    def env
    def params 
    def scm
    def currentBuild
	
    concurrent_SlaveCreation(steps, env, params, scm, currentBuild) { 
        this.steps = steps
	this.env = env
        this.params = params
        this.scm = scm
        this.currentBuild = currentBuild
    }
  
    static final String credentialsId = 'GitHub_Admin'
    static final String orgName = 'WEX-ORG'	
  
    def creation(clusterRepo) {
        def dir1_path = "${env.WORKSPACE}"
        steps.dir("${dir1_path}"){
	     //Create Slave
             steps.echo 'Creating slave .....'
             steps.sh 'python3 ./slave_creation/createSlave.py' 
             steps.echo 'Slave created and attached to Master successfully ....'
	}
	steps.dir("${dir1_path}/slave_creation"){
	    steps.sh 'touch outfile'
	    steps.sh 'terraform output | cut -d "=" -f 2 | awk \'{ print substr( $0, 3, length($0)-3 ) }\' > outfile'
	    env.slaveIP = steps.readFile 'outfile'
	    steps.sh "echo ${slaveIP}"
	    env.slaveName = steps.sh(script: "cat slave_extra_vars.json | jq -r '.NODE_NAME'", returnStdout: true).trim()
	    steps.sh "echo ${env.slaveName}"
	}
	return env.slaveName
    }
  
    def validation(clusterRepo) {
  	(env.clusterRepoName, env.clusterRepoBranch) = "${clusterRepo}".tokenize( '/' )
        env.dir2_path = "/home/ec2-user/${env.clusterRepoName}"
        steps.dir("${env.dir2_path}") {   
	    // Github Checkout
	    def GitHub_Url = "https://github.com/xor/${env.clusterRepoName}.git"
	    def GitHub_Branch = "${env.clusterRepoBranch}"
	    steps.git branch: "${GitHub_Branch}", credentialsId: "${credentialsId}", url: "${GitHub_Url}" 

	   // Slave Validation
	   if (steps.fileExists('slave_validation.sh')) {
	       steps.echo "Checking slave validation ....."
	       steps.sh "chmod 744 slave_validation.sh"
	       steps.sh 'sh slave_validation.sh'

	       if (steps.fileExists('success_slave.txt')) {
		     steps.echo 'Slave validation done .....'
		     steps.sh "exit 0"    // if 0 , then execute the next stage
	        } else {
		     steps.echo 'Slave validation failed ....'
		     steps.sh "exit 1" // if 1 , then fail the current stage
	        }
	    } else {
	         steps.echo 'Slave validation failed ....'
	         steps.sh "exit 1" // if 1 , then fail the current stage
	    }
        } 
    } 
  
    def cascade() {
         build job: "${orgName}/${clusterRepo}", parameters: [
	     steps.string(name: 'slaveIP', value: "${env.slaveIP}" ), 
	     steps.string(name: 'slaveName', value: "${env.slaveName}" ),
	     steps.string(name: 'clusterRepoName', value: "${env.clusterRepoName}"),
	     steps.string(name: 'clusterRepoBranch', value: "${env.clusterRepoBranch}" ),
	     steps.string(name: 'dir2_path', value: "${env.dir2_path}" )
        ])
    } 
	
	
 
}
