pipeline {
    agent any
    
    // tools{
    //     maven "maven3.6.3"    //maven installed in jenkins server, so not required to use this
    // }
    
    triggers {
        cron('* * * * *') // Runs every minute (Build Periodically), Build trigger periodically Irrespective of code pushing
    }

    
    environment {
        // Define your SonarQube server in this block
        SONARQUBE_SERVER = 'Sonarqube_server'  
    }

    stages {
        stage('clone') {
            steps {
                git credentialsId: 'github_creds', url: 'https://github.com/maheswarreddyva/maven-web-app.git'
            }
        }
        stage('build') {
            steps {
                sh '''
                date
                mvn clean package
                '''
            }
        }
        stage('sonar-scan') {
            steps {
                script {
                    withSonarQubeEnv(SONARQUBE_SERVER) {
                        // Only perform SonarQube analysis after the build is done
                        sh "mvn sonar:sonar"
                    }
                }
            }
        }
        stage('Quality Gate') {
            steps {
                script {
                    // Timeout set to 1 hour for waiting for the quality gate status
                    timeout(time: 1, unit: 'HOURS') {
                        def qualityGate = waitForQualityGate()
                        if (qualityGate.status != 'OK') {
                            error("Quality Gate failed: ${qualityGate.status}")
                        }
                    }
                }
            }
        }
        stage('Store Artifacts in Nexus Repository') {
            steps {
                // Deploy the build artifact to Nexus repository
                withCredentials([usernamePassword(credentialsId: 'nexus', passwordVariable: 'passwd', usernameVariable: 'uname')]) {
                    sh """
                    mvn deploy
                    """
                }
            }
        }
        stage('Deploy the application into the tomcat') {
            steps {
                sshagent(['ssh-login']) {
                    sh """
                        # Copy the .jar file to the EC2 instance
                        scp -o StrictHostKeyChecking=no target/*.war ec2-user@172.31.80.221:/home/ec2-user/
                        
                        # SSH into the EC2 instance and move the .jar file to the Tomcat 'webapps' directory
                        ssh -o StrictHostKeyChecking=no ec2-user@172.31.80.221 '
                            sudo mv /home/ec2-user/*.war /usr/local/tomcat10/webapps/
                        '
                    """
                }
            }
        }

    }
    

    post {
        success {
            echo "The build was successful!"
            // Optionally send a Slack notification here
            // slackSend (channel: '#dev-team', message: "Build #${env.BUILD_NUMBER} passed successfully!")
        }
        
        failure {
            echo "The build has failed!"
            // Optionally send a Slack notification here for failure
            // slackSend (channel: '#dev-team', message: "Build #${env.BUILD_NUMBER} failed! Please check the logs.")
        }
        
        always {
            echo "This will always run, regardless of build status."
            // Archive the build artifacts regardless of success or failure
            // archiveArtifacts allowEmptyArchive: true, artifacts: '**/target/*.jar', onlyIfSuccessful: false
        }
    }
}
