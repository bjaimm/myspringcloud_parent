tag ="latest"
imageName = "${ServiceName}:${tag}"
//如果是本地私有镜像仓库，如Harbor，需要设置repositoryUrl和projectName
//如果是dockerhub,则不需要设置repository
repositoryUrl=""
projectName="microservice-demo"
//Windows节点下这个变量中俄路径隔离符会被去除
//scannerHome = tool name: 'sonarqube-scanner', type: 'hudson.plugins.sonar.SonarRunnerInstallation'
scannerHome = "C:/ProgramData/Jenkins/.jenkins/tools/hudson.plugins.sonar.SonarRunnerInstallation/sonarqube-scanner"

pipeline {
    agent any

    stages{
        stage('Pull Code') {
            steps{
                checkout scmGit(branches: [[name: '*/master']], extensions: [], userRemoteConfigs: [[credentialsId: 'f2f81612-da80-4372-9d5f-f19bb42c442b', url: 'https://github.com/bjaimm/myspringcloud_parent.git']])
            }
        }

        stage("Sonar Scan"){
            //设置SonarQube需要的JDK版本，在Jenkins全局工具里设置
            tools {
                jdk "jdk17"
            }

            steps{

                withSonarQubeEnv('sonarqube-server'){

                    sh "cd ${ServiceName}"
                    //sh "${scannerHome}/bin/sonar-scanner ${ServiceName}"
                }
             }

        }
        stage("Common Modules Installation"){
            //设置Maven引用，在Jenkins全局工具里设置
            tools{
                maven "Maven3.8.6"
            }
            steps{
                sh "mvn -f microservice_commons clean install -DskipTests=true"
            }
        }
        stage("Compile,Package Microservice and Build Push Docker Image"){
            //设置Maven引用，在Jenkins全局工具里设置
            tools{
                 maven "Maven3.8.6"
            }
            steps{
                sh "cd ${ServiceName}"

                //Compile,Package,Build image
                sh "mvn clean package -DskipTests=true dockerfile:build"

                //Tag image
                sh "docker tag ${imageName} ${repositoryUrl}/${projectName}/${imageName}"

                //Push image
            }
        }
    }

}
