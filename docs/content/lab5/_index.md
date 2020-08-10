---
title: Lab 5 - Blue/Green Deploy
weight: 35
---

**In this Lab we will**:

- Implement blue/green deployments using AWS SAM
- Learn about Lambda versions and aliases
- Learn about different deployment types (blue/green vs. canary)
- Use Cloudwatch Alarms to check if a change is safe to deploy to production

**You completed this lab if you**:

- Have implemented [CodeDeploy blue/green deployment](https://docs.aws.amazon.com/codedeploy/latest/userguide/welcome.html#welcome-deployment-overview-blue-green) for at least one function
- Watched a deployment in the [Code Deploy Console](https://eu-central-1.console.aws.amazon.com/codesuite/codedeploy/deployments?region=eu-central-1)

## Blue/Green Deployments with AWS SAM

Blue/Green deployments is a [feature of AWS CodeDeploy](https://docs.aws.amazon.com/codedeploy/latest/userguide/welcome.html#welcome-deployment-overview-blue-green). 
Thanks to the implementation in SAM it is fairly easy to make use of in our project.

Steps to take in this lab:

- Activate aliases for the function you want to "b/g-deploy" (see: `AutoPublishAlias`)
- Configure a `DeploymentPreference` for this function and 
- Decide which [deployment type](https://github.com/awslabs/serverless-application-model/blob/master/docs/safe_lambda_deployments.rst#traffic-shifting-configurations) to use
- Create and configure one or more Cloudwatch Alarms
- Optionally: Create and configure `PreTraffic` and `PostTraffic` hooks

## Hints

- [Traffic shifting using CodeDeploy](https://github.com/awslabs/serverless-application-model/blob/master/docs/safe_lambda_deployments.rst#traffic-shifting-using-codedeploy)
- You can find an example implementation here: https://github.com/superluminar-io/serverless-workshop-go/compare/lab4..lab5?expand=1
