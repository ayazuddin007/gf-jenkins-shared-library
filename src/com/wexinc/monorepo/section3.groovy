package com.wexinc.monorepo
import com.wexinc.features.ansibleRepo

class section3 implements Serializable {

    // pipeline global properties
    def steps
    def env
    def params 
    def scm
    def currentBuild
	
    section3(steps, env, params, scm, currentBuild) {
        this.steps = steps
	this.env = env
        this.params = params
        this.scm = scm
        this.currentBuild = currentBuild
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
   
    def deployToCluster() {  
	def env_names = "${params.namespaces}"  
	steps.echo "${env_names}"    
	steps.sh "ansible-playbook ${ansibleRepo.ansibleRepoParentPath}/${ansibleRepo.ansibleRepoName}/dev/setup-app-demo.yml --extra-vars \"@datas-${env.BUILD_NUMBER}.yaml\" -e '{\"namespaces\":[$env_names]}'" 
    }
	
  
}
