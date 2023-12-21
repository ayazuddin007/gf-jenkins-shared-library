import com.wexinc.msLibs.msLibsBuild

def call(String jdkVersion) {

	def msLibs = new msLibsBuild(this, env, params, scm, currentBuild)
	
	pipeline {

		//agent any
		agent { label 'ifcs-wf-aus-dev-v3-sydney-ec2-slave'}
		
		environment {
			PATH = "/opt/maven/bin:$PATH"
		}
		
		stages {

			stage('Maven deploy'){
		        	tools { jdk "${jdkVersion}" }	
				steps {
					script {
						msLibs.build_deploy()
				     	}
				}
			}
						
}
}
}
