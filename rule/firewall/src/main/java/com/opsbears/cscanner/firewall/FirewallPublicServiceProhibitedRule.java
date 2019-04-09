package com.opsbears.cscanner.firewall;

import com.opsbears.cscanner.core.*;

import javax.annotation.Nullable;
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
        Options options
    ) {
        this.protocol = options.protocol;
        this.ports = options.ports;
        this.include = options.include;
        this.exclude = options.exclude;
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
            List<RuleResult.Violation> violations = new ArrayList<>();
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
                            String protocolDescription = firewallRule.protocolNumber == null?"all":Protocols.getInstance().getProtocolNameByNumber(firewallRule.protocolNumber);
                            String fromPortDescription = firewallRule.fromPort == null?"0": String.valueOf(firewallRule.fromPort);
                            String toPortDescription = firewallRule.toPort == null?"0": String.valueOf(firewallRule.toPort);
                            violations.add(new RuleResult.Violation(firewallRule.id, "Ingress rule provides public access to port " + port + " (" + protocolDescription + "/" + fromPortDescription + "-" + toPortDescription + ")"));
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
                    firewallGroup.region,
                    firewallGroup.name,
                    compliant? RuleResult.Compliancy.COMPLIANT: RuleResult.Compliancy.NONCOMPLIANT,
                    violations
                )
            );
        }
        return results;
    }

    public static class Options {
        public final Integer protocol;
        public final List<Integer> ports;
        public final List<Pattern> include;
        public final List<Pattern> exclude;

        public Options(
            @CScannerParameter(
                value = "protocol",
                defaultSupplier = NullSupplier.class,
                description = "Protocol number"
            )
            @Nullable
            Integer protocol,
            @CScannerParameter(
                value = "ports",
                defaultSupplier = EmptyListSupplier.class,
                description = "Ports to scan for public accessibility"
            )
            List<Integer> ports,
            @CScannerParameter(
                value = "include",
                defaultSupplier = EmptyListSupplier.class,
                description = "Regular expression on firewall group names to include in this rule."
            )
            List<Pattern> include,
            @CScannerParameter(
                value = "exclude",
                defaultSupplier = EmptyListSupplier.class,
                description = "Regular expression on firewall group names to exclude from this rule. Exclude takes precedence over include"
            )
            List<Pattern> exclude
        ) {
            this.protocol = protocol;
            this.ports = ports;
            this.include = include;
            this.exclude = exclude;
        }

        public Options(
            @CScannerParameter(
                value = "protocol",
                defaultSupplier = NullSupplier.class,
                description = "Protocol name"
            )
                @Nullable
                String protocol,
            @CScannerParameter(
                value = "ports",
                defaultSupplier = EmptyListSupplier.class,
                description = "Ports to scan for public accessibility"
            )
                List<Integer> ports,
            @CScannerParameter(
                value = "include",
                defaultSupplier = EmptyListSupplier.class,
                description = "Regular expression on firewall group names to include in this rule."
            )
                List<Pattern> include,
            @CScannerParameter(
                value = "exclude",
                defaultSupplier = EmptyListSupplier.class,
                description = "Regular expression on firewall group names to exclude from this rule. Exclude takes precedence over include"
            )
                List<Pattern> exclude
        ) {
            if (protocol != null) {
                this.protocol = Protocols.getInstance().getProtocolIdByName(protocol);
            } else {
                this.protocol = null;
            }
            this.ports = ports;
            this.include = include;
            this.exclude = exclude;
        }
    }
}
