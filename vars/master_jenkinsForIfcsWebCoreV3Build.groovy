import com.wexinc.ifcsWebCoreV3.ifcsWebCoreV3Build

def call(String jdkVersion, List ifcsRepos) {

	def ifcsWebCoreV3 = new ifcsWebCoreV3Build(this, env, params, scm, currentBuild)

	pipeline {

		//agent any
		agent { label 'ifcs-zen-dev-frankfurt-ec2-slave'}
		
		environment {
			PATH = "/opt/maven/bin:$PATH"
		}
		
		stages {

			stage('Maven deploy'){
		        	tools { jdk "${jdkVersion}" }	
				steps {
					script {
						ifcsWebCoreV3.build_deploy()
				     	}
				}
			}
				
			stage('Cascade to ifcs-server build') {
				steps {
					script {
						ifcsWebCoreV3.cascade(ifcsRepos)
					}	
				}
			}
			
			
}
}
}
