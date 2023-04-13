tag ="latest"
imageName = "${ServiceName}:${tag}"
//如果是本地私有镜像仓库，如Harbor，需要设置repositoryUrl和projectName
//如果是dockerhub,则不需要设置repository
repositoryUrl=""
projectName="microservice-demo"
//Windows节点下这个变量中俄路径隔离符会被去除
scannerHome = tool name: 'sonarqube-scanner', type: 'hudson.plugins.sonar.SonarRunnerInstallation'
//scannerHome = "C:/ProgramData/Jenkins/.jenkins/tools/hudson.plugins.sonar.SonarRunnerInstallation/sonarqube-scanner"

pipeline {
    agent any

    stages{
        stage('Pull Code') {
            steps{
                checkout scmGit(branches: [[name: '*/master']], extensions: [], userRemoteConfigs: [[credentialsId: 'f2f81612-da80-4372-9d5f-f19bb42c442b', url: 'https://github.com/bjaimm/myspringcloud_parent.git']])
            }
        }

        stage("Sonar Scan"){

            steps{

                withSonarQubeEnv('sonarqube-server'){

                    sh "cd ${ServiceName}"
                    sh "echo ${scannerHome}"
                    sh "${scannerHome}/bin/sonar-scanner"
                }
             }

        }
        stage("Common Modules Installation"){
            steps{
                sh "mvn -f microservice_commons clean intall -DskipTests=true"
            }
        }
        stage("Compile,Package Microservice and Build Push Docker Image"){
            steps{
                //Compile,Package,Build image
                sh "mvn -f ${ServiceName} clean package -DskipTests=true dockerfile:build"

                //Tag,Push image
                sh "docker tag ${imageName} ${repositoryUrl}/${projectName}/${imageName}"
            }
        }
    }

}
