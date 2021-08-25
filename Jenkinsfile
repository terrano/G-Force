
pipeline {
    agent any

    stages {
        stage('Build') {
            steps {
                echo 'Building..'
                script {
                    def test = 2 + 2 > 3 ? "cool" : "really?"
                    echo test
                }
            }
        }
        stage('Test') {
            steps {
                echo 'Testing..'
            }
        }
        stage('Deploy') {
            steps {
                echo 'Deploying....'
            }
        }
        stage('Just testing') {
            steps {
                echo 'Small step for a man...'
                echo '...Jaint leap for mankind'
            }
        }
    }
}
