package com.opsbears.cscanner.core;

import javax.annotation.ParametersAreNonnullByDefault;
import java.util.Collections;
import java.util.List;

@ParametersAreNonnullByDefault
public interface Plugin {
    default List<ConnectionBuilder<?>> getSupportedConnections() {
        return Collections.emptyList();
    }

    default List<RuleBuilder<?, ?>> getSupportedRules() {
        return Collections.emptyList();
    }

    default List<ConfigLoader> getConfigLoaders() {
        return Collections.emptyList();
    }
}
