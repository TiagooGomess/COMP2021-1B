# Compilers Project

For this project, you need to [install Gradle](https://gradle.org/install/)

## Compile

To compile the program, run ``gradle build``. This will compile your classes to [classes/java/main](build/classes/java/main).

## Test

To test the program, run ``gradle test``. This will execute the build, and run the JUnit tests in the ``test`` folder. If you want to see output printed during the tests, use the flag ``-i`` (i.e., ``gradle test -i``).
You can also see a test report by opening [build/reports/tests/test/index.html](build/reports/tests/test/index.html).

### Run

To run the project, you can use the following command:

```bash
java -cp "./build/classes/java/main/:./libs/utils.jar:./libs/gson-2.8.2.jar:./libs/ollir.jar" Main <<jmm_file_to_parse>>
```

This will parse the program and put the results in the [results folder](results)

### Asked features

All the basic features asked for this project were implemented.

### Extra features

We implemented some extra features, namely:

- Handling uninitialized variables
- Disabling the use of fields in static functions
- Verifying if caller of static methods is a class reference
- Method overloading, including parent class
- Verification of method return types
- Expected type's assumption even if not possible in the first usages
- Length can only be used in arrays
- Handling variables with reserved names in jmm and ollir
- Handling not declared variables
- Handling method and variable redefinition
- Handling types that were not imported nor declared
