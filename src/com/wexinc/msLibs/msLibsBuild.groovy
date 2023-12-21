package com.wexinc.msLibs

class msLibsBuild implements Serializable {
  
   // pipeline global properties
    def steps
    def env
    def params 
    def scm
    def currentBuild
	
    msLibsBuild(steps, env, params, scm, currentBuild) { 
        this.steps = steps
	this.env = env
        this.params = params
        this.scm = scm
        this.currentBuild = currentBuild
    }
	
    def build_deploy() {
        // Reading pom.xml values
        def pom = steps.readMavenPom file: 'pom.xml'
        def groupId_pom = "${pom.groupId}"
        def artifactId_pom = "${pom.artifactId}"
        
        // Condition for artifact version updates ; if it contains '.999-SNAPSHOT' strip it and append build number ; else no change 
        def mv = "${pom.version}".tokenize('.')[0]
        def version_pom = "${mv}.${env.BUILD_NUMBER}"
	def majorVersion = "$mv"
        def maven_settings = "/home/ec2-user/.m2/settings.xml"
       
        // Change the version in all the modules 
	steps.sh "mvn -s ${maven_settings} versions:set -DnewVersion=${version_pom}"
	    
	// Deploying maven artifact and docker image
        steps.sh "mvn -s ${maven_settings} -U -f pom.xml deploy -DskipTests=true"
    }
  
    
  
 
}
