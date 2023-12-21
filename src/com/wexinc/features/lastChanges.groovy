package com.wexinc.features

class lastChanges implements Serializable {
  
   // pipeline global properties
    def steps
    def env
    def params 
    def scm
    def currentBuild
	
    lastChanges(steps, env, params, scm, currentBuild) {
        this.steps = steps
	this.env = env
        this.params = params
        this.scm = scm
        this.currentBuild = currentBuild
    }

    public static appUniquePath = []
    public static artifacts = []
  
    def getAppUniquePath() {
    	    // code for all dockerfile exist app list
          steps.sh '''
              find * -type f -name Dockerfile | awk -F "/*[^/]*/*$" '{ print ($1 == "" ? "." : $1); }' | sort | uniq >  dockerfilePath.txt 
          '''
          def dockerfilePath = steps.readFile("dockerfilePath.txt").readLines()
          steps.echo "${dockerfilePath}"

          // code for updated app list
          steps.sh '''
              git diff --name-only HEAD~1 | awk -F "/*[^/]*/*$" '{ print ($1 == "" ? "." : $1); }' | sort | uniq > change.txt
          '''
	  def appPathList = steps.readFile("change.txt").readLines()
          steps.echo "appPathList: ${appPathList}"

          // code for unique app list
          for (int i = 0; i < dockerfilePath.size(); i++) {
              if("${appPathList}".contains("${dockerfilePath[i]}")) {
                appUniquePath += "${dockerfilePath[i]}"
              }
          }
          steps.echo "appUniquePath: ${appUniquePath}"

          // code for app name
          for (int j = 0; j < appUniquePath.size(); j++) {
	      def dirName = "${appUniquePath[j]}/kubernetes"
	      def checkDirExist = steps.sh(script: "test -d ${dirName} && echo '1' || echo '0' ", returnStdout: true).trim()
	      if("${checkDirExist}"=='1'){
		  def fileName = 'service.yaml'
		  steps.dir("${dirName}"){
		      if(steps.fileExists("${fileName}")) {
			  def datas = steps.readYaml file: "${fileName}"
			  artifacts += "${datas.metadata.name}"
		      }
		  }
	      } else { steps.echo "kubernetes directory not exist...."}
              steps.echo "${artifacts[j]}"
          }
          steps.echo "artifacts: ${artifacts}"
     } 	
	
	
       def getAppUniquePathforJava() {
    	    // code for all dockerfile exist app list
          steps.sh '''
              find * -type f -name Dockerfile | awk -F "/*[^/]*/*$" '{ print ($1 == "" ? "." : $1); }' | sort | uniq >  change_dockerfile.txt 
          '''
          def change_dockerfile = steps.readFile("change_dockerfile.txt").readLines()
          steps.echo "${change_dockerfile}"
	  
	  def dockerfilePath = []
	  for (int z = 0; z < change_dockerfile.size(); z++) {
                dockerfilePath += "${change_dockerfile[z]}".tokenize('/')[0]
          }
	  steps.echo "dockerfilePath: ${dockerfilePath}"     

          // code for updated app list
          steps.sh '''
              git diff --name-only HEAD~1 | awk -F "/*[^/]*/*$" '{ print ($1 == "" ? "." : $1); }' | sort | uniq > change.txt
          '''
          def appPathList = steps.readFile("change.txt").readLines()
          steps.echo "appPathList: ${appPathList}"

          // code for unique app list
          for (int i = 0; i < dockerfilePath.size(); i++) {
              if("${appPathList}".contains("${dockerfilePath[i]}")) {
                appUniquePath += "${dockerfilePath[i]}"
              }
          }
          steps.echo "appUniquePath: ${appUniquePath}"

          // code for app name
          for (int j = 0; j < appUniquePath.size(); j++) {
		 def fileName = "${appUniquePath[j]}/pom.xml"
		 if(steps.fileExists("${fileName}")) {
			   def pom = steps.readMavenPom file: "${fileName}"
			   artifacts += "${pom.artifactId}"
		  } else { steps.echo "pom.xml not exist...."}
              steps.echo "${artifacts[j]}"
          }
	    
          steps.echo "artifacts: ${artifacts}"
     } 	
   

}
