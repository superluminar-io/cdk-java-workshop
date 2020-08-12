package com.myorg;

import software.amazon.awscdk.core.*;
import software.amazon.awscdk.services.apigatewayv2.AddRoutesOptions;
import software.amazon.awscdk.services.apigatewayv2.HttpApi;
import software.amazon.awscdk.services.apigatewayv2.HttpMethod;
import software.amazon.awscdk.services.apigatewayv2.LambdaProxyIntegration;
import software.amazon.awscdk.services.dynamodb.Attribute;
import software.amazon.awscdk.services.dynamodb.AttributeType;
import software.amazon.awscdk.services.dynamodb.BillingMode;
import software.amazon.awscdk.services.dynamodb.Table;
import software.amazon.awscdk.services.lambda.Code;
import software.amazon.awscdk.services.lambda.Function;
import software.amazon.awscdk.services.lambda.Runtime;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

public class ServerlessWorkshopStack extends Stack {
    public ServerlessWorkshopStack(final Construct scope, final String id) {
        this(scope, id, null);
    }

    public ServerlessWorkshopStack(final Construct scope, final String id, final StackProps props) {
        super(scope, id, props);

        // Create DynamoDB table
        Table table = Table.Builder.create(this, "dynamoDbTable")
            .billingMode(BillingMode.PAY_PER_REQUEST)
            .partitionKey(Attribute.builder().name("id").type(AttributeType.STRING).build())
            .build();
        final Map<String, String> environment = new HashMap<>();
        environment.put("DYNAMODB_TABLE", table.getTableName());

        // Create the Lambda functions
        Function helloWorld = Function.Builder.create(this, "helloWorld")
            .code(Code.fromAsset(System.getProperty("user.dir") + "/lambda/build/distributions/lambda.zip"))
            .handler("com.myorg.HelloWorldHandler")
            .runtime(Runtime.JAVA_8)
            .build();
        LambdaProxyIntegration helloWorldIntegration = LambdaProxyIntegration.Builder.create().handler(helloWorld).build();
        Function createUrl = Function.Builder.create(this, "createUrl")
            .code(Code.fromAsset(System.getProperty("user.dir") + "/lambda/build/distributions/lambda.zip"))
            .handler("com.myorg.CreateURLHandler")
            .runtime(Runtime.JAVA_8)
            .memorySize(512)
            .timeout(Duration.seconds(10))
            .environment(environment)
            .build();
        LambdaProxyIntegration createUrlIntegration = LambdaProxyIntegration.Builder.create().handler(createUrl).build();
        Function getUrl = Function.Builder.create(this, "getUrl")
                .code(Code.fromAsset(System.getProperty("user.dir") + "/lambda/build/distributions/lambda.zip"))
                .handler("com.myorg.GetURLHandler")
                .runtime(Runtime.JAVA_8)
                .memorySize(512)
                .timeout(Duration.seconds(10))
                .environment(environment)
                .build();
        LambdaProxyIntegration getUrlIntegration = LambdaProxyIntegration.Builder.create().handler(getUrl).build();

        // Grant IAM permissions for table access
        table.grantReadWriteData(createUrl);
        table.grantReadData(getUrl);

        // Expose the functions via HTTP
        HttpApi httpApi = HttpApi.Builder.create(this,"HttpApi").build();
        httpApi.addRoutes(AddRoutesOptions.builder()
                .path("/hello-world")
                .methods(Arrays.asList(HttpMethod.GET))
                .integration(helloWorldIntegration)
                .build()
        );
        httpApi.addRoutes(AddRoutesOptions.builder()
                .path("/")
                .methods(Arrays.asList(HttpMethod.POST))
                .integration(createUrlIntegration)
                .build()
        );
        httpApi.addRoutes(AddRoutesOptions.builder()
                .path("/{short_id}")
                .methods(Arrays.asList(HttpMethod.GET))
                .integration(getUrlIntegration)
                .build()
        );

        // Output the URL for later consumption
        CfnOutput.Builder.create(this, "URL").value(httpApi.getUrl() + "hello-world").build();
    }
}
