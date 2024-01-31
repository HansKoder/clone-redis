#!/bin/bash

./gradlew sonar   -Dsonar.projectKey=clone-redis   -Dsonar.projectName='clone-redis'   -Dsonar.host.url=http://localhost:9010   -Dsonar.token=sqp_dbd21d2d0e998cee4e6ee11b42a413fe035fd680

if [ $? -eq 0 ] ;
then
    echo 'SonarQube Passed'
    exit 0
else
    echo 'SonarQube Failed'
    exit 1
fi