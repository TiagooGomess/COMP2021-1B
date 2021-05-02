#!/bin/bash

rm -Rf generated/
gradle build
java -cp "./build/classes/java/main/:./libs/utils.jar:./libs/gson-2.8.2.jar:./libs/ollir.jar" Main "Simple"