package com.opsbears.cscanner.core;

import javax.annotation.ParametersAreNonnullByDefault;
import java.util.Map;
import java.util.function.Supplier;

@ParametersAreNonnullByDefault
public interface ConnectionBuilder<CONNECTIONTYPE> {
    String getType();

    Class<CONNECTIONTYPE> getConnectionType();

    /**
     *
     * @param options the configuration map received from the configuration file.
     * @return a provider for the connection.
     */
    Supplier<CONNECTIONTYPE> create(Map<String, Object> options);
}
