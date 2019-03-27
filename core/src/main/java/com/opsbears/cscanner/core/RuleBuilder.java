package com.opsbears.cscanner.core;

import javax.annotation.ParametersAreNonnullByDefault;
import java.util.Map;
import java.util.function.Supplier;

@ParametersAreNonnullByDefault
public interface RuleBuilder<RULETYPE extends Rule, CONNECTIONTYPE> {
    String getType();

    Class<CONNECTIONTYPE> getConnectionType();

    /**
     * @param options the configuration options for this type.
     * @return a certain type
     */
    RULETYPE create(
        Map<String, Object> options,
        String connectionName,
        Supplier<CONNECTIONTYPE> connectionFactory
    );
}
