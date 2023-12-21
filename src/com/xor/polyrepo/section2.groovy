package com.xor.polyrepo

class section2 implements Serializable {

    // pipeline global properties
    def steps
    def env
    def params 
    def scm
    def currentBuild
	
    section2(steps, env, params, scm, currentBuild) {
	this.steps = steps
	this.env = env
        this.params = params
        this.scm = scm
        this.currentBuild = currentBuild
    }

    static final String cr_groupId = 'com.xor.gf.xor.run.app.wfa'
    public static namespaces = []
  
    def gitCheckout() {  
        def (githubRepoName, githubRepoBranch) = "${params.controlRepos}".tokenize( '/' )
	steps.git(branch: "${githubRepoBranch}", credentialsId: "${params.credentialsId}", url: "https://github.com/xor/${githubRepoName}.git")    
    }
  
    def scan_uploadNamespaceZip() {
        def artifact_id = "${params.groupId}:${params.artifactId}"
        steps.echo "${artifact_id}"   
        steps.dir(env.WORKSPACE) {  	
             env.cr_groupId = "${cr_groupId}".replace(".","/")
             steps.echo "${env.cr_groupId}" 
             def files = steps.findFiles()
             files.each{ f -> 
                  steps.echo "This is directory: ${f.name}" 
                  steps.dir("${f.name}") {
                      def fileName = 'versions.json'	 
                      if (steps.fileExists("${fileName}")) {
			  steps.echo 'Yes version file exists'
                          def ns = steps.readJSON file: "${fileName}"
                          if (ns["${artifact_id}"] != null) { 
                              steps.echo "${ns["$artifact_id"]}"
                              def zip_name = "${f.name}-${env.BUILD_NUMBER}"
                              def zip_upload_path = "${params.registry_id_maven}/${env.cr_groupId}/${f.name}/${params.version}.${env.BUILD_NUMBER}/"
                              steps.sh "zip -r ../${zip_name}.zip ../${f.name}"
                              steps.sh "jfrog rt u ../${zip_name}.zip $zip_upload_path"
                              steps.sh "rm -rf ../${zip_name}.zip"
                              //steps.namespaces.add("${f.name}") 
                              steps.echo "Just before adding namespace ${f.name}" 
                              namespaces += "${f.name}" 
                              steps.echo "Just after adding namespace ${namespaces}"
                          }
                     } else { steps.echo "No version file exists" }
                 }    
            }
        }  
        steps.echo "Namespace is ${namespaces}"    
    }
  
  
    def cascade(EnvRepo) {  
         for (int j = 0; j < EnvRepo.size(); j++) {
              steps.build(job: "${params.orgName}/${EnvRepo[j]}", parameters: [
                   steps.string(name: 'controlRepos', value: "${params.controlRepos}" ),
                   steps.string(name: 'cr_groupId', value: "${env.cr_groupId}" ), 
                   steps.string(name: 'artifactId', value: "${params.artifactId}" ), 				  			      
                   steps.string(name: 'registryId', value: "${params.registry_id_maven}" ),
                   steps.string(name: 'version', value: "${params.version}" ), 
                   steps.string(name: 'section1_build_number', value: "${params.section1_build_number}" ),
                   steps.string(name: 'section2_build_number', value: "${env.BUILD_NUMBER}" ),
                   steps.string(name: 'namespaces', value: "${namespaces}"),
                   steps.string(name: 'credentialsId', value: "${params.credentialsId}" ), 
                   steps.string(name: 'orgName', value: "${params.orgName}" ),
		   steps.string(name: 'jiraBranch', value: "${params.jiraBranch}")    
              ])
         }
    }
    
    

}
