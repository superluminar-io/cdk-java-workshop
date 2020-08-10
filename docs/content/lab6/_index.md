---
title: Lab 6 - Tracing w/ X-Ray
weight: 40
---

**In this Lab we will**:

- Enable AWS X-Ray for our functions using AWS SAM
- Learn how distributed tracing works with AWS X-Ray 
- Instrument our application code to gain even more insights

**You completed this lab if you**:

- Can differentiate traces in X-Ray by HTTP Method (POST, GET, ...)
- Can tell how long your function needed to start (cold vs. warm)
- Can tell how long your function needed to persist and fetch data from DynamoDB

## Tracing with AWS X-Ray

[AWS X-Ray](https://aws.amazon.com/xray/features/) allows us to do distributed tracing in a way that we can trace 
invocations of our functions through the different AWS services.

### Step 1 - Build-in tracing

AWS SAM allows us to easily configure the X-Ray tracing mode for our lambda functions 
(check the `Tracing` [property](https://github.com/awslabs/serverless-application-model/blob/master/versions/2016-10-31.md#awsserverlessfunction)
of your function) 

### Step 2 - Instrument your applications codes

In order have more control over the traces X-Ray records, it is possible to add X-Ray tracing to your
AWS clients with the [xray sdk for go](https://github.com/aws/aws-xray-sdk-go).
The Clients will then trace the calls the other AWS service like DynamoDB for instance.


## Hints

- You can find an example implementation here: [Step 1](https://github.com/superluminar-io/serverless-workshop-go/compare/lab5..lab6_a?expand=1)
- You can find an example implementation here: [Step 2](https://github.com/superluminar-io/serverless-workshop-go/compare/lab6_a..lab6_b?expand=1)
