package com.opsbears.cscanner.core;

import javax.annotation.ParametersAreNonnullByDefault;
import java.util.Map;

@ParametersAreNonnullByDefault
class CloudProviderConnectionFactory {
    private final ConfigurationConverter configurationConverter = new ConfigurationConverter();

    <CONFIGURATIONTYPE, CONNECTIONTYPE extends CloudProviderConnection> CONNECTIONTYPE create(
        CloudProvider<CONFIGURATIONTYPE, CONNECTIONTYPE> cloudProvider,
        String connectionKey,
        Map<String, Object> options
    ) {
        return cloudProvider.getConnection(connectionKey,
            configurationConverter.convert(
                options,
                cloudProvider.getConfigurationType()
            )
        );
    }
}
