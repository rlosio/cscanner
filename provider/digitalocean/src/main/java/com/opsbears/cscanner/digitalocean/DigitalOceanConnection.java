package com.opsbears.cscanner.digitalocean;

import com.opsbears.cscanner.core.CloudProviderConnection;
import com.opsbears.cscanner.s3.S3Connection;
import com.opsbears.cscanner.s3.S3Factory;

import javax.annotation.ParametersAreNonnullByDefault;

@ParametersAreNonnullByDefault
public class DigitalOceanConnection implements CloudProviderConnection, S3Connection {
    private final DigitalOceanConfiguration configuration;

    public DigitalOceanConnection(DigitalOceanConfiguration configuration) {
        this.configuration = configuration;
    }

    @Override
    public S3Factory getS3Factory() {
        return new DigitalOceanS3ClientSupplier(configuration);
    }

    @Override
    public String getConnectionName() {
        return "digitalocean";
    }
}
