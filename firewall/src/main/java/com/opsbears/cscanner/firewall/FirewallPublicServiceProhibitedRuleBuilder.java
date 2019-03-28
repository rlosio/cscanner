package com.opsbears.cscanner.firewall;

import com.opsbears.cscanner.core.RuleBuilder;
import jnr.netdb.Protocol;

import javax.annotation.ParametersAreNonnullByDefault;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.regex.Pattern;

@ParametersAreNonnullByDefault
public class FirewallPublicServiceProhibitedRuleBuilder implements RuleBuilder<FirewallPublicServiceProhibitedRule, FirewallConnection> {
    @Override
    public String getType() {
        return "FIREWALL_PUBLIC_SERVICE_PROHIBITED";
    }

    @Override
    public Class<FirewallConnection> getConnectionType() {
        return FirewallConnection.class;
    }

    @Override
    public FirewallPublicServiceProhibitedRule create(Map<String, Object> options) {
        if (!options.containsKey("protocol")) {
            throw new RuntimeException("Parameter 'protocol' required for FIREWALL_PUBLIC_SERVICE_PROHIBITED");
        }
        Object protocol = options.get("protocol");
        if (protocol instanceof String) {
            protocol = Protocols.getInstance().getProtocolIdByName((String) protocol);
        } else if (!(protocol instanceof Integer)) {
            throw new RuntimeException("Invalid protocol definition type '" + protocol.getClass().getSimpleName() + "'");
        }
        int protocolNumber = (int) protocol;

        if (!options.containsKey("ports")) {
            throw new RuntimeException("Parameter 'ports' required for FIREWALL_PUBLIC_SERVICE_PROHIBITED");
        }
        Object ports = options.get("ports");
        if (!(ports instanceof List)) {
            throw new RuntimeException("Invalid port definition type '" + protocol.getClass().getSimpleName() + "'");
        }
        List portList = (List) ports;
        List<Integer> realPortList = new ArrayList<>();
        for (Object port : portList) {
            if (!(port instanceof Integer)) {
                throw new RuntimeException("Invalid port type: '" + port.getClass().getSimpleName() + "'");
            }
            Integer portInt = (Integer) port;
            if (portInt < 1 || portInt > 65535) {
                throw new RuntimeException("Invalid port number: " + portInt);
            }
            realPortList.add(portInt);
        }

        List<Pattern> includePatterns = new ArrayList<>();
        if (options.containsKey("include")) {
            Object include = options.get("include");
            if (!(include instanceof List)) {
                throw new RuntimeException("The option 'include' should be a list, " + include.getClass().getSimpleName() + " given.");
            }
            List includeList = (List) include;

            for (Object includeObject : includeList) {
                if (!(includeObject instanceof String)) {
                    throw new RuntimeException("The include rule should be a string, " + includeObject.getClass().getSimpleName() + " given.");
                }
                includePatterns.add(Pattern.compile((String)includeObject));
            }
        }

        List<Pattern> excludePatterns = new ArrayList<>();
        if (options.containsKey("exclude")) {
            Object exclude = options.get("exclude");
            if (!(exclude instanceof List)) {
                throw new RuntimeException("The option 'exclude' should be a list, " + exclude.getClass().getSimpleName() + " given.");
            }
            List excludeList = (List) exclude;

            for (Object excludeObject : excludeList) {
                if (!(excludeObject instanceof String)) {
                    throw new RuntimeException("The exclude rule should be a string, " + excludeObject.getClass().getSimpleName() + " given.");
                }
                excludePatterns.add(Pattern.compile((String)excludeObject));
            }
        }

        return new FirewallPublicServiceProhibitedRule(
            protocolNumber,
            realPortList,
            includePatterns,
            excludePatterns
        );
    }
}
