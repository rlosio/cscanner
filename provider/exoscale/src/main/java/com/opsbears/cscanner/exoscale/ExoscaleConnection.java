package com.opsbears.cscanner.exoscale;

import com.opsbears.cscanner.core.CloudProviderConnection;
import com.opsbears.cscanner.firewall.FirewallClient;
import com.opsbears.cscanner.firewall.FirewallConnection;
import com.opsbears.cscanner.s3.S3Connection;
import com.opsbears.cscanner.s3.S3Factory;

import javax.annotation.ParametersAreNonnullByDefault;

@ParametersAreNonnullByDefault
public class ExoscaleConnection implements CloudProviderConnection, S3Connection, FirewallConnection {
    private final String name;
    private final ExoscaleConfiguration exoscaleConfiguration;
    private final ExoscaleFirewallClient exoscaleFirewallClient;

    public ExoscaleConnection(
        String name,
        ExoscaleConfiguration exoscaleConfiguration
    ) {
        this.name = name;

        this.exoscaleConfiguration = exoscaleConfiguration;
        //todo handle cloudstack config
        exoscaleFirewallClient = new ExoscaleFirewallClient(
            exoscaleConfiguration.key,
            exoscaleConfiguration.secret
        );
    }

    @Override
    public S3Factory getS3Factory() {
        return new ExoscaleS3ClientSupplier(exoscaleConfiguration);
    }

    @Override
    public String getConnectionName() {
        return name;
    }

    @Override
    public FirewallClient getFirewallClient() {
        return exoscaleFirewallClient;
    }
}
