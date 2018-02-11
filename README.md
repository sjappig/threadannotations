[![Build Status](https://travis-ci.org/sjappig/threadannotations.svg?branch=master)](https://travis-ci.org/sjappig/threadannotations)

# threadannotations
threadannotations is a light-weight Java-library for thread annotations with Java-agent to enforce them.

Annotate your classes and methods using
* ```@SwingThread```
* ```@SingleThread```
* ```@MultiThread```

and enforce annotatios using Java-agent while testing your code.

You can leave the annotations in your production code; without Java-agent,
they cause no performance penalty.

# Getting started with Gradle

1. Add the maven repository
    ```gradle
    repositories {
        maven {
            url "https://dl.bintray.com/sjappig/threadannotations"
        }
    }
    ```
1. Add the annotations and start using them
    ```gradle
    dependencies {
        compile 'io.threadannotations:threadannotations:+'
    }
    ```
1. Enforce the annotations during tests using the agent
    ```gradle
    configurations {
        testAgent
    }

    dependencies {
        testAgent("io.threadannotations:threadannotations-agent:+")
    }

    // assuming java-plugin
    test.jvmArgs "-javaagent:${configurations.testAgent.singleFile}"
    ```
1. If needed, enforce the annotations when running the application
    ```gradle
    // assuming application-plugin
    applicationDefaultJvmArgs << "-javaagent:${configurations.testAgent.singleFile}"
    ```
