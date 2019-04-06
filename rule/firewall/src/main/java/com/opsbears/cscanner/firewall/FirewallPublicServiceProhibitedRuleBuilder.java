package com.opsbears.cscanner.firewall;

import com.opsbears.cscanner.core.RuleBuilder;

import javax.annotation.ParametersAreNonnullByDefault;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.regex.Pattern;

@ParametersAreNonnullByDefault
public class FirewallPublicServiceProhibitedRuleBuilder implements RuleBuilder<FirewallPublicServiceProhibitedRule, FirewallConnection, FirewallPublicServiceProhibitedRule.Options> {
    @Override
    public String getType() {
        return FirewallPublicServiceProhibitedRule.RULE;
    }

    @Override
    public Class<FirewallConnection> getConnectionType() {
        return FirewallConnection.class;
    }

    @Override
    public Class<FirewallPublicServiceProhibitedRule.Options> getConfigurationType() {
        return FirewallPublicServiceProhibitedRule.Options.class;
    }

    @Override
    public FirewallPublicServiceProhibitedRule create(FirewallPublicServiceProhibitedRule.Options options) {
        return new FirewallPublicServiceProhibitedRule(
            options
        );
    }
}
