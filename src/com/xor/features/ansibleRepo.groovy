package com.xor.features
import com.xor.monorepo.section1

class ansibleRepo implements Serializable {
  
   // pipeline global properties
    def steps
    def env
    def params 
    def scm
    def currentBuild
	
    ansibleRepo(steps, env, params, scm, currentBuild) {
        this.steps = steps
	this.env = env
        this.params = params
        this.scm = scm
        this.currentBuild = currentBuild
    }

    static final String ansibleRepoName = 'gf-xor-utils-ci-ansible-scripts'
    static final String ansibleRepoBranch = 'master'
    static final String ansibleRepoParentPath = '/home/ec2-user/artifact_ansible_script'
    
    def ansibleRepoCheckout() {
        //check ansible repo exist or not
        def ansibleRepoParentPathExist = steps.sh(script: "test -d  ${ansibleRepoParentPath} && echo '1' || echo '0' ", returnStdout: true).trim()
        if("${ansibleRepoParentPathExist}"=='0'){ //if not
          steps.sh "mkdir ${ansibleRepoParentPath}"
        } 
        steps.dir("${ansibleRepoParentPath}"){
            def ansibleRepoExist = steps.sh(script: "test -d  ${ansibleRepoName} && echo '1' || echo '0' ", returnStdout: true).trim()
            if("${ansibleRepoExist}"=='0'){// if not
                steps.withCredentials([steps.usernamePassword(credentialsId: "${section1.credentialsId}", usernameVariable: 'GIT_USERNAME', passwordVariable: 'GIT_PASSWORD')]) {
                    steps.sh "git clone -b ${ansibleRepoBranch} https://${env.GIT_USERNAME}:${env.GIT_PASSWORD}@github.com/xor/${ansibleRepoName}.git"
                }
            }  	
        }  
    } 
  
    def ansibleRepoRemove() {
	      steps.sh "rm -rf ${ansibleRepoParentPath}*"
    } 
    
}
