import com.xor.lamda.lambdaFunctions

def call(String agentName) {

	def lambdaFunctions = new lambdaFunctions(this, env, params, scm, currentBuild)

	pipeline {

		//agent any
		agent { label "${agentName}"}
		
		environment {
			PATH = "/usr/bin/npm:$PATH"
		}
		
		stages {

			stage('Build'){
				steps {
					script {
						lambdaFunctions.build()
				     	}
				}
			}
				
			stage('Deploy') {
				steps {
					script {
						lambdaFunctions.deploy(agentName)
					}	
				}
			}
			
			
}
}
}
