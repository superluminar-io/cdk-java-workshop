---
title: Lab 0 - Init
weight: 10
---

**In this Lab we will**:

- Setup the development environment
- Use [AWS CDK CLI](https://docs.aws.amazon.com/cdk/latest/guide/cli.html)
- Deploy a simple `hello-world` function
- Find logs and metrics for the function

**You completed this lab if you**:

- Successfully deployed the `hello-world` function
- Executed it once via the HTTP endpoint (e.g using `curl`)
- Extended the function to generate some log output and found it in *Cloudwatch Logs*
- Took a look at the dashboards for your function

## Setup
First we want to create a new serverless project with Java. Then we want to convert it to use Gradle instead of Maven.

### Bootstrap the project

```sh
# Create a new folder for your project:
mkdir serverless-workshop

# Change to the newly created directory
cd serverless-workshop

# Now bootstrap it
cdk init app --language java
```

If everything works as intended you should see output similar to this:
```sh
Applying project template app for java
# Welcome to your CDK Java project!

This is a blank project for Java development with CDK.

The `cdk.json` file tells the CDK Toolkit how to execute your app.

It is a [Maven](https://maven.apache.org/) based project, so you can open this project with any Maven compatible Java IDE to build and run tests.

## Useful commands

 * `mvn package`     compile and run tests
 * `cdk ls`          list all stacks in the app
 * `cdk synth`       emits the synthesized CloudFormation template
 * `cdk deploy`      deploy this stack to your default AWS account/region
 * `cdk diff`        compare deployed stack with current state
 * `cdk docs`        open CDK documentation

Enjoy!

Initializing a new git repository...
Executing 'mvn package'
✅ All done!
```

### Converting to Gradle

We want to use Gradle instead of Maven, so do the following:
```sh
# convert project to gradle
gradle init
```

It should print something like:
```
Found a Maven build. Generate a Gradle build from this? (default: yes) [yes, no] yes


> Task :init
Maven to Gradle conversion is an incubating feature.
Get more help with your project: https://docs.gradle.org/6.5.1/userguide/migrating_from_maven.html

BUILD SUCCESSFUL in 4s
2 actionable tasks: 2 executed
```

Open the file `cdk.json` and change it to:
```shell script
{
  "app": "./gradlew build run",
  "context": {
    "@aws-cdk/core:enableStackNameDuplicates": "true",
    "aws-cdk:enableDiffNoFail": "true"
  }
}
```

At the top of your `build.gradle` file, add the [application plugin](https://docs.gradle.org/current/userguide/application_plugin.html) and set the main class, it should look like this:
```groovy
plugins {
    id 'application'
    id 'java'
    id 'maven-publish'
}

application {
    mainClassName = 'com.myorg.FooApp'
}
```

Now delete the `pom.xml` file, you won't need it anymore.

## Create your first Lambda function

Now we will create our first Lambda function. We need to set up the Gradle structure, implement the handler, and wire it up with the CDK. 

### Bootstrap

Create a Gradle project in a folder called `lambda`:

```sh
# Create a folder to hold the code for the Lambda function
mkdir lambda
cd lambda
# Bootstrap 
gradle init --type java-library --dsl groovy --test-framework junit-jupiter --project-name lambda --package com.example
```

We need some dependencies, paste these into `build.gradle`:

```groovy
    implementation 'com.amazonaws:aws-lambda-java-core:1.2.1'
    implementation 'com.amazonaws:aws-lambda-java-events:2.2.9'
    runtimeOnly 'com.amazonaws:aws-lambda-java-log4j2:1.2.0'
```

AWS Lambda expects a [deployment package](https://docs.aws.amazon.com/lambda/latest/dg/java-package.html) which is a ZIP file with the Java classes plus libraries, so paste this at
the bottom of `build.gradle`:
```groovy

task buildZip(type: Zip) {
    from compileJava
    from processResources
    into('lib') {
        from configurations.runtimeClasspath
    }
}

build.dependsOn buildZip
```

### Write some code

Create a class called `HelloWorldHandler`.
Paste in the following:
```java
package com.example;

import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.RequestHandler;
import com.amazonaws.services.lambda.runtime.events.APIGatewayProxyRequestEvent;
import com.amazonaws.services.lambda.runtime.events.APIGatewayProxyResponseEvent;

public class HelloWorldHandler implements RequestHandler<APIGatewayProxyRequestEvent, APIGatewayProxyResponseEvent> {

    @Override
    public APIGatewayProxyResponseEvent handleRequest(APIGatewayProxyRequestEvent input, Context context) {
        APIGatewayProxyResponseEvent response = new APIGatewayProxyResponseEvent();
        response.setStatusCode(200);
        response.setBody("Hello World!");
        return response;
    }
}
```

Try to compile it, it should work.

### Write some test code

For bonus points implement a unit test for the class above. Skip this if you are in a rush.

### Wire it up with CDK

We'll need to configure our project and write some more code

#### Setup the Gradle project
Navigate back to the top level project, and open `settings.gradle`.
Paste the following:
```groovy
include 'lambda'
```

Now open `build.gradle` and add these dependencies:
```groovy
    implementation 'software.amazon.awscdk:core:1.55.0'
    implementation 'software.amazon.awscdk:lambda:1.55.0'
    implementation 'software.amazon.awscdk:apigatewayv2:1.55.0'

    implementation project(':lambda') // Depend on our subproject, so it will always be rebuilt
```

#### Write the code

Open the file `FooStack.java` (rename it if you want to) and paste the following:
```java
package de.libri;

import software.amazon.awscdk.core.CfnOutput;
import software.amazon.awscdk.core.Construct;
import software.amazon.awscdk.core.Stack;
import software.amazon.awscdk.core.StackProps;
import software.amazon.awscdk.services.apigatewayv2.AddRoutesOptions;
import software.amazon.awscdk.services.apigatewayv2.HttpMethod;
import software.amazon.awscdk.services.apigatewayv2.LambdaProxyIntegration;
import software.amazon.awscdk.services.lambda.Code;
import software.amazon.awscdk.services.lambda.Function;
import software.amazon.awscdk.services.lambda.Runtime;
import software.amazon.awscdk.services.apigatewayv2.HttpApi;

import java.util.Arrays;

public class FooStack extends Stack {
    public FooStack(final Construct scope, final String id) {
        this(scope, id, null);
    }

    public FooStack(final Construct scope, final String id, final StackProps props) {
        super(scope, id, props);

        // Create the Lambda function
        Function myFunc = Function.Builder.create(this, "helloWorld")
            .code(Code.fromAsset(System.getProperty("user.dir") + "/lambda/build/distributions/lambda-func-1.0-SNAPSHOT.zip"))
            .handler("com.myorg.HelloWorldHandler")
            .runtime(Runtime.JAVA_8)
            .build();

        // Wire up the Lambda function to be accessible at path '/hello-world'
        LambdaProxyIntegration lambdaProxyIntegration = LambdaProxyIntegration.Builder.create().handler(myFunc).build();
        HttpApi httpApi = HttpApi.Builder.create(this,"HttpApi").build();
        httpApi.addRoutes(AddRoutesOptions.builder()
                .path("/hello-world")
                .methods(Arrays.asList(HttpMethod.GET))
                .integration(lambdaProxyIntegration)
                .build()
        );

        // Output the URL for later consumption
        CfnOutput.Builder.create(this, "URL").value(httpApi.getUrl() + "hello-world").build();
    }
}
```

## Deploy it using CDK

Open your shell, make sure you have your AWS credentials configured.
Now run:
```sh
cdk synth
cdk deploy
```

## Call your function using curl

Now use the output from above and call your HTTP handler, it should print "Hello world!".

## CloudFormation

If you check out the [CloudFormation console](https://eu-central-1.console.aws.amazon.com/cloudformation/home?region=eu-central-1) you will notice a new stack with the name you just defined in the guided deployment. CloudFormation is essentially a tool to provision, maintain and remove infrastructure in AWS. SAM uses CloudFormation under the hood to deploy the infrastrucutre we describe in this workshop.

Try to figure out:

- Which resources did we just deploy?
- What are outputs and why are they helpful?
