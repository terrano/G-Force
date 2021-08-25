
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
        stage('Pull changes test...') {
            steps {
                echo 'Jenkins does it by itself.'
            }
        }
        stage('Deploy') {
            steps {
                echo 'Deploying....'
            }
        }
        stage('Just another testing') {
            steps {
                echo 'Small step for a man...'
                echo '...Jaint leap for mankind'
            }
        }
    }
}
