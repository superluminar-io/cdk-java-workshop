package de.libri;

import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.LambdaLogger;
import com.amazonaws.services.lambda.runtime.events.APIGatewayProxyRequestEvent;
import com.amazonaws.services.lambda.runtime.events.APIGatewayProxyResponseEvent;
import com.google.gson.Gson;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class ExampleHandlerTest {

    @Test
    void handleRequest() {
        ExampleHandler handler = new ExampleHandler();
        APIGatewayProxyRequestEvent request = new APIGatewayProxyRequestEvent();
        Gson gson = new Gson();
        String json = gson.toJson(new Data("spam", "eggs"));
        request.setBody(json);
        APIGatewayProxyResponseEvent response = handler.handleRequest(request, getContext());
        Data data = gson.fromJson(response.getBody(), Data.class);
        assertEquals("spam", data.getFoo());
        assertEquals("eggs", data.getBar());
    }

    private Context getContext() {
        Context context = mock(Context.class);
        when(context.getLogger()).thenReturn(mock(LambdaLogger.class));
        return context;
    }
}