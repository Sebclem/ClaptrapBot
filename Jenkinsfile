pipeline {
    agent any

    stages {
        stage('Clone') { // for display purposes
            steps{
                def app
                // Get some code from a GitHub repository
                git 'https://github.com/BrokenFire/BrokenDiscordBot.git'
            }

        }
        stage('Build image') {
            steps {
                /* This builds the actual image; synonymous to
                * docker build on the command line */

                app = docker.build("brokenfire/brokendiscordbot","--rm=true .")
            }
        }
        stage('Push image') {
            steps {

                /* Finally, we'll push the image with two tags:
                * First, the incremental build number from Jenkins
                * Second, the 'latest' tag.
                * Pushing multiple tags is cheap, as all the layers are reused. */
                app.push("devel")
            }
        }
        stage('Cleaning'){
            sh "docker image prune -f"
        }
    }
} 
