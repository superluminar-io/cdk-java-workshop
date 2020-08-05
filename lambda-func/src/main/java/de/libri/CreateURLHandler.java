package de.libri;

import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.LambdaLogger;
import com.amazonaws.services.lambda.runtime.RequestHandler;
import com.amazonaws.services.lambda.runtime.events.APIGatewayProxyRequestEvent;
import com.amazonaws.services.lambda.runtime.events.APIGatewayProxyResponseEvent;
import com.google.gson.Gson;
import software.amazon.awssdk.services.dynamodb.DynamoDbClient;
import software.amazon.awssdk.services.dynamodb.model.AttributeValue;
import software.amazon.awssdk.services.dynamodb.model.DynamoDbException;
import software.amazon.awssdk.services.dynamodb.model.PutItemRequest;

import java.math.BigInteger;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

public class CreateURLHandler implements RequestHandler<APIGatewayProxyRequestEvent, APIGatewayProxyResponseEvent> {
    private static final BigInteger INIT64 = new BigInteger("cbf29ce484222325", 16);
    private static final BigInteger MOD64 = new BigInteger("2").pow(64);
    private static final BigInteger PRIME64 = new BigInteger("100000001b3", 16);
    private DynamoDbClient dynamoDbClient;

    public DynamoDbClient getDynamoDbClient() {
        if (this.dynamoDbClient == null) {
            this.dynamoDbClient = DynamoDbClient.builder().build();
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
        BigInteger hash = hashURL(payload.get("url").getBytes());
        String shortId = hash.toString(36);
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

    private BigInteger hashURL(byte[] data) {
        BigInteger hash = INIT64;

        for (byte b : data) {
            hash = hash.xor(BigInteger.valueOf((int) b & 0xff));
            hash = hash.multiply(PRIME64).mod(MOD64);
        }

        return hash;
    }
}
