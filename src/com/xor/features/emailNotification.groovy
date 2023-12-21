package com.xor.features
import com.xor.config.constant

class emailNotification implements Serializable {
  
   // pipeline global properties
    def steps
    def env
    def params 
    def scm
    def currentBuild
	
    emailNotification(steps, env, params, scm, currentBuild) { 
        this.steps = steps
	this.env = env
        this.params = params
        this.scm = scm
        this.currentBuild = currentBuild
    }
  
    //static final String email_to = 'swapna.paleti@xor.com'
  
    def sendEmail(envName){ 
	def url = "${env.BUILD_URL}input"    
	def subject = "${envName} approval request for build #${env.BUILD_NUMBER}"
	def body = "Please click on a below link to proceed or Abort. <br/><br/><b>LINK:-</b> ${url}<br/><br/><b>BUILD_NUMBER:-</b> ${env.BUILD_NUMBER}"
	steps.emailext body: "${body}", mimeType: 'text/html', to: "${constant.email_to}", subject: "${subject}"
    }
 
	
}
