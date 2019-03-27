package com.opsbears.cscanner.core;

import javax.annotation.ParametersAreNonnullByDefault;
import java.util.List;
import java.util.Map;

@ParametersAreNonnullByDefault
public interface ConfigLoader {
    Map<String, ConnectionConfiguration> loadConnectionConfigurations();
    List<RuleConfiguration> loadRuleConfigurations();

}
