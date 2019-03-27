package com.opsbears.cscanner.core;

import javax.annotation.ParametersAreNonnullByDefault;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Supplier;

@ParametersAreNonnullByDefault
public class ScannerCore {
    private final List<Plugin> plugins;

    public ScannerCore(List<Plugin> plugins) {
        this.plugins = plugins;
    }

    public List<Rule.Result> scan() {
        List<ConfigLoader> configLoaders = new ArrayList<>();
        List<ConnectionBuilder<?>> connectionBuilders = new ArrayList<>();
        List<RuleBuilder<?, ?>> ruleBuilders = new ArrayList<>();
        for (Plugin plugin : plugins) {
            configLoaders.addAll(plugin.getConfigLoaders());
            connectionBuilders.addAll(plugin.getSupportedConnections());
            ruleBuilders.addAll(plugin.getSupportedRules());
        }

        Map<String, ConfigLoader.ConnectionConfiguration> connectionConfigs = new HashMap<>();
        List<ConfigLoader.RuleConfiguration> ruleConfigurations = new ArrayList<>();
        for (ConfigLoader configLoader : configLoaders) {
            connectionConfigs.putAll(configLoader.loadConnectionConfigurations());
            ruleConfigurations.addAll(configLoader.loadRuleConfigurations());
        }

        Map<String, ConnectionBuilder<?>> connectionBuilderMap = new HashMap<>();
        for (String connectionName : connectionConfigs.keySet()) {
            ConfigLoader.ConnectionConfiguration connectionConfig = connectionConfigs.get(connectionName);
            ConnectionBuilder<?> foundConnectionBuilder = null;
            for (ConnectionBuilder<?> connectionBuilder : connectionBuilders) {
                if (connectionBuilder.getType().equalsIgnoreCase(connectionConfig.type)) {
                    foundConnectionBuilder = connectionBuilder;
                    break;
                }
            }
            if (foundConnectionBuilder == null) {
                throw new RuntimeException("Unable to find connection builder for connection type " + connectionConfig.type + " in connection " + connectionName);
            }

            connectionBuilderMap.put(connectionName, foundConnectionBuilder);
        }

        List<Rule> finalRuleList = new ArrayList<>();
        for (ConfigLoader.RuleConfiguration ruleConfiguration : ruleConfigurations) {
            String ruleType = ruleConfiguration.type;
            List<String> ruleConnectionNames = ruleConfiguration.connections;
            Map<String, Object> ruleOptions = ruleConfiguration.options;

            RuleBuilder<?, ?> foundRuleBuilder = null;
            for (RuleBuilder<?, ?> ruleBuilder : ruleBuilders) {
                if (ruleBuilder.getType().equalsIgnoreCase(ruleType)) {
                    foundRuleBuilder = ruleBuilder;
                    break;
                }
            }
            if (foundRuleBuilder == null) {
                throw new RuntimeException("No rule builder found for rule type " + ruleType);
            }

            if (ruleConnectionNames.isEmpty()) {
                ruleConnectionNames.addAll(connectionBuilderMap.keySet());
            }
            for (String connectionName : ruleConnectionNames) {
                if (!connectionBuilderMap.containsKey(connectionName)) {
                    throw new RuntimeException("No connection with name " + connectionName + " found");
                }
                ConnectionBuilder<?> connectionBuilder = connectionBuilderMap.get(connectionName);
                if (foundRuleBuilder.getConnectionType().equals(connectionBuilder.getConnectionType())) {
                    //Purposefully left off generic
                    Supplier connectionSupplier = connectionBuilder.create(
                            connectionConfigs.get(connectionName).options
                    );
                    //noinspection unchecked
                    finalRuleList.add(foundRuleBuilder.create(
                        ruleOptions,
                        connectionName,
                        connectionSupplier
                    ));
                } else {
                    //todo log invalid connection
                }
            }
        }

        List<Rule.Result> results = new ArrayList<>();
        for (Rule rule : finalRuleList) {
            results.addAll(rule.evaluate());
        }

        return results;
    }
}
