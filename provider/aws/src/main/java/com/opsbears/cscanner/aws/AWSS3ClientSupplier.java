package com.opsbears.cscanner.aws;

import com.amazonaws.regions.Regions;
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
        builder.withCredentials(configuration.getCredentialsProvider());

        if (region != null) {
            builder.withRegion(region);
        } else {
            builder.withRegion(Regions.DEFAULT_REGION);
        }

        builder.withForceGlobalBucketAccessEnabled(true);

        return builder.build();
    }
}
