package com.opsbears.cscanner.firewall;

import javax.annotation.Nullable;
import javax.annotation.ParametersAreNonnullByDefault;
import java.util.List;

@ParametersAreNonnullByDefault
public interface TestFirewallClient {
    void ensureSecurityGroupExists(String name);
    void ensureSecurityGroupAbsent(String name);
    void ensureRuleExists(
        String securityGroupName,
        @Nullable
        Integer protocol,
        List<String> cidrList,
        @Nullable Integer startPort,
        @Nullable Integer endPort,
        @Nullable Integer icmpType,
        @Nullable Integer icmpCode
    );
}
