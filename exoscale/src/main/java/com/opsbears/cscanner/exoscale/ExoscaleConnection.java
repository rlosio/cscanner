package com.opsbears.cscanner.exoscale;

import com.amazonaws.services.s3.AmazonS3;
import com.opsbears.cscanner.core.CloudProviderConnection;
import com.opsbears.cscanner.s3.S3Connection;
import com.opsbears.cscanner.s3.S3Factory;

import javax.annotation.ParametersAreNonnullByDefault;
import java.util.function.Supplier;

@ParametersAreNonnullByDefault
public class ExoscaleConnection implements CloudProviderConnection, S3Connection {
    private final String name;
    private final ExoscaleConfiguration awsConfiguration;

    public ExoscaleConnection(
        String name,
        ExoscaleConfiguration awsConfiguration
    ) {
        this.name = name;

        this.awsConfiguration = awsConfiguration;
    }

    @Override
    public S3Factory getS3Factory() {
        return new ExoscaleS3ClientSupplier(awsConfiguration);
    }

    @Override
    public String getConnectionName() {
        return name;
    }
}
