@echo off

REM set MAVEN_HOME=%CD%\build-tools\apache-maven-3.3.9
REM set MAVEN_HOME=E:\PROJECT_REPO_CODEBASE\REPO_PROJECTS\REPOSITORY\MAVEN_REPO\build-tools\apache-maven-3.3.9
set MAVEN_HOME=D:\apache-maven-3.6.3
set JAVA_HOME=C:\Program Files\Java\jdk1.8.0_181
set PATH=%MAVEN_HOME%\bin;%JAVA_HOME%\bin;%PATH%


echo Setting Maven home to: %MAVEN_HOME%

mvn -version
