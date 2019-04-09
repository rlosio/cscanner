package com.opsbears.cscanner.core;

import javax.annotation.ParametersAreNonnullByDefault;
import java.util.Map;

@ParametersAreNonnullByDefault
class RuleFactory {
    private final ConfigurationConverter configurationConverter = new ConfigurationConverter();

    <RULETYPE extends Rule, CONNECTION extends CloudProviderConnection, CONFIGURATIONTYPE> RULETYPE create(
        RuleBuilder<RULETYPE, CONNECTION, CONFIGURATIONTYPE> ruleBuilder,
        Map<String, Object> ruleOptions
    ) {
        return ruleBuilder.create(
            configurationConverter.convert(ruleOptions, ruleBuilder.getConfigurationType())
        );
    }
}

