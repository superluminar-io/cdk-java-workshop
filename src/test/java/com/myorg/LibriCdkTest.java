package com.myorg;

import org.junit.Ignore;
import software.amazon.awscdk.core.App;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import org.junit.Test;

import java.io.IOException;

import static org.junit.Assert.assertEquals;

public class LibriCdkTest {
    private final static ObjectMapper JSON =
        new ObjectMapper().configure(SerializationFeature.INDENT_OUTPUT, true);

    @Test
    @Ignore
    public void testStack() throws IOException {
        App app = new App();
        ServerlessWorkshopStack stack = new ServerlessWorkshopStack(app, "test");

        // synthesize the stack to a CloudFormation template and compare against
        // a checked-in JSON file.
        JsonNode actual = JSON.valueToTree(app.synth().getStackArtifact(stack.getArtifactId()).getTemplate());
        assertEquals(new ObjectMapper().createObjectNode(), actual);
    }
}
