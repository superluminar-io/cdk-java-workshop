package de.libri;

import software.amazon.awscdk.core.App;

public class LibriCdkApp {
    public static void main(final String[] args) {
        App app = new App();

        new LibriCdkStack(app, "LibriCdkStack");

        app.synth();
    }
}
