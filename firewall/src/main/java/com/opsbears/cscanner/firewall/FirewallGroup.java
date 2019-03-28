package com.opsbears.cscanner.firewall;

import javax.annotation.ParametersAreNonnullByDefault;
import java.util.List;

@ParametersAreNonnullByDefault
public class FirewallGroup {
    public final String name;
    public final List<FirewallRule> firewallRules;

    public FirewallGroup(
        String name,
        List<FirewallRule> firewallRules
    ) {
        this.name = name;
        this.firewallRules = firewallRules;
    }
}
