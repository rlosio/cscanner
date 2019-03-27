package com.opsbears.cscanner.exoscale;

import com.opsbears.cscanner.core.CloudProvider;
import com.opsbears.cscanner.s3.S3CloudProvider;

import javax.annotation.ParametersAreNonnullByDefault;
import java.util.Map;

@ParametersAreNonnullByDefault
public class ExoscaleCloudProvider implements CloudProvider<ExoscaleConfiguration, ExoscaleConnection>, S3CloudProvider<ExoscaleConfiguration, ExoscaleConnection> {
    @Override
    public String getName() {
        return "exoscale";
    }

    @Override
    public Class<ExoscaleConfiguration> getConfigurationType() {
        return ExoscaleConfiguration.class;
    }

    @Override
    public Class<ExoscaleConnection> getConnectionType() {
        return ExoscaleConnection.class;
    }

    @Override
    public ExoscaleConnection getConnection(
        String name,
        Map<String, Object> configuration
    ) {
        ExoscaleConfiguration exoscaleConfiguration = new ExoscaleConfiguration(
            (String) configuration.getOrDefault("key", null),
            (String) configuration.getOrDefault("secret", null),
            (String) configuration.getOrDefault("cloudStackConfig", null),
            (String) configuration.getOrDefault("profile", null)
        );

        return new ExoscaleConnection(name, exoscaleConfiguration);
    }
}
