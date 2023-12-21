import com.xor.xorWebCoreV3.xorWebCoreV3Build

def call(String jdkVersion, List xorRepos) {

	def xorWebCoreV3 = new xorWebCoreV3Build(this, env, params, scm, currentBuild)

	pipeline {

		//agent any
		agent { label 'xor-zen-dev-frankfurt-ec2-slave'}
		
		environment {
			PATH = "/opt/maven/bin:$PATH"
		}
		
		stages {

			stage('Maven deploy'){
		        	tools { jdk "${jdkVersion}" }	
				steps {
					script {
						xorWebCoreV3.build_deploy()
				     	}
				}
			}
				
			stage('Cascade to xor-server build') {
				steps {
					script {
						xorWebCoreV3.cascade(xorRepos)
					}	
				}
			}
			
			
}
}
}
