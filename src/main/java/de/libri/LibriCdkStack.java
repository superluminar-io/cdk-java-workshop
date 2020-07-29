package de.libri;

import software.amazon.awscdk.core.Construct;
import software.amazon.awscdk.core.Stack;
import software.amazon.awscdk.core.StackProps;
import software.amazon.awscdk.services.s3.Bucket;
import software.amazon.awscdk.services.s3.BucketEncryption;

public class LibriCdkStack extends Stack {
    public LibriCdkStack(final Construct scope, final String id) {
        this(scope, id, null);
    }

    public LibriCdkStack(final Construct scope, final String id, final StackProps props) {
        super(scope, id, props);
        Bucket bucket = Bucket.Builder.create(this, "MyBucket")
                .versioned(true)
                .encryption(BucketEncryption.KMS_MANAGED)
                .build();

        // The code that defines your stack goes here
    }
}
