package com.opsbears.cscanner.core;

import javax.annotation.ParametersAreNonnullByDefault;
import java.util.Map;

public class ConnectionConfiguration {
    public final String type;
    public final Map<String, Object> options;

    public ConnectionConfiguration(
        String type,
        Map<String, Object> options
    ) {
        this.type = type;
        this.options = options;
    }
}
