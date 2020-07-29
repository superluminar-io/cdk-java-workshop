package com.myorg;

import software.amazon.awscdk.core.App;

import java.util.Arrays;

public class LibriCdkApp {
    public static void main(final String[] args) {
        App app = new App();

        new LibriCdkStack(app, "LibriCdkStack");

        app.synth();
    }
}
