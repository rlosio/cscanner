package com.opsbears.cscanner.firewall;

import com.opsbears.cscanner.core.CloudProvider;

import javax.annotation.ParametersAreNonnullByDefault;

@ParametersAreNonnullByDefault
public interface FirewallCloudProvider<CONFIGURATIONTYPE, CONNECTIONTYPE extends FirewallConnection> extends CloudProvider<CONFIGURATIONTYPE, CONNECTIONTYPE> {
}
