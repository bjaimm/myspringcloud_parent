//设置镜像版本
tag ="latest"

//获取用户选择部署的服务项
SelectedServiceNames = "${ServiceName}".split(",")

//设置镜像名
imageName = "${ServiceName}:${tag}"

//如果是本地私有镜像仓库，如Harbor，需要设置repositoryUrl和projectName
//如果是dockerhub,则不需要设置repository
repositoryUrl="9.197.4.240:85"
projectName="microservice-demo"

//Windows节点下这个变量中俄路径隔离符会被去除
//scannerHome = tool name: 'sonarqube-scanner', type: 'hudson.plugins.sonar.SonarRunnerInstallation'
scannerHome = "C:/ProgramData/Jenkins/.jenkins/tools/hudson.plugins.sonar.SonarRunnerInstallation/sonarqube-scanner"

//设置SSH远程部署脚本命令行
jenkins_shell= "/opt/jenkins_shell/deploy.sh $ServiceName $repositoryUrl $projectName $tag >> /opt/jenkins_shell/deploy.log"

pipeline {
    agent any

    stages{
        stage('Pull Code') {
            steps{
                checkout scmGit(branches: [[name: '*/master']], extensions: [], userRemoteConfigs: [[credentialsId: 'f2f81612-da80-4372-9d5f-f19bb42c442b', url: 'https://github.com/bjaimm/myspringcloud_parent.git']])

                dir("${ServiceName}") {
                    sh "mkdir -p target/classes"
                }
            }
        }

        stage("Sonar Scan"){
            //设置SonarQube需要的JDK版本，在Jenkins全局工具里设置
            tools {
                jdk "jdk17"
            }
            for(i=0;i<${SelectedServiceNames}.length;i++) {

                ServiceName = "${SelectedServiceNames}".split("@")[0]
                steps{
                        //设置当前工作目录
                        dir("${ServiceName}") {
                            withSonarQubeEnv('sonarqube-server') {
                                sh "${scannerHome}/bin/sonar-scanner"
                            }
                        }
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
                //编译父工程，确保pom文件在repository中出现
                sh "mvn clean install -DskipTests=true -N"
            }
        }
        stage("Compile,Package Microservice and Build Push Docker Image"){
            //设置Maven引用，在Jenkins全局工具里设置
            tools{
                 maven "Maven3.8.6"
            }
            steps{
                //Compile,Package,Build image
                sh "mvn -f ${ServiceName} clean package dockerfile:build -DskipTests=true"

                //Tag image1
                sh "docker tag ${imageName} ${repositoryUrl}/${projectName}/${imageName}"

                //Push image
                withCredentials([usernamePassword(credentialsId: 'de607c77-1073-4e39-bbcc-73fdab617162', passwordVariable: 'password', usernameVariable: 'username')]) {
                    sh "docker login -u ${username} -p ${password} ${repositoryUrl}"
                    sh "docker push ${repositoryUrl}/${projectName}/${imageName}"
                }

            }
        }
        stage("Deploy Application Remotely"){
            steps {
                sshPublisher(publishers: [sshPublisherDesc(configName: 'publish_server', transfers: [sshTransfer(cleanRemote: false, excludes: '', execCommand: "${jenkins_shell}", execTimeout: 120000, flatten: false, makeEmptyDirs: false, noDefaultExcludes: false, patternSeparator: '[, ]+', remoteDirectory: '', remoteDirectorySDF: false, removePrefix: '', sourceFiles: '')], usePromotionTimestamp: false, useWorkspaceInPromotion: false, verbose: false)])
            }
        }
    }

}
