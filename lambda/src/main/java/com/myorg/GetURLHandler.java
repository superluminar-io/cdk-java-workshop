package com.myorg;

import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.LambdaLogger;
import com.amazonaws.services.lambda.runtime.RequestHandler;
import com.amazonaws.services.lambda.runtime.events.APIGatewayProxyRequestEvent;
import com.amazonaws.services.lambda.runtime.events.APIGatewayProxyResponseEvent;
import com.google.gson.Gson;
import software.amazon.awssdk.auth.credentials.EnvironmentVariableCredentialsProvider;
import software.amazon.awssdk.core.SdkSystemSetting;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.dynamodb.DynamoDbClient;
import software.amazon.awssdk.services.dynamodb.model.AttributeValue;
import software.amazon.awssdk.services.dynamodb.model.GetItemRequest;
import software.amazon.awssdk.services.dynamodb.model.GetItemResponse;
import software.amazon.awssdk.services.dynamodb.model.PutItemRequest;

import java.math.BigInteger;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

public class GetURLHandler implements RequestHandler<APIGatewayProxyRequestEvent, APIGatewayProxyResponseEvent> {
    private DynamoDbClient dynamoDbClient;

    public DynamoDbClient getDynamoDbClient() {
        if (this.dynamoDbClient == null) {
            this.dynamoDbClient = DynamoDbClient.builder().credentialsProvider(EnvironmentVariableCredentialsProvider.create())
                    .region(Region.of(System.getenv(SdkSystemSetting.AWS_REGION.environmentVariable()))).build();
        }
        return this.dynamoDbClient;
    }

    public void setDynamoDbClient(DynamoDbClient dynamoDbClient) {
        this.dynamoDbClient = dynamoDbClient;
    }

    @Override
    public APIGatewayProxyResponseEvent handleRequest(APIGatewayProxyRequestEvent input, Context context) {
        LambdaLogger logger = context.getLogger();
        String shortId = input.getPathParameters().get("short_id");
        logger.log("Looking for: " + shortId);
        APIGatewayProxyResponseEvent response = new APIGatewayProxyResponseEvent();

        try {
            GetItemResponse itemResponse = this.getDynamoDbClient().getItem(GetItemRequest.builder()
                    .tableName(System.getenv("DYNAMODB_TABLE"))
                    .key(Collections.singletonMap("id", AttributeValue.builder().s(shortId).build())).build());
            if (itemResponse.hasItem()) {
                response.setStatusCode(302);
                response.setHeaders(Collections.singletonMap("Location", itemResponse.item().get("url").s()));
                return response;
            }
        } catch (Exception e) {
            logger.log(e.getMessage());
        }

        response.setStatusCode(404);
        response.setBody("Not found");
        return response;
    }

}
