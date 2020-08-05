package de.libri;

import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.LambdaLogger;
import com.amazonaws.services.lambda.runtime.RequestHandler;
import com.amazonaws.services.lambda.runtime.events.APIGatewayProxyRequestEvent;
import com.amazonaws.services.lambda.runtime.events.APIGatewayProxyResponseEvent;
import com.google.gson.Gson;

import java.math.BigInteger;
import java.util.Collections;
import java.util.HashMap;

public class CreateURLHandler implements RequestHandler<APIGatewayProxyRequestEvent, APIGatewayProxyResponseEvent> {
    private static final BigInteger INIT64 = new BigInteger("cbf29ce484222325", 16);
    private static final BigInteger MOD64 = new BigInteger("2").pow(64);
    private static final BigInteger PRIME64 = new BigInteger("100000001b3", 16);


    public class Payload {
        public String url;
    }

    @Override
    public APIGatewayProxyResponseEvent handleRequest(APIGatewayProxyRequestEvent input, Context context) {
        Gson gson = new Gson();
        Payload payload = gson.fromJson(input.getBody(), Payload.class);
        LambdaLogger logger = context.getLogger();
        logger.log("Got URL: " + payload.url);
        BigInteger hash = hashURL(payload.url.getBytes());
        String shortId = hash.toString(36);
        logger.log("Shortened to " + shortId);
        APIGatewayProxyResponseEvent response = new APIGatewayProxyResponseEvent();
        String shortUrl = "https://" + input.getHeaders().get("Host") + "/" + shortId;
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
