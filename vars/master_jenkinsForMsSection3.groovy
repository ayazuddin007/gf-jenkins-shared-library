import com.wexinc.polyrepo.section3
import com.wexinc.features.ansibleRepo
import com.wexinc.features.jira
import com.wexinc.features.gchatNotification
	
def call(String clusterName, String slaveName) {
	
	def section3 = new section3(this, env, params, scm, currentBuild)
	def ansibleRepo = new ansibleRepo(this, env, params, scm, currentBuild)
	def jira = new jira(this, env, params, scm, currentBuild)
	def gchatNotification = new gchatNotification(this, env, params, scm, currentBuild)
		
	pipeline {
	
		    parameters {
			string(name: 'controlRepos', description: '' )
			string(name: 'cr_groupId', description: '')
			string(name: 'artifactId', description: '')
			string(name: 'version', description: '')
			string(name: 'registryId', description: '' )
			string(name: 'section1_build_number', description: '')
			string(name: 'section2_build_number', description:'' )
			string(name: 'namespaces', description:'' )
			string(name: 'credentialsId', description: '')
			string(name: 'orgName', description: '') 
			string(name: 'jiraBranch', description: '')    
		    }
  
    		    agent { label "${slaveName}"}
   
		    environment {
			PATH = "/opt/maven/bin:$PATH"
		    } 
    
    		    stages {
		    	
			stage('Create Yaml'){
				steps {
					script {
						section3.createYamlFile(clusterName)
				     	}
				}
			}
			    
			stage('Deploy to cluster'){
				steps {
					script {
						ansibleRepo.ansibleRepoCheckout()
						section3.deployToCluster(clusterName, slaveName)
						ansibleRepo.ansibleRepoRemove()
				     	}
				} 
				post  { 
					always { 
						script { 
							jira.deploy() 
						}
					}
					success {
						script {	
							gchatNotification.success()
						}
					}
					failure {
						script {	
							gchatNotification.failure()
						}
					}
					cleanup { 
						script { 
							cleanWs()
						}
					}
				}
			}   
			  
}	
}
}  
