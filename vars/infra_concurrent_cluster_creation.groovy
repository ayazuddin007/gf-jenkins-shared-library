import com.wexinc.infra.concurrent_ClusterCreation

def call()
{
	def cluster = new concurrent_ClusterCreation(this, env, params, scm, currentBuild)
	
	pipeline {
		
		parameters {
			string(name: 'slaveIP', description: '' )
			string(name: 'slaveName', description: '')
			string(name: 'clusterRepoName', description: '')
			string(name: 'clusterRepoBranch', description: '')
			string(name: 'dir2_path', description: '')
		}
		
		agent { label "${params.slaveName}" }
		
		options { skipDefaultCheckout() }
		
		stages {
   
			stage('Cluster Creation') {
		              steps {
				    script {
					  cluster.creation()
				    }
			      }
			}
      
			stage('Cluster Validation') { 	
			      steps {
				   script {
					  cluster.validation()
				   }
		              }
			      post { 
				   cleanup { 
					  script { 
						cleanWs()
					  }
				    }
			      }	
			}
			
			
}
}
}
