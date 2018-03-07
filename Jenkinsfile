pipeline {
    agent any
    stages {

        stage('Clone') { // for display purposes
            // Get some code from a GitHub repository
            steps {
                echo env.BRANCH_NAME
                script {
                    if (env.BRANCH_NAME == 'master') {
                        git url: 'https://github.com/BrokenFire/BrokenDiscordBot.git', branch: 'master'
                    } else {
                        git url: 'https://github.com/BrokenFire/BrokenDiscordBot.git', branch: 'devel'
                    }
                }
            }


        }
        stage('Gradle Build'){
            steps{
                script {
                    if (env.BRANCH_NAME == 'master') {
                        build job: 'Bot Discord Gradle', wait: true
                    } else {
                        build job: 'Bot Discord Gradle devel', wait: true
                    }
                }
            }

        }
        stage('Build Docker image') {
            /* This builds the actual image; synonymous to
             * docker build on the command line */
            steps{
                script{
                    app = docker.build("brokenfire/brokendiscordbot",'--build-arg BUILD_NBR=${BUILD_NUMBER} --build-arg BRANCH_NAME=${BRANCH_NAME} --rm=true .')
                }

            }

        }
        stage('Push Docker image') {
            /* Finally, we'll push the image with two tags:
             * First, the incremental build number from Jenkins
             * Second, the 'latest' tag.
             * Pushing multiple tags is cheap, as all the layers are reused. */
            steps{
                withCredentials([[$class: 'UsernamePasswordMultiBinding', credentialsId: 'docker-hub-credentials', usernameVariable: 'USERNAME', passwordVariable: 'PASSWORD']]) {
                    sh 'docker login -u $USERNAME -p $PASSWORD'
                    script {
                        if (env.BRANCH_NAME == 'master') {
                            app.push()
                        } else {
                            app.push("devel")
                        }
                    }
                }
            }





        }
        stage('Cleaning'){
            steps{
                sh "docker image prune -f"
            }

        }
    }
}

