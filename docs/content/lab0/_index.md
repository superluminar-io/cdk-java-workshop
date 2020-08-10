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

# Setup
First we want to create a new serverless project with Java. Then we want to convert it to use Gradle instead of Maven.

## Bootstrap the project

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

## Converting to Gradle

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

# Create your first Lambda function

## Bootstrap
```sh
# Create a folder to hold the code for the Lambda function
mkdir lambda
cd lambda
# Bootstrap 
gradle init --type java-library --dsl groovy --test-framework junit-jupiter --project-name lambda --package com.example
```

## Add dependencies

## Write some code

## Wire it up with CDK

### Bootstrap
- Go back to your Cloud9 environment
- Open the terminal window and type:
- `git clone --single-branch --branch lab0 https://github.com/superluminar-io/serverless-workshop-go.git`
- `cd serverless-workshop-go`
- `./bootstrap.sh`

### Test
- `sam --version`
- `aws sts get-caller-identity`

### SAM

In this step, we are going to setup SAM and deploy the infrastructure for the first time. SAM will create a `samconfig.toml` file to persist your choices.

```sh
# Compile GO files
$ make build

# Configure SAM and deploy the infrastructure
$ sam deploy --guided

  Configuring SAM deploy
  ======================

  Looking for samconfig.toml :  Not found

  Setting default arguments for 'sam deploy'
  =========================================
  Stack Name [sam-app]: UrlShortener 
  AWS Region [us-east-1]: eu-central-1
  Confirm changes before deploy [y/N]: Y
  Allow SAM CLI IAM role creation [Y/n]: Y
  HelloWorldFunction may not have authorization defined, Is this okay? [y/N]: Y
  Save arguments to samconfig.toml [Y/n]: Y
```

## CloudFormation

If you check out the [CloudFormation console](https://eu-central-1.console.aws.amazon.com/cloudformation/home?region=eu-central-1) you will notice a new stack with the name you just defined in the guided deployment. CloudFormation is essentially a tool to provision, maintain and remove infrastructure in AWS. SAM uses CloudFormation under the hood to deploy the infrastrucutre we describe in this workshop.

Try to figure out:

- Which resources did we just deploy?
- What are outputs and why are they helpful?
