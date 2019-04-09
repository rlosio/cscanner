package com.opsbears.cscanner.aws;

import com.opsbears.cscanner.core.CloudProviderConnection;
import com.opsbears.cscanner.firewall.FirewallClient;
import com.opsbears.cscanner.firewall.FirewallConnection;
import com.opsbears.cscanner.s3.S3Connection;
import com.opsbears.cscanner.s3.S3Factory;

import javax.annotation.ParametersAreNonnullByDefault;

@ParametersAreNonnullByDefault
public class AWSConnection implements CloudProviderConnection, S3Connection, FirewallConnection {
    private final String name;
    private final AWSConfiguration awsConfiguration;
    private final AWSFirewallClient awsFirewallClient;

    public AWSConnection(
        String name,
        AWSConfiguration awsConfiguration
    ) {
        this.name = name;
        this.awsConfiguration = awsConfiguration;

        awsFirewallClient = new AWSFirewallClient(awsConfiguration);
    }

    @Override
    public S3Factory getS3Factory() {
        return new AWSS3ClientSupplier(awsConfiguration);
    }

    @Override
    public String getConnectionName() {
        return name;
    }

    @Override
    public FirewallClient getFirewallClient() {
        return awsFirewallClient;
    }
}
