import com.xor.sam.samDeploy

def call(String agentName) {

	def samDeploy = new samDeploy(this, env, params, scm, currentBuild)

	pipeline {

		//agent any
		agent { label "${agentName}"}
		
		environment {
			PATH = "/usr/bin/npm:$PATH"
		}
		
		stages {

			stage('Install Packages'){
				steps {
					script {
						samDeploy.install()
				     	}
				}
			}
				
			stage('Deploy') {
				steps {
					script {
						samDeploy.deploy(agentName)
					}	
				}
			}
			
			
}
}
}
