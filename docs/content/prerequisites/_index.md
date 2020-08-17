---
title: Prerequisites
weight: 5
---

## AWS CLI

The AWS CLI allows you to interact with AWS services from a terminal session.
Make sure you have the latest version of the AWS CLI installed on your system.

 * Windows: [MSI installer](https://docs.aws.amazon.com/cli/latest/userguide/install-windows.html#install-msi-on-windows)
 * Linux, macOS or Unix: [Bundled installer](https://docs.aws.amazon.com/cli/latest/userguide/awscli-install-bundle.html#install-bundle-other)

See the [AWS Command Line Interface
installation](https://docs.aws.amazon.com/cli/latest/userguide/installing.html)
page for more details.


## AWS account and access

To get started with the workshop you need to have **Administrator Access** to an AWS Account. Please do not use the 
accounts root user since this is bad practice and leads to potential security risks.

We recommend you create a dedicated IAM user following [this guide](https://docs.aws.amazon.com/IAM/latest/UserGuide/getting-started_create-admin-group.html).

Make sure, that you are able to login into the [AWS Console](https://console.aws.amazon.com/) with your IAM user.

## NodeJS

The AWS CDK uses Node.js (>= 10.3.0).

* To install Node.js visit the [node.js](https://nodejs.org) website.

    * __Windows__: if you have an old version of Node.js installed on your
      system, it may be required to run the .msi installation as Administrator.

* If you already have Node.js installed, verify that you have a compatible version:

    ```
    node --version
    ```

    Output should be >= 10.3.0:

    ```
    v10.3.0
    ```

## Install the AWS CDK

Next, we'll install the AWS CDK Toolkit. The toolkit is a command-line utility
which allows you to work with CDK apps.

Open a terminal session and run the following command:

- Windows: you'll need to run this as an Administrator
- POSIX: on some systems you may need to run this with `sudo`

```
npm install -g aws-cdk@1.57
```

You can check the toolkit version:

```
cdk --version
1.57
```

## Tell CDK your AWS region and API credentials

Windows:

```
SET AWS_DEFAULT_REGION=eu-central-1
SET AWS_ACCESS_KEY_ID=ASIA3DSBXXXXXXXXXXXX
SET AWS_SECRET_ACCESS_KEY=2Gex8xT5XXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXX
SET AWS_SESSION_TOKEN=XXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXX
```

Mac/Linux:

```
export AWS_DEFAULT_REGION=eu-central-1
export AWS_ACCESS_KEY_ID=ASIA3DSBXXXXXXXXXXXX
export AWS_SECRET_ACCESS_KEY=2Gex8xT5XXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXX
export AWS_SESSION_TOKEN=XXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXX
```

## Bootstrap CDK for your AWS Account

{{% notice info %}}
This needs to be done only once per AWS Account (co-ordinate by yourselves who is going to do this)
{{% /notice %}}

```shell script
cdk bootstrap <aws-account-id>/eu-central-1
```
where `aws-account-id` is the account id without dashes.

## Java and Gradle

You will also need [Java JDK](http://www.oracle.com/technetwork/java/javase/downloads/index.html) 1.8 or higher and the latest version of [Gradle](https://gradle.org/install/).
