package com.opsbears.cscanner.core;

import javax.annotation.ParametersAreNonnullByDefault;
import java.util.Map;

@ParametersAreNonnullByDefault
public interface CloudProvider<CONFIGURATIONTYPE, CONNECTIONTYPE extends CloudProviderConnection> {
    /**
     * @return the name of the cloud provider it can be referenced as in the configuration. E.g. "aws" or "exoscale"
     */
    String getName();

    Class<CONFIGURATIONTYPE> getConfigurationType();

    Class<CONNECTIONTYPE> getConnectionType();

    CONNECTIONTYPE getConnection(
        String name,
        Map<String, Object> configuration
    );
}
