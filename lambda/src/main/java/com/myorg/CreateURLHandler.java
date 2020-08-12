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
import software.amazon.awssdk.services.dynamodb.model.PutItemRequest;

import java.math.BigInteger;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

public class CreateURLHandler implements RequestHandler<APIGatewayProxyRequestEvent, APIGatewayProxyResponseEvent> {
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
        Gson gson = new Gson();
        Map<String, String> payload = gson.fromJson(input.getBody(), Map.class);
        LambdaLogger logger = context.getLogger();
        logger.log("Got URL: " + payload.get("url"));
        String shortId = shortenURL(payload.get("url"));
        logger.log("Shortened to " + shortId);

        HashMap<String, AttributeValue> item = new HashMap<String,AttributeValue>();
        item.put("id", AttributeValue.builder().s(shortId).build());
        item.put("url", AttributeValue.builder().s(payload.get("url")).build());

        try {
            this.getDynamoDbClient().putItem(PutItemRequest.builder().tableName(System.getenv("DYNAMODB_TABLE")).item(item).build());
        } catch (Exception e) {
            logger.log(e.getMessage());
        }

        APIGatewayProxyResponseEvent response = new APIGatewayProxyResponseEvent();
        logger.log(input.getHeaders().toString());
        String shortUrl = "https://" + input.getHeaders().get("host") + "/" + shortId;
        String body = gson.toJson(Collections.singletonMap("short_url", shortUrl));
        response.setBody(body);
        response.setStatusCode(201);
        return response;
    }

    private String shortenURL(String url) {
        byte[] data = url.getBytes();
        BigInteger hash = new BigInteger("cbf29ce484222325", 16);
        for (byte b : data) {
            hash = hash.xor(BigInteger.valueOf((int) b & 0xff));
            hash = hash.multiply(new BigInteger("100000001b3", 16)).mod(new BigInteger("2").pow(64));
        }
        return hash.toString(36);
    }
}
