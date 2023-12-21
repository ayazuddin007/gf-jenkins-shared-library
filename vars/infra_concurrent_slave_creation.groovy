import com.wexinc.infra.concurrent_SlaveCreation

def call(String clusterRepo)
{
	def slave = new concurrent_SlaveCreation(this, env, params, scm, currentBuild)
	
	pipeline {
		
		stages {
   
			stage('Slave Creation') {
			      agent { label "master" }		
		              steps {
				    script {
					  def slaveName = slave.creation(clusterRepo)
				    }
			      }
			}
      
			stage('Slave Validation') {
			      agent { label "${slaveName}" }
			      environment { PATH = "/opt/maven/bin:$PATH" } 	
			      steps {
				   script {
					  slave.validation(clusterRepo)
				   }
		              }
			}
			
			stage('Cascade to Cluster') {
			      agent { label "${slaveName}" }	
			      steps {
				   script {
					  slave.cascade()
				   }
		              }
			}
      
			
			
}
}
}
