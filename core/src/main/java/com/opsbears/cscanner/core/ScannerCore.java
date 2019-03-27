package com.opsbears.cscanner.core;

import javax.annotation.ParametersAreNonnullByDefault;
import java.util.*;

@ParametersAreNonnullByDefault
public class ScannerCore {
    private final List<Plugin> plugins;

    public ScannerCore(List<Plugin> plugins) {
        this.plugins = plugins;
    }

    public List<RuleResult> scan() {
        //region Plugins
        List<ConfigLoader> configLoaders = new ArrayList<>();
        List<CloudProvider<?, ?>> cloudProviders = new ArrayList<>();
        List<RuleBuilder<?, ?>> ruleBuilders = new ArrayList<>();
        for (Plugin plugin : plugins) {
            configLoaders.addAll(plugin.getConfigLoaders());
            cloudProviders.addAll(plugin.getCloudProviders());
            ruleBuilders.addAll(plugin.getSupportedRules());
        }
        //endregion

        //region Connection configuration
        Map<String, ConnectionConfiguration> connectionConfigurations = new HashMap<>();
        List<RuleConfiguration> ruleConfigurations = new ArrayList<>();
        for (ConfigLoader configLoader : configLoaders) {
            connectionConfigurations.putAll(configLoader.loadConnectionConfigurations());
            ruleConfigurations.addAll(configLoader.loadRuleConfigurations());
        }
        //endregion

        //region Cloud provider mapping
        Map<String,CloudProvider<?, ?>> cloudProviderByConnectionKey = new HashMap<>();
        Map<String, CloudProviderConnection> cloudProviderConnectionMap = new HashMap<>();
        for (String connectionKey : connectionConfigurations.keySet()) {
            ConnectionConfiguration connectionConfiguration = connectionConfigurations.get(connectionKey);

            Optional<CloudProvider<?, ?>> cloudProvider = cloudProviders.stream().filter(
                cp -> cp.getName().equalsIgnoreCase(connectionConfiguration.type)
            ).findFirst();

            if (!cloudProvider.isPresent()) {
                throw new RuntimeException("Cloud provider '" + connectionConfiguration.type + "' is not supported.");
            }
            cloudProviderByConnectionKey.put(connectionKey, cloudProvider.get());
            cloudProviderConnectionMap.put(
                connectionKey,
                cloudProvider.get().getConnection(connectionKey, connectionConfigurations.get(connectionKey).options)
            );
        }
        //endregion

        //region Rules
        List<RuleResult> result = new ArrayList<>();
        for (RuleConfiguration ruleConfiguration : ruleConfigurations) {
            String ruleType = ruleConfiguration.type;
            List<String> ruleConnections = ruleConfiguration.connections;
            Map<String, Object> ruleOptions = ruleConfiguration.options;

            Optional<RuleBuilder<?, ?>> ruleBuilder = ruleBuilders
                .stream()
                .filter(rb -> rb.getType().equalsIgnoreCase(ruleType))
                .findFirst();

            if (!ruleBuilder.isPresent()) {
                throw new RuntimeException("Rule type '" + ruleType + "' not supported.");
            }

            if (ruleConnections.isEmpty()) {
                ruleConnections.addAll(cloudProviderByConnectionKey.keySet());
            }
            Rule rule = ruleBuilder.get().create(ruleOptions);

            for (String ruleConnection : ruleConnections) {
                if (!cloudProviderByConnectionKey.containsKey(ruleConnection)) {
                    throw new RuntimeException("No such connection: " + ruleConnection);
                }

                CloudProvider<?, ?> connection = cloudProviderByConnectionKey.get(ruleConnection);
                Class<?> connectionType = ruleBuilder.get().getConnectionType();
                if (!connectionType.isAssignableFrom(connection.getConnectionType())) {
                    //This connection does not support this type.
                    continue;
                }
                //noinspection unchecked
                result.addAll(rule.evaluate(cloudProviderConnectionMap.get(ruleConnection)));
            }
        }
        //endregion

        return result;
    }
}
