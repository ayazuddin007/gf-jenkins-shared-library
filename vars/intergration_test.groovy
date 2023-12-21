import com.xor.testing.integrationTest

def call(String clusterName, String slaveName, String integrationTestRepo, String jdkVersion)
{

	def integrationTest = new integrationTest(this, env, params, scm, currentBuild)

	pipeline {
		
    		agent { label "${slaveName}"}
		
		environment {
			PATH = "/opt/maven/bin:$PATH"
		}
		
		stages {
    
			stage('Integration Test') {
			      tools { jdk "${jdkVersion}" }		
		              steps {
				    script {
					  integrationTest.execute(integrationTestRepo)
				    }
			      }
			}
      
			stage('Allure Report') {
			      steps {
				   script {
					  integrationTest.allureReport(integrationTestRepo)
				   }
		              }
			}
      
			
			
}
}
}
