package com.opsbears.cscanner.firewall;

import com.opsbears.cscanner.core.CloudProviderConnection;

import javax.annotation.ParametersAreNonnullByDefault;
import java.util.List;

@ParametersAreNonnullByDefault
public interface FirewallConnection extends CloudProviderConnection {
    String RESOURCE_TYPE = "firewallGroup";

    FirewallClient getFirewallClient();
}
