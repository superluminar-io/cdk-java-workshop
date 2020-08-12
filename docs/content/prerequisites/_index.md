---
title: Prerequisites
weight: 5
---

To get started with the workshop you need to have **Administrator Access** to an AWS Account. Please do not use the 
accounts root user since this is bad practice and leads to potential security risks.

We recommend you create a dedicate IAM user following [this guide](https://docs.aws.amazon.com/IAM/latest/UserGuide/getting-started_create-admin-group.html).

## Configure AWS access

Make sure, that you are able to login into the [AWS Console](https://console.aws.amazon.com/) with your IAM user.

## Required software

First you will need a installation of [NodeJS](https://nodejs.org).
Now install the AWS CDK with:
```shell script
npm install -g aws-cdk
```

## Bootstrap CDK for your AWS Account

{{% notice info %}}
This needs to be done only once per AWS Account (co-ordinate by yourselves who is going to do this):
{{% /notice %}}
```shell script
cdk bootstrap <aws-account-id>/eu-central-1
```
where `aws-account-id` is the account id without dashes.
