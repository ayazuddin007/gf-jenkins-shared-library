package com.xor.naf
import com.xor.features.ansibleRepo
import com.xor.features.emailNotification
import com.xor.features.approval

class section3 implements Serializable {

    // pipeline global properties
    def steps
    def env
    def params 
    def scm
    def currentBuild	
    def emailNotification
    def approval
	
    section3(steps, env, params, scm, currentBuild) {
        this.steps = steps
	this.env = env
        this.params = params
        this.scm = scm
        this.currentBuild = currentBuild
	this.emailNotification = new emailNotification(steps, env, params, scm, currentBuild)
	this.approval = new approval(steps, env, params, scm, currentBuild)
    }
	
    def createYamlFile(clusterName) {  
	def amap = [
	    'ns-controlRepo': "${params.controlRepos}",
             'Cr_groupId': "${params.cr_groupId}",
	     'majorVersion': "${params.version}",
	     'artifact_id': "${params.artifactId}",
	     'registry_id': "${params.registryId}",
	     's1_build_number': "${params.section1_build_number}",
	     's2_build_number': "${params.section2_build_number}",
	     's3_build_number': "${env.BUILD_NUMBER}",
	     'clusterName': "${clusterName}",
	      'branch': "${env.GIT_BRANCH}"
	]
	steps.writeYaml file: "datas-${env.BUILD_NUMBER}.yaml", data: amap
        def file2 = steps.readYaml file: "datas-${env.BUILD_NUMBER}.yaml"
        steps.echo "${file2}"
    }
   
    def deployToCluster(clusterName, slaveName) {  
        def envName = "${params.namespaces}".toString().replace("[", "").replace("]", "").toUpperCase()
	if("${clusterName}".contains("${approval.checkEnv}") || "${slaveName}".contains("${approval.checkEnv}")) {  // code for deployment on prod Env with approval gate
           steps.timeout(time: 1, unit: 'HOURS')  { // time out for 1 hr, if no approval then pipeline will abort
              steps.waitUntil {
		// code for email notification to approver
		emailNotification.sendEmail(envName) 
		// code for approval
		def approverInput = approval.input()
		steps.echo 'approverInput: ' + approverInput 
		if(approverInput == true || approverInput == null) {  //code for deployment on prod Env
		    this.deployment()
		    steps.echo "Deployed to ${envName} successfully......"
		} else {	
		    steps.echo "Action was aborted."  // abort the pipeline
		}    
		return true 
	      }
	   }		
	} else {     
		// code for deployment on non-prod Env (i.e. dev, qa, uat)
		this.deployment()
		steps.echo "Deployed to ${envName} successfully......"
	}
    }
	
    def deployment() {  
	def env_names = "${params.namespaces}"  
	steps.echo "${env_names}"    
	steps.sh "ansible-playbook ${ansibleRepo.ansibleRepoParentPath}/${ansibleRepo.ansibleRepoName}/dev/setup-app-demo.yml --extra-vars \"@datas-${env.BUILD_NUMBER}.yaml\" -e '{\"namespaces\":[$env_names]}'" 
    }	
	
  
}

