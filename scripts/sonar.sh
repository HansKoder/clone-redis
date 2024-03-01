#!/bin/bash

./gradlew sonar \
  -Dsonar.projectKey=redis-clone \
  -Dsonar.projectName='redis-clone' \
  -Dsonar.host.url=http://localhost:9000 \
  -Dsonar.token=sqp_e831d5d5e119ee183c6ba21bc97a82b723ef6431

if [ $? -eq 0 ] ;
then
    echo 'SonarQube Passed'
    exit 0
else
    echo 'SonarQube Failed'
    exit 1
fi