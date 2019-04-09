package com.opsbears.cscanner.digitalocean;

import com.myjeeva.digitalocean.DigitalOcean;
import com.myjeeva.digitalocean.exception.DigitalOceanException;
import com.myjeeva.digitalocean.exception.RequestUnsuccessfulException;
import com.myjeeva.digitalocean.impl.DigitalOceanClient;
import com.myjeeva.digitalocean.pojo.*;
import com.opsbears.cscanner.firewall.FirewallClient;
import com.opsbears.cscanner.firewall.FirewallGroup;
import com.opsbears.cscanner.firewall.FirewallRule;
import com.opsbears.cscanner.firewall.Protocols;

import javax.annotation.ParametersAreNonnullByDefault;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@ParametersAreNonnullByDefault
public class DigitalOceanFirewallClient implements FirewallClient {
    private final Protocols protocols = Protocols.getInstance();
    private final List<FirewallGroup> firewallGroupsCache = new ArrayList<>();
    private final String apiToken;

    public DigitalOceanFirewallClient(
        DigitalOceanConfiguration configuration
    ) {
        this.apiToken = configuration.apiToken;
    }

    @Override
    public List<FirewallGroup> listFirewallGroups() {
        if (!firewallGroupsCache.isEmpty()) {
            return firewallGroupsCache;
        }

        List<FirewallGroup> firewallGroups = new ArrayList<>();

        DigitalOcean apiClient = new DigitalOceanClient(apiToken);

        Pages pages;
        int page = 0;
        int perPage = 100;
        do {
            try {
                Firewalls firewalls = apiClient.getAvailableFirewalls(page, perPage);
                firewallGroups.addAll(firewalls.getFirewalls().stream().map(this::convert).collect(Collectors.toList()));
                if (firewalls.getLinks() != null && firewalls.getLinks().getPages() != null && firewalls.getLinks().getPages().getNext() != null) {
                    pages = firewalls.getLinks().getPages();
                    page++;
                } else {
                    pages = null;
                }
            } catch (DigitalOceanException | RequestUnsuccessfulException e) {
                throw new RuntimeException(e);
            }
        } while (pages != null);

        firewallGroupsCache.addAll(firewallGroups);
        return firewallGroups;
    }

    private FirewallGroup convert(Firewall firewall) {
        List<FirewallRule> firewallRules = new ArrayList<>();

        int ruleId = 0;
        for (InboundRules inboundRule : firewall.getInboundRules()) {
            if (inboundRule.getSources().getAddresses() != null) {
                for (String cidr : inboundRule.getSources().getAddresses()) {
                    String ports = inboundRule.getPorts();
                    Integer startPort = null;
                    Integer endPort = null;
                    String[] portParts = ports.split("-");
                    if (portParts.length > 1) {
                        startPort = Integer.parseInt(portParts[0]);
                        endPort = Integer.parseInt(portParts[1]);
                    } else {
                        try {
                            startPort = endPort = Integer.parseInt(ports);
                        } catch (NumberFormatException ignore) {

                        }
                    }
                    firewallRules.add(new FirewallRule(
                        String.valueOf(ruleId++),
                        Protocols.getInstance().getProtocolIdByName(inboundRule.getProtocol()),
                        cidr,
                        null,
                        startPort,
                        endPort,
                        null,
                        null,
                        FirewallRule.Direction.INGRESS,
                        FirewallRule.Rule.ALLOW
                    ));
                }
            }
            //todo add support for load balancers/etc.
        }
        for (OutboundRules outboundRule : firewall.getOutboundRules()) {
            if (outboundRule.getDestinations().getAddresses() != null) {
                for (String cidr : outboundRule.getDestinations().getAddresses()) {
                    String ports = outboundRule.getPorts();
                    Integer startPort = null;
                    Integer endPort = null;
                    String[] portParts = ports.split("-");
                    if (portParts.length > 1) {
                        startPort = Integer.parseInt(portParts[0]);
                        endPort = Integer.parseInt(portParts[1]);
                    } else {
                        try {
                            startPort = endPort = Integer.parseInt(ports);
                        } catch (NumberFormatException ignore) {

                        }
                    }
                    firewallRules.add(new FirewallRule(
                        String.valueOf(ruleId++),
                        Protocols.getInstance().getProtocolIdByName(outboundRule.getProtocol()),
                        cidr,
                        null,
                        startPort,
                        endPort,
                        null,
                        null,
                        FirewallRule.Direction.INGRESS,
                        FirewallRule.Rule.ALLOW
                    ));
                }
            }
            //todo add support for load balancers/etc.
        }

        return new FirewallGroup(firewall.getName(), null, firewallRules);
    }
}
