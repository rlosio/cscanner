package com.opsbears.cscanner.firewall;

import com.opsbears.cscanner.core.Rule;
import com.opsbears.cscanner.core.RuleResult;

import javax.annotation.ParametersAreNonnullByDefault;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.regex.Pattern;

@ParametersAreNonnullByDefault
public class FirewallPublicServiceProhibitedRule implements Rule<FirewallConnection> {
    public static final String RULE = "FIREWALL_PUBLIC_SERVICE_PROHIBITED";
    public final Integer protocol;
    public final List<Integer> ports;
    public final List<Pattern> include;
    public final List<Pattern> exclude;

    public FirewallPublicServiceProhibitedRule(
        Integer protocol,
        List<Integer> ports,
        List<Pattern> include,
        List<Pattern> exclude
    ) {
        this.protocol = protocol;
        this.ports = ports;
        this.include = include;
        this.exclude = exclude;
    }

    @Override
    public List<RuleResult> evaluate(FirewallConnection connection) {
        List<FirewallGroup> firewallGroups = connection.getFirewallClient().listFirewallGroups();

        List<RuleResult> results = new ArrayList<>();
        for (FirewallGroup firewallGroup : firewallGroups) {
            if (include.size() > 0) {
                boolean foundInclude = false;
                for (Pattern match : include) {
                    if (match.matcher(firewallGroup.name).matches()) {
                        foundInclude = true;
                        break;
                    }
                }
                if (!foundInclude) {
                    continue;
                }
            }

            if (exclude.size() > 0) {
                boolean foundExclude = false;
                for (Pattern match : exclude) {
                    if (match.matcher(firewallGroup.name).matches()) {
                        foundExclude = true;
                        break;
                    }
                }
                if (foundExclude) {
                    continue;
                }
            }

            boolean compliant = true;
            for (Integer port : ports) {
                for (FirewallRule firewallRule : firewallGroup.firewallRules) {
                    if (
                        ((firewallRule.fromPort != null
                        && firewallRule.toPort != null
                        && firewallRule.fromPort <= port
                        && firewallRule.toPort >= port
                        && (
                            firewallRule.protocolNumber == null
                            || Objects.equals(
                                firewallRule.protocolNumber,
                                protocol
                            )
                        )) || (
                            firewallRule.protocolNumber == null &&
                                firewallRule.fromPort == null &&
                                firewallRule.toPort == null
                            ))
                        && firewallRule.cidr != null
                        && firewallRule.direction == FirewallRule.Direction.INGRESS
                        && (
                            firewallRule.cidr.equalsIgnoreCase("0.0.0.0/0")
                            || firewallRule.cidr.equalsIgnoreCase("::/0")
                        )) {
                        if (firewallRule.rule == FirewallRule.Rule.ALLOW) {
                            compliant = false;
                        } else {
                            //Blocks rule, fall through.
                            break;
                        }
                    }
                }
            }
            results.add(
                new RuleResult(
                    connection.getConnectionName(),
                    FirewallConnection.RESOURCE_TYPE,
                    firewallGroup.name,
                    compliant? RuleResult.Compliancy.COMPLIANT: RuleResult.Compliancy.NONCOMPLIANT
                )
            );
        }
        return results;
    }
}
