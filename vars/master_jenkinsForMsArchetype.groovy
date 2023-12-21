import com.xor.archetype.archetype

def call(String jdkVersion) {

	def archetype = new archetype(this, env, params, scm, currentBuild)
	
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
						archetype.build_deploy()
				     	}
				}
			}
				
			
			
			
}
}
}
