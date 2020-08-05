package de.libri;

import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.LambdaLogger;
import com.amazonaws.services.lambda.runtime.events.APIGatewayProxyRequestEvent;
import com.amazonaws.services.lambda.runtime.events.APIGatewayProxyResponseEvent;
import com.google.gson.Gson;
import org.junit.jupiter.api.Test;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class CreateURLHandlerTest {

    @Test
    void handleRequest() {
        CreateURLHandler handler = new CreateURLHandler();
        APIGatewayProxyRequestEvent request = new APIGatewayProxyRequestEvent();
        Gson gson = new Gson();
        // Set the URL to shorten
        String body = gson.toJson(Collections.singletonMap("url", "https://libri.de"));
        request.setBody(body);
        // Inject Host name for URL shortener service
        request.setHeaders(Collections.singletonMap("Host", "example.com"));
        APIGatewayProxyResponseEvent response = handler.handleRequest(request, getContext());
        Map data = gson.fromJson(response.getBody(), Map.class);
        assertEquals("https://example.com/1vk6h2ayvbhbd", data.get("short_url"));
    }

    private Context getContext() {
        Context context = mock(Context.class);
        when(context.getLogger()).thenReturn(mock(LambdaLogger.class));
        return context;
    }
}