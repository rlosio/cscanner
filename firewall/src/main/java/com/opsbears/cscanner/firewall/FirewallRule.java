package com.opsbears.cscanner.firewall;

import javax.annotation.Nullable;
import javax.annotation.ParametersAreNonnullByDefault;

@ParametersAreNonnullByDefault
public class FirewallRule {

    @Nullable
    public final Integer protocolNumber;
    @Nullable
    public final String cidr;
    @Nullable
    public final String otherFirewallGroupReference;
    @Nullable
    public final Integer fromPort;
    @Nullable
    public final Integer toPort;
    @Nullable
    public final Integer icmpType;
    @Nullable
    public final Integer icmpCode;

    public final Direction direction;

    public final Rule rule;

    public FirewallRule(
        @Nullable Integer protocolNumber,
        @Nullable String cidr,
        @Nullable String otherFirewallGroupReference,
        @Nullable Integer fromPort,
        @Nullable Integer toPort,
        @Nullable Integer icmpType,
        @Nullable Integer icmpCode,
        Direction direction,
        Rule rule
    ) {
        this.protocolNumber = protocolNumber;
        this.cidr = cidr;
        this.otherFirewallGroupReference = otherFirewallGroupReference;
        this.fromPort = fromPort;
        this.toPort = toPort;
        this.icmpType = icmpType;
        this.icmpCode = icmpCode;
        this.direction = direction;
        this.rule = rule;
    }

    public enum Direction {
        INGRESS,
        EGRESS
    }

    public enum Rule {
        ALLOW,
        DENY
    }
}
