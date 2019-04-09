package com.opsbears.cscanner.exoscale;

import com.opsbears.cscanner.core.CloudProvider;
import com.opsbears.cscanner.firewall.FirewallCloudProvider;
import com.opsbears.cscanner.firewall.FirewallConnection;
import com.opsbears.cscanner.s3.S3CloudProvider;

import javax.annotation.ParametersAreNonnullByDefault;
import java.util.Map;

@ParametersAreNonnullByDefault
public class ExoscaleCloudProvider implements CloudProvider<ExoscaleConfiguration, ExoscaleConnection>, S3CloudProvider<ExoscaleConfiguration, ExoscaleConnection>, FirewallCloudProvider<ExoscaleConfiguration, ExoscaleConnection> {
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
        ExoscaleConfiguration configuration
    ) {
        return new ExoscaleConnection(name, configuration);
    }
}
