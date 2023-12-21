package com.wexinc.liquibase

class icpLiquibase implements Serializable {
  
   // pipeline global properties
    def steps
    def env
    def params 
    def scm
    def currentBuild
	
    icpLiquibase(steps, env, params, scm, currentBuild) { 
        this.steps = steps
	      this.env = env
        this.params = params
        this.scm = scm
        this.currentBuild = currentBuild
    }
  
    static final String orgName = 'WEX-ORG'
	
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
	    
	//command to ensure that the build is successful
	      steps.sh "mvn -s /home/ec2-user/.m2/settings.xml -U -f pom.xml clean verify -DskipTests=true"
	    
	 // Deploying maven artifact and docker image
              steps.sh "mvn -s /home/ec2-user/.m2/settings.xml -U -f pom.xml clean deploy -DskipTests=true -X"
    }
  
    def cascade(repo) {
        if (repo != null) {    //check if further cascade is required
            for (int i = 0; i < repo.size(); i++) {
                steps.build(job: "${orgName}/${repo[i]}")
            }		
        } else { steps.echo "no further builds required" }
    }
  
    
  
 
}
