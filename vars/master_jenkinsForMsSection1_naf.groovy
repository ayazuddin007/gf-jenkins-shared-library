import com.xor.naf.section1
import com.xor.features.jira

def call(String jdkVersion, List controlRepos) {

	def section1 = new section1(this, env, params, scm, currentBuild)
	def jira = new jira(this, env, params, scm, currentBuild)

	pipeline {

		//agent any
		 agent { label 'wfe-poc-frankfurt-ec2-slave'}
		//agent { label 'xor-wfe-dev-frankfurt-ec2-slave'}
		
		environment {
			PATH = "/opt/maven/bin:$PATH"
		}
		
		stages {

			stage('Maven deploy'){
		        	tools { jdk "${jdkVersion}" }	
				steps {
					script {
						section1.build_deployDockerImage()
						cleanWs()
				     	}
				}
				post  { 
					always { 
						script { 
							jira.build()  
						}
					}	
				}
			}
				
			stage('Cascade to section2') {
				steps {
					script {
						section1.cascade(controlRepos)
					}	
				}
			}
			
			
}
}
}
