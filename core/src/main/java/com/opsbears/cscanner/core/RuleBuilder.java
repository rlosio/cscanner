package com.opsbears.cscanner.core;

import javax.annotation.ParametersAreNonnullByDefault;
import java.util.Map;
import java.util.function.Supplier;

@ParametersAreNonnullByDefault
public interface RuleBuilder<RULETYPE extends Rule, CONNECTION extends CloudProviderConnection, CONFIGURATIONTYPE> {
    String getType();

    Class<CONNECTION> getConnectionType();

    Class<CONFIGURATIONTYPE> getConfigurationType();

    /**
     * @param options the configuration options for this type.
     * @return a certain type
     */
    RULETYPE create(
        CONFIGURATIONTYPE options
    );
}
