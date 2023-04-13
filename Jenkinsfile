def tag ="latest"
def imageName = "${ServiceName}:${tag}"
//如果是本地私有镜像仓库，如Harbor，需要设置repositoryUrl和projectName
//如果是dockerhub,则不需要设置repository
def repositoryUrl=""
def projectName="microservice-demo"

node() {

    stage('Pull Code') {

        checkout scmGit(branches: [[name: '*/master']], extensions: [], userRemoteConfigs: [[credentialsId: 'f2f81612-da80-4372-9d5f-f19bb42c442b', url: 'https://github.com/bjaimm/myspringcloud_parent.git']])

    }

    stage("Sonar Scan"){

        def scannerHome = tool 'sonarqube-scanner'
        sh "mkdir test"

    }
    stage("Common Modules Installation"){
        sh "mvn -f microservice_commons clean intall -DskipTests=true"
    }
    stage("Compile,Package Microservice and Build Push Docker Image"){
        //Compile,Package,Build image
        sh "mvn -f ${ServiceName} clean package -DskipTests=true dockerfile:build"

        //Tag,Push image
        sh "docker tag ${imageName} ${repositoryUrl}/${projectName}/${imageName}"
    }

}
