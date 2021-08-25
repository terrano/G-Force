
pipeline {
    agent any
    
    tools {
        gradle 'MyGradle'
    }

    stages {
        stage('Build with Gradle') {
            steps {
                echo 'Building..'
          /*      withGradle() {
                    sh './gradlew -v'
          */      }
            }
        }
        stage('Pull changes test...') {
            steps {
                echo 'Jenkins does it by itself.'
                script {
                    def test = 2 + 2 > 3 ? "cool" : "really?"
                    echo test
                //    sh './gradlew -v'
                }
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
