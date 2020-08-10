---
title: Prerequisites
weight: 5
---

To get started with the workshop you need to have **Administrator Access** to an AWS Account. Please do not use the 
accounts root user since this is bad practice and leads to potential security risks.

We recommend you create a dedicate IAM user following [this guide](https://docs.aws.amazon.com/IAM/latest/UserGuide/getting-started_create-admin-group.html).

## Required software

First you will need a installation of [NodeJS](https://nodejs.org).
Now install the AWS CDK with:
```shell script
npm install -g aws-cdk
```

## Configure AWS access

Make sure, that you are able to login into the [AWS Console](https://console.aws.amazon.com/) with your IAM user.
