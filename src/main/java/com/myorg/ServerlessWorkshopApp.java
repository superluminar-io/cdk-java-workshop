package com.myorg;

import software.amazon.awscdk.core.App;

public class ServerlessWorkshopApp {
    public static void main(final String[] args) {
        App app = new App();

        new ServerlessWorkshopStack(app, "ServerlessWorkshopCdkStack");

        app.synth();
    }
}
