import com.xor.liquibase.icpLiquibase

def call(String jdkVersion, List repo = null) {

	def icpLiquibase = new icpLiquibase(this, env, params, scm, currentBuild)

	pipeline {

		//agent any
		agent { label 'xor-wfe-dev-frankfurt-ec2-slave'}
		
		environment {
			PATH = "/opt/maven/bin:$PATH"
		}
		
		stages {

			stage('Maven deploy'){
		        	tools { jdk "${jdkVersion}" }	
				steps {
					script {
						icpLiquibase.build_deploy()
				     	}
				}
			}
				
			stage('Cascade') {
				steps {
					script {
						icpLiquibase.cascade(repo)
					}	
				}
			}
			
			
}
}
}
