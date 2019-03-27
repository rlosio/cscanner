package com.opsbears.cscanner.aws;

import com.amazonaws.auth.*;
import com.amazonaws.auth.profile.ProfileCredentialsProvider;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.AmazonS3ClientBuilder;
import com.opsbears.cscanner.s3.S3Factory;

import javax.annotation.Nullable;
import javax.annotation.ParametersAreNonnullByDefault;

@ParametersAreNonnullByDefault
public class AWSS3ClientSupplier implements S3Factory {
    private final AWSConfiguration configuration;

    public AWSS3ClientSupplier(AWSConfiguration configuration) {
        this.configuration = configuration;
    }

    @Override
    public AmazonS3 get(@Nullable String region) {
        AmazonS3ClientBuilder builder = AmazonS3ClientBuilder.standard();
        if (configuration.accessKeyId != null && configuration.secretAccessKey != null) {
            if (configuration.sessionToken != null) {
                builder.withCredentials(new AWSSessionCredentialsProvider() {
                    @Override
                    public AWSSessionCredentials getCredentials() {
                        return new AWSSessionCredentials() {
                            @Override
                            public String getSessionToken() {
                                return configuration.sessionToken;
                            }

                            @Override
                            public String getAWSAccessKeyId() {
                                return configuration.accessKeyId;
                            }

                            @Override
                            public String getAWSSecretKey() {
                                return configuration.secretAccessKey;
                            }
                        };
                    }

                    @Override
                    public void refresh() {

                    }
                });
            } else {
                builder.withCredentials(new AWSCredentialsProvider() {
                    @Override
                    public AWSCredentials getCredentials() {
                        return new AWSCredentials() {
                            @Override
                            public String getAWSAccessKeyId() {
                                return configuration.accessKeyId;
                            }

                            @Override
                            public String getAWSSecretKey() {
                                return configuration.secretAccessKey;
                            }
                        };
                    }

                    @Override
                    public void refresh() {

                    }
                });
            }
        } else if (configuration.profile != null) {
            builder.withCredentials(new ProfileCredentialsProvider(configuration.profile));
        } else {
            builder.withCredentials(new DefaultAWSCredentialsProviderChain());
        }

        if (region != null) {
            builder.withRegion(region);
        }

        builder.withForceGlobalBucketAccessEnabled(true);

        return builder.build();
    }
}
