import com.wexinc.monorepo.section1
import com.wexinc.features.lastChanges

def call(String jdkVersion = null, List controlRepos) {

	def section1 = new section1(this, env, params, scm, currentBuild)
	def lastChanges = new lastChanges(this, env, params, scm, currentBuild)
	def app
	if (jdkVersion == null) { app = 'Other' } else { app = 'Java'}

	pipeline {

		//agent any
		agent { label 'wfe-poc-frankfurt-ec2-slave'}
		
		environment {
			PATH = "/opt/maven/bin:$PATH"
		}
		
		stages {
			
			stage('Build and Deploy Image to JFrog'){
				steps {
					script {
						echo "App: ${app}"
						if (app == 'Java') {
							def jdk = tool name: "${jdkVersion}"
							lastChanges.getAppUniquePathforJava()
							section1.build_deployDockerImageforJava(app)
						} else {
							lastChanges.getAppUniquePath()
							section1.build_deployDockerImage(app)
						} 
					       cleanWs()
				     	}
				}
			}
			
			stage('Cascade to section2') {
				steps {
					script {
						section1.cascade(controlRepos, app)
					}	
				}
			}
			
			
}
}
}
