import com.wexinc.naf.section2

def call(List EnvRepo) {

	def section2 = new section2(this, env, params, scm, currentBuild)

	pipeline {
		
		parameters {
		    	string(name: 'controlRepos', description: '')
			string(name: 'groupId', description: '')
			string(name: 'artifactId', description: '')
			string(name: 'version', description: '')
			string(name: 'section1_build_number', description: '')
			string(name: 'credentialsId', description: '')
			string(name: 'registry_id_maven', description: '')
			string(name: 'orgName', description: '')
			string(name: 'jiraBranch', description: '')
		}
		
		//agent any
    		agent { label 'wfe-poc-frankfurt-ec2-slave'}
		
		options { skipDefaultCheckout() }
		
		environment {
			PATH = "/opt/maven/bin:$PATH"
		}
		
		stages {
    
			stage('Git Checkout') {
		              steps {
				    script {
					  section2.gitCheckout()
				    }
			      }
			}
      
			stage('Scan and upload namespace zip folder to jfrog') {
			      steps {
				   script {
					  section2.scan_uploadNamespaceZip()
					  cleanWs()
				   }
		              }
			}
      
			stage('Cascade to section3') {
			      steps {
				   script {
					  section2.cascade(EnvRepo)
				   }
			      }
			}
			
}
}
}
