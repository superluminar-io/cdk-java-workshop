package de.libri;

import software.amazon.awscdk.core.CfnOutput;
import software.amazon.awscdk.core.Construct;
import software.amazon.awscdk.core.Stack;
import software.amazon.awscdk.core.StackProps;
import software.amazon.awscdk.services.apigatewayv2.AddRoutesOptions;
import software.amazon.awscdk.services.apigatewayv2.HttpMethod;
import software.amazon.awscdk.services.apigatewayv2.LambdaProxyIntegration;
import software.amazon.awscdk.services.dynamodb.Attribute;
import software.amazon.awscdk.services.dynamodb.AttributeType;
import software.amazon.awscdk.services.dynamodb.BillingMode;
import software.amazon.awscdk.services.dynamodb.Table;
import software.amazon.awscdk.services.lambda.Code;
import software.amazon.awscdk.services.lambda.Function;
import software.amazon.awscdk.services.lambda.Runtime;
import software.amazon.awscdk.services.s3.Bucket;
import software.amazon.awscdk.services.apigatewayv2.HttpApi;
import software.amazon.awscdk.services.s3.BucketEncryption;

import java.util.Arrays;

public class LibriCdkStack extends Stack {
    public LibriCdkStack(final Construct scope, final String id) {
        this(scope, id, null);
    }

    public LibriCdkStack(final Construct scope, final String id, final StackProps props) {
        super(scope, id, props);

        // Create DynamoDB table
        Table table = Table.Builder.create(this, "dynamoDbTable")
                .tableName("short-urls")
                .billingMode(BillingMode.PAY_PER_REQUEST)
                .partitionKey(Attribute.builder().name("id").type(AttributeType.STRING).build())
                .build();

        // Create the Lambda functions
        Function helloWorld = Function.Builder.create(this, "helloWorld").code(Code.fromAsset(System.getProperty("user.dir") + "/lambda-func/build/distributions/lambda-func-1.0-SNAPSHOT.zip")).handler("de.libri.HelloWorldHandler").runtime(Runtime.JAVA_8).build();
        LambdaProxyIntegration helloWorldIntegration = LambdaProxyIntegration.Builder.create().handler(helloWorld).build();
        Function createUrl = Function.Builder.create(this, "createUrl").code(Code.fromAsset(System.getProperty("user.dir") + "/lambda-func/build/distributions/lambda-func-1.0-SNAPSHOT.zip")).handler("de.libri.CreateURLHandler").runtime(Runtime.JAVA_8).build();
        LambdaProxyIntegration createUrlIntegration = LambdaProxyIntegration.Builder.create().handler(createUrl).build();

        // Grant IAM permissions for table access
        table.grantReadWriteData(createUrl);

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

        // Output the URL for later consumption
        CfnOutput.Builder.create(this, "URL").value(httpApi.getUrl() + "hello-world").build();
    }
}
