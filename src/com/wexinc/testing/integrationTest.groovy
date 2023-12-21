package com.wexinc.testing

class integrationTest implements Serializable {
  
   // pipeline global properties
    def steps
    def env
    def params 
    def scm
    def currentBuild
	
    integrationTest(steps, env, params, scm, currentBuild) { 
        this.steps = steps
	this.env = env
        this.params = params
        this.scm = scm
        this.currentBuild = currentBuild
    }
  
    static final String credentialsId = 'GitHub_Admin'
    
    def execute(integrationTestRepo) {
        def (repoName, repoBranch) = "${integrationTestRepo}".tokenize( '/' )
	steps.dir("${repoName}"){
	    // Git checkout
	    steps.git branch: "${repoBranch}", credentialsId: "${credentialsId}", url: "https://github.com/wexinc/${repoName}.git"

	    // Reading pom.xml values
	    def pom = steps.readMavenPom file: 'pom.xml'
	    def artifactId_pom = "$pom.artifactId"

	    // Allure variables
	    def maven_settings = "/home/ec2-user/.m2/settings.xml"	
	    def allureProjectDir = '/home/ec2-user/allure-efs-mount/'
	    env.allureResultDir = "${allureProjectDir}${artifactId_pom}"
	    	
	    //Read active profile name from application.properties
	    def filePath = 'src/test/resources/environments'
	    def fileName = 'application.properties'
	    def file = "${filePath}/${fileName}"
	    def props = steps.readProperties(file: "${file}")
	    def activeProfileName = "${props}".toString().replace("[", "").replace("]", "").tokenize(':')[-1].replaceAll("\\}", " ")  
	    steps.echo "${activeProfileName}"   

	    // if exception, then execute the next stage 
	    try {
		steps.sh "mvn -s ${maven_settings} -U -f pom.xml clean test -Dspring.profiles.active=${activeProfileName}"
	    } 
	    catch(Exception e) {
	       steps.catchError(buildResult: 'FAILURE', stageResult: 'FAILURE') { steps.sh "exit 1" }
	    }

	}
    }
	 
    def allureReport(integrationTestRepo) {
        def (repoName, repoBranch) = "${integrationTestRepo}".tokenize( '/' )
	steps.dir("${repoName}"){
	   // checking allureResultDir exist or not
	   def res = steps.sh(script: "test -d ${env.allureResultDir} && echo '1' || echo '0' ", returnStdout: true).trim()
	   if(res=='0'){
		    steps.echo 'not exist'	
		    steps.sh "mkdir ${env.allureResultDir}"
	    }
	    steps.sh "mv  target/allure-results target/results"
	    steps.sh "cp -r target/results ${env.allureResultDir}"
        }
    }
  
 
}
