package com.opsbears.cscanner.firewall;

import javax.annotation.Nullable;
import javax.annotation.ParametersAreNonnullByDefault;
import java.util.List;

@ParametersAreNonnullByDefault
public class FirewallGroup {
    public final String name;
    @Nullable
    public final String region;
    public final List<FirewallRule> firewallRules;

    public FirewallGroup(
        String name,
        @Nullable
        String region,
        List<FirewallRule> firewallRules
    ) {
        this.name = name;
        this.region = region;
        this.firewallRules = firewallRules;
    }
}
