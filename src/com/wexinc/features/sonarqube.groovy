package com.wexinc.features

class sonarqube implements Serializable {
  
   // pipeline global properties
    def steps
    def env
    def params 
    def scm
    def currentBuild
	
    sonarqube(steps, env, params, scm, currentBuild) { 
        this.steps = steps
	this.env = env
        this.params = params
        this.scm = scm
        this.currentBuild = currentBuild
    }
  
  
    def analyis(maven_settings, app, appType) { 
         // Static code analysis and send the analysis-report to sonarqube server
         steps.withSonarQubeEnv('Sonarqube-Server') {
	     steps.echo "${app}"
	     def pom_xml_path = '' 	 
	     if (app == "Java") {
		     if(appType == "Polyrepo") {
		     	 pom_xml_path = 'pom.xml'
		     } else { 
			 pom_xml_path = '../pom.xml' 
		     }    
		 steps.sh "mvn -s ${maven_settings} -U -f ${pom_xml_path} clean package -DskipTests=true sonar:sonar"
	     } else {  
                 steps.sh "mvn sonar:sonar"
	     }
         } 
    }

    def qualityGate() { 
        // Check whether static code analysis pass the quality gate or not, If not then pipeline will abort
	steps.timeout(time: 1, unit: 'HOURS') {
             steps.waitForQualityGate abortPipeline: true
	}
    }
  
  
  
}
