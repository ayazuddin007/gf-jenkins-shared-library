package com.xor.features

class approval implements Serializable {
  
   // pipeline global properties
    def steps
    def env
    def params 
    def scm
    def currentBuild
	
    approval(steps, env, params, scm, currentBuild) { 
        this.steps = steps
	this.env = env
        this.params = params
        this.scm = scm
        this.currentBuild = currentBuild
    }
  
    static final String inputId = 'Deploy'
    static final String checkEnv = 'stage'
    
    def input(){
         def approverInput = steps.input(id: "${inputId}", message: 'Do you approve the build?', parameters: [[$class: 'BooleanParameterDefinition', defaultValue: true, description: '', name: 'Please confirm if you approve this build']])
         return approverInput
    }
  
 
}
