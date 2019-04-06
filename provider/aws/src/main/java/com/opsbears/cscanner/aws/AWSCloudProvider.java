package com.opsbears.cscanner.aws;

import com.opsbears.cscanner.core.CloudProvider;
import com.opsbears.cscanner.firewall.FirewallCloudProvider;
import com.opsbears.cscanner.s3.S3CloudProvider;

import javax.annotation.ParametersAreNonnullByDefault;
import java.util.Map;

@ParametersAreNonnullByDefault
public class AWSCloudProvider implements CloudProvider<AWSConfiguration, AWSConnection>, S3CloudProvider<AWSConfiguration, AWSConnection>, FirewallCloudProvider<AWSConfiguration, AWSConnection> {
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
        AWSConfiguration configuration
    ) {
        configuration.validateCredentials();

        return new AWSConnection(name, configuration);
    }
}
