//设置镜像版本
tag ="latest"

//获取用户选择部署的服务项
SelectedServiceNames = "${ServiceName}".split(",")

//获取用户选择部署的服务器节点
SelectedNodes="${PublishServer}".split(",")

//如果是本地私有镜像仓库，如Harbor，需要设置repositoryUrl和projectName
//如果是dockerhub,则不需要设置repository
repositoryUrl="9.197.4.240:85"
projectName="microservice-demo"

//如果工具配置是自动下载的，那么通过tool命令返回的工具路径中会缺失路径隔离符"\"
//所以最好是自行下载好工具，再在Jenkins中配置路径
//scannerHome = tool name: 'sonarqube-scanner', type: 'hudson.plugins.sonar.SonarRunnerInstallation'
//scannerHome = "C:/ProgramData/Jenkins/.jenkins/tools/hudson.plugins.sonar.SonarRunnerInstallation/sonarqube-scanner"

pipeline {
    agent {
        label "${JenkinsNodes}"
    }

    environment{
        scannerHome = tool 'sonarqube-scanner'
    }

    stages{
        stage('Pull Code') {
            steps{
                //checkout scmGit(branches: [[name: "*/${Branch}"]], extensions: [], userRemoteConfigs: [[credentialsId: 'f2f81612-da80-4372-9d5f-f19bb42c442b', url: 'https://github.com/bjaimm/myspringcloud_parent.git']])
                checkout scmGit(branches: [[name: "*/${Branch}"]], extensions: [], userRemoteConfigs: [[credentialsId: 'eb5f023a-abf9-4307-90ec-2c949f2b00ba', url: 'git@github.com:bjaimm/myspringcloud_parent.git']])
                /*dir("${ServiceName}") {
                    sh "mkdir -p target/classes"
                }*/
            }
        }

        stage("Sonar Scan"){
            //设置SonarQube需要的JDK版本，在Jenkins全局工具里设置
            tools {
                jdk "jdk17"
            }

            steps {
                script {
                    if("${IsSonarScanSkipped}"=="false") {
                        for (i = 0; i < SelectedServiceNames.length; i++) {
                            echo SelectedServiceNames[i]
                            CurrentServiceName = SelectedServiceNames[i].split("@")[0]
                            echo CurrentServiceName
                            //设置当前工作目录
                            dir("${CurrentServiceName}") {
                                sh "mkdir -p target/classes"
                                withSonarQubeEnv('sonarqube-server') {
                                    sh "${scannerHome}/bin/sonar-scanner"
                                }
                            }
                        }
                    }
                    else{
                        echo "SonarScan step is not required per user input"
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
                script {
                    if("${IsServiceBuildSkipped}"=="false") {
                        for (i = 0; i < SelectedServiceNames.length; i++) {

                            CurrentServiceName = SelectedServiceNames[i].split("@")[0]
                            //设置镜像名
                            imageName = "${CurrentServiceName}:${tag}"

                            //Compile,Package,Build image
                            sh "mvn -f ${CurrentServiceName} clean package dockerfile:build -DskipTests=true"

                            //Tag image1
                            sh "docker tag ${imageName} ${repositoryUrl}/${projectName}/${imageName}"

                            //Push image
                            withCredentials([usernamePassword(credentialsId: 'de607c77-1073-4e39-bbcc-73fdab617162', passwordVariable: 'password', usernameVariable: 'username')]) {
                                sh "docker login -u ${username} -p ${password} ${repositoryUrl}"
                                sh "docker push ${repositoryUrl}/${projectName}/${imageName}"
                            }
                        }
                    }
                    else{
                        echo "ServiceBuild step is not required per user input"
                    }
                }
            }
        }
        stage("Deploy Application Remotely"){
            steps {
                script {

                    for (i = 0; i < SelectedServiceNames.length; i++) {

                        CurrentServiceName = SelectedServiceNames[i].split("@")[0]

                        for (j = 0; j < SelectedNodes.length; j++) {
                            CurrentNodeName = SelectedNodes[j].split("@")[0]
                            CurrentNodeIP = SelectedNodes[j].split("@")[1]

                            echo CurrentNodeName
                            echo CurrentNodeIP

                            //设置SSH远程部署脚本命令行
                            jenkins_shell= "/opt/jenkins_shell/cluster/deploy_cluster.sh $CurrentServiceName $repositoryUrl $projectName $tag $CurrentNodeIP >> /opt/jenkins_shell/cluster/deploy_cluster.log"

                            sshPublisher(publishers: [sshPublisherDesc(configName: "${CurrentNodeName}", transfers: [sshTransfer(cleanRemote: false, excludes: '', execCommand: "${jenkins_shell}", execTimeout: 120000, flatten: false, makeEmptyDirs: false, noDefaultExcludes: false, patternSeparator: '[, ]+', remoteDirectory: '', remoteDirectorySDF: false, removePrefix: '', sourceFiles: '')], usePromotionTimestamp: false, useWorkspaceInPromotion: false, verbose: false)])
                        }
                    }
                }
            }
        }
    }

}
