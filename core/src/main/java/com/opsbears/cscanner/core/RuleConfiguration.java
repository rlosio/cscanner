package com.opsbears.cscanner.core;

import javax.annotation.ParametersAreNonnullByDefault;
import java.util.List;
import java.util.Map;

public class RuleConfiguration {
    public final String type;
    public final List<String> connections;
    public final Map<String, Object> options;

    public RuleConfiguration(
        String rule,
        List<String> connections,
        Map<String, Object> options
    ) {
        this.type = rule;
        this.connections = connections;
        this.options = options;
    }
}
