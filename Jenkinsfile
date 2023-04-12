node {
    //sonar_home = tool name: 'sonarqube-scanner'

    stage('Pull Code') {

        checkout scmGit(branches: [[name: '*/master']], extensions: [], userRemoteConfigs: [[credentialsId: 'f2f81612-da80-4372-9d5f-f19bb42c442b', url: 'https://github.com/bjaimm/myspringcloud_parent.git']])

    }

    stage("Sonar Scan"){

        def scannerHome = tool 'sonarqube-scanner'
        withSonarQubeEnv(credentialsId: 'a6357d77-cb64-4aef-9665-16a0b2608739'){
            cmd "cd ${ServiceName}"
            //${scannerHome}/bin/sonar-scanner
        }
    }

}
