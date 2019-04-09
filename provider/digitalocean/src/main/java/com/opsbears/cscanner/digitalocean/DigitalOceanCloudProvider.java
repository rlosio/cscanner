package com.opsbears.cscanner.digitalocean;

import com.opsbears.cscanner.core.CloudProvider;
import com.opsbears.cscanner.s3.S3CloudProvider;

import javax.annotation.ParametersAreNonnullByDefault;

@ParametersAreNonnullByDefault
public class DigitalOceanCloudProvider implements CloudProvider<DigitalOceanConfiguration, DigitalOceanConnection>, S3CloudProvider<DigitalOceanConfiguration, DigitalOceanConnection> {
    @Override
    public String getName() {
        return "digitalocean";
    }

    @Override
    public Class<DigitalOceanConfiguration> getConfigurationType() {
        return DigitalOceanConfiguration.class;
    }

    @Override
    public Class<DigitalOceanConnection> getConnectionType() {
        return DigitalOceanConnection.class;
    }

    @Override
    public DigitalOceanConnection getConnection(
        String name,
        DigitalOceanConfiguration configuration
    ) {
        return new DigitalOceanConnection(configuration);
    }
}
