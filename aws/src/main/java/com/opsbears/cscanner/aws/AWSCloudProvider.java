package com.opsbears.cscanner.aws;

import com.opsbears.cscanner.core.CloudProvider;
import com.opsbears.cscanner.s3.S3CloudProvider;

import javax.annotation.ParametersAreNonnullByDefault;
import java.util.Map;

@ParametersAreNonnullByDefault
public class AWSCloudProvider implements CloudProvider<AWSConfiguration, AWSConnection>, S3CloudProvider<AWSConfiguration, AWSConnection> {
    @Override
    public String getName() {
        return "aws";
    }

    @Override
    public Class<AWSConfiguration> getConfigurationType() {
        return AWSConfiguration.class;
    }

    @Override
    public Class<AWSConnection> getConnectionType() {
        return AWSConnection.class;
    }

    @Override
    public AWSConnection getConnection(
        String name,
        Map<String, Object> configuration
    ) {
        AWSConfiguration awsConfiguration = new AWSConfiguration(
            (String)configuration.getOrDefault("accessKeyId", null),
            (String)configuration.getOrDefault("secretAccessKey", null),
            (String)configuration.getOrDefault("sessionToken", null),
            (String)configuration.getOrDefault("profile", null)
        );

        return new AWSConnection(name, awsConfiguration);
    }
}
