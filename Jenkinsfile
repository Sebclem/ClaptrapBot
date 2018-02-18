
pipeline {
    agent any

    stages {

        stage('Clone') {
            steps{
                step{
                    def app
                    // for display purposes
                    // Get some code from a GitHub repository
                    git 'https://github.com/BrokenFire/BrokenDiscordBot.git'
                }
            }

        }
        stage('Build image') {
            /* This builds the actual image; synonymous to
             * docker build on the command line */
            node{
                app = docker.build("brokenfire/brokendiscordbot","--rm=true .")
            }

        }
        stage('Push image') {
            /* Finally, we'll push the image with two tags:
            * First, the incremental build number from Jenkins
            * Second, the 'latest' tag.
            * Pushing multiple tags is cheap, as all the layers are reused. */
            node{
                app.push()

            }

        }
        stage('Cleaning'){
           sh "docker image prune -f"
        }
    }
}
