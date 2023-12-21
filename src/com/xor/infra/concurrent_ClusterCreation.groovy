package com.xor.infra
import com.xor.infra.concurrent_SlaveCreation

class concurrent_ClusterCreation implements Serializable {
  
   // pipeline global properties
    def steps
    def env
    def params 
    def scm
    def currentBuild
	
    concurrent_ClusterCreation(steps, env, params, scm, currentBuild) { 
        this.steps = steps
	this.env = env
        this.params = params
        this.scm = scm
        this.currentBuild = currentBuild
    }
 
    def creation() {
         steps.dir("${params.dir2_path}"){       
	     // Writing vars into yaml
	     def amap = [
	        'slaveIP': "${params.slaveIP}",
	        'slaveName': "${params.slaveName}",
	        'clusterRepoName': "${params.clusterRepoName}",
	        'clusterRepoBranch': "${params.clusterRepoBranch}",
	        'dir2_path': "${params.dir2_path}"
	    ] 

	    // Reading yaml
	    def fileName = 'datas.yaml'
	    if(steps.fileExists("${fileName}")) { steps.sh "rm -rf ${fileName}" } 
	    steps.writeYaml file: "${fileName}", data: amap
	    def file2 = steps.readYaml file: "${fileName}"
	    steps.echo "${file2}"

	    // Create Cluster
	    steps.echo 'Creating cluster ....'
	    steps.sh "python3 cluster_creation.py"
	    steps.echo 'Cluster created successfully ....'

        }  
    }
  
    def validation() {
        steps.dir("${params.dir2_path}"){           
	    // Cluster Validation
	    if (steps.fileExists('cluster_validation.sh')) {
	         steps.echo "Checking cluster validaton ....."
	         steps.sh "chmod 744 cluster_validation.sh"
	         steps.sh 'sh cluster_validation.sh'

	        if (steps.fileExists('success_cluster.txt')) {
		     steps.echo 'Cluster validation done .....'
	        } else {
		    steps.echo 'Cluster validation failed .....'
		    steps.sh "exit 1"   // if 1 , then fail the current stage
	        }
	    } else {
	        steps.echo 'Cluster validation failed ....'
	        steps.sh "exit 1" // if 1 , then fail the current stage
	    }          
        } 
    } 
 
  
}
  
