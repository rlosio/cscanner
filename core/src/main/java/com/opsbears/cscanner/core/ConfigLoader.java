package com.opsbears.cscanner.core;

import javax.annotation.ParametersAreNonnullByDefault;
import java.util.List;
import java.util.Map;

@ParametersAreNonnullByDefault
public interface ConfigLoader {
    Map<String, ConnectionConfiguration> loadConnectionConfigurations();
    List<RuleConfiguration> loadRuleConfigurations();

    class ConnectionConfiguration {
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

    class RuleConfiguration {
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
}
