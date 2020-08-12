package com.myorg;

import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.LambdaLogger;
import com.amazonaws.services.lambda.runtime.events.APIGatewayProxyRequestEvent;
import com.amazonaws.services.lambda.runtime.events.APIGatewayProxyResponseEvent;
import org.junit.jupiter.api.Test;
import software.amazon.awssdk.services.dynamodb.DynamoDbClient;
import software.amazon.awssdk.services.dynamodb.model.AttributeValue;
import software.amazon.awssdk.services.dynamodb.model.GetItemRequest;
import software.amazon.awssdk.services.dynamodb.model.GetItemResponse;

import java.util.Collections;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.hasEntry;

class GetURLHandlerTest {

    public static final String SOME_SHORT_ID = "123abc";
    public static final String SOME_URL = "https://libri.de";

    @Test
    void handleRequest() {
        GetURLHandler handler = new GetURLHandler();
        DynamoDbClient client = mock(DynamoDbClient.class);
        handler.setDynamoDbClient(client);
        APIGatewayProxyRequestEvent request = new APIGatewayProxyRequestEvent();
        request.setPathParameters(Collections.singletonMap("short_id", SOME_SHORT_ID));
        when(client.getItem(any(GetItemRequest.class))).thenReturn(GetItemResponse.builder().item(Collections.singletonMap("url", AttributeValue.builder().s(SOME_URL).build())).build());
        APIGatewayProxyResponseEvent response = handler.handleRequest(request, getContext());
        assertThat(response.getHeaders(), hasEntry("Location", SOME_URL));
    }

    private Context getContext() {
        Context context = mock(Context.class);
        when(context.getLogger()).thenReturn(mock(LambdaLogger.class));
        return context;
    }
}