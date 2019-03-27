package com.opsbears.cscanner.s3;

import com.amazonaws.services.s3.AmazonS3;
import com.opsbears.cscanner.core.ConnectionBuilder;

import javax.annotation.ParametersAreNonnullByDefault;
import java.util.Map;

@ParametersAreNonnullByDefault
public class S3ConnectionBuilder implements ConnectionBuilder<AmazonS3> {
    @Override
    public String getType() {
        return "s3";
    }

    @Override
    public Class<AmazonS3> getConnectionType() {
        return AmazonS3.class;
    }

    @Override
    public S3ClientSupplier create(Map<String, Object> options) {
        //todo clean this up, provider support for AWS profiles, etc
        //todo build in more checks
        return new S3ClientSupplier(
            (String) options.get("AWS_ACCESS_KEY_ID"),
            (String) options.get("AWS_SECRET_ACCESS_KEY"),
            (String) options.get("AWS_REGION"),
            (String) options.get("AWS_ENDPOINT")
        );
    }
}
