package com.opsbears.cscanner.s3;

import com.amazonaws.ClientConfiguration;
import com.amazonaws.auth.AWSCredentials;
import com.amazonaws.auth.AWSCredentialsProvider;
import com.amazonaws.client.builder.AwsClientBuilder;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.AmazonS3ClientBuilder;

import javax.annotation.Nullable;
import javax.annotation.ParametersAreNonnullByDefault;
import java.util.function.Supplier;

@ParametersAreNonnullByDefault
class S3ClientSupplier implements Supplier<AmazonS3> {
    @Nullable
    private final String accessKeyId;
    @Nullable
    private final String secretAccessKey;
    @Nullable
    private final String region;
    @Nullable
    private final String endpoint;

    S3ClientSupplier(
        @Nullable
        String accessKeyId,
        @Nullable
        String secretAccessKey,
        @Nullable
        String region,
        @Nullable
        String endpoint
    ) {
        this.accessKeyId = accessKeyId;
        this.secretAccessKey = secretAccessKey;
        this.region = region;
        this.endpoint = endpoint;
    }

    @Override
    public AmazonS3 get() {
        AmazonS3ClientBuilder builder = AmazonS3ClientBuilder.standard();
        builder.withForceGlobalBucketAccessEnabled(true);
        if (region != null) {
            builder.withRegion(region);
        }
        if (endpoint != null) {
            builder.withEndpointConfiguration(new AwsClientBuilder.EndpointConfiguration(
                endpoint,
                region
            ));
        }
        if (accessKeyId != null && secretAccessKey != null) {
            builder.withCredentials(new AWSCredentialsProvider() {
                @Override
                public AWSCredentials getCredentials() {
                    return new AWSCredentials() {
                        @Override
                        public String getAWSAccessKeyId() {
                            return accessKeyId;
                        }

                        @Override
                        public String getAWSSecretKey() {
                            return secretAccessKey;
                        }
                    };
                }

                @Override
                public void refresh() {
                }
            });
        }
        return builder.build();
    }
}
