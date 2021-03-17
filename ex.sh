rm -Rf generated/
gradle build
java -cp "./build/classes/java/main/:./libs/utils.jar" JmmCompiler