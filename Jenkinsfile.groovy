pipeline {
    agent any
    options { 
        timeout(time: 30, unit: 'MINUTES') 
    }
    triggers {
        pollSCM('* * * * *')
    }
    stages {
        stage('git') {
            steps {
                git url: 'https://github.com/RavindraSystima/Finance-DIT-Env.git',
                branch: 'main'
            }
        }
        stage('build') {
            steps {
                dir('Finance-DIT-Env') {
                    script {
                        try {
                            sh 'npm install'
                            sh 'npm run build'
                        } catch (err) {
                            echo "Build failed, but continuing with the pipeline: ${err}"
                        }
                    }
                }
            }
        }
        stage('Artifact upload') {
            steps {
                script {
                    sh 'ls -la'
                    sh 'zip -r dist.zip dist'
                    // Assuming JFrog Artifactory credentials are already configured in Jenkins
                    // Replace 'Jfrog' with your credentialsId if it's different
                    withCredentials([usernamePassword(credentialsId: 'Jfrog', usernameVariable: 'USER', passwordVariable: 'PASS')]) {
                        sh 'curl -u $USER:$PASS -T dist.zip https://venkyorg.jfrog.io/artifactory/spc/PROD/$BUILD_NUMBER/'
                    }
                }
            }
        }
    }
}
