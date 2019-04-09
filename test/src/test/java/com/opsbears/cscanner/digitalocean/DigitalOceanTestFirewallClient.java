package com.opsbears.cscanner.digitalocean;

import com.myjeeva.digitalocean.DigitalOcean;
import com.myjeeva.digitalocean.exception.DigitalOceanException;
import com.myjeeva.digitalocean.exception.RequestUnsuccessfulException;
import com.myjeeva.digitalocean.impl.DigitalOceanClient;
import com.myjeeva.digitalocean.pojo.*;
import com.opsbears.cscanner.firewall.Protocols;
import com.opsbears.cscanner.firewall.TestFirewallClient;

import javax.annotation.Nullable;
import javax.annotation.ParametersAreNonnullByDefault;
import java.util.ArrayList;
import java.util.List;

@ParametersAreNonnullByDefault
public class DigitalOceanTestFirewallClient implements TestFirewallClient {
    private final DigitalOcean apiClient;

    public DigitalOceanTestFirewallClient(
        String apiToken
    ) {
        apiClient = new DigitalOceanClient(apiToken);
    }

    @Override
    public void ensureSecurityGroupExists(String name) {
        try {
            Firewall firewall = new Firewall();
            firewall.setName(name);

            List<InboundRules> inboundRules = new ArrayList<>();
            InboundRules inboundRule = new InboundRules();
            inboundRule.setProtocol("icmp");
            inboundRule.setPorts("1-65535");
            inboundRule.setSources(new Sources());
            inboundRules.add(inboundRule);
            firewall.setInboundRules(inboundRules);

            List<OutboundRules> outboundRules = new ArrayList<>();
            OutboundRules outboundRule = new OutboundRules();
            outboundRule.setProtocol("icmp");
            outboundRule.setPorts("1-65535");
            outboundRule.setDestinations(new Destinations());
            outboundRules.add(outboundRule);
            firewall.setOutboundRules(outboundRules);

            apiClient.createFirewall(firewall);
        } catch (DigitalOceanException | RequestUnsuccessfulException e) {
            throw new RuntimeException(e);
        }
    }

    private String findFirewall(String name) {
        Pages pages;
        int page = 0;
        int perPage = 100;
        List<Firewall> firewallList = new ArrayList<>();
        do {
            try {
                Firewalls firewalls = apiClient.getAvailableFirewalls(page, perPage);
                firewallList.addAll(firewalls.getFirewalls());
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

        return firewallList.stream().filter(firewall -> firewall.getName().equalsIgnoreCase(name)).findFirst().get().getId();
    }

    @Override
    public void ensureSecurityGroupAbsent(String name) {
        try {
            apiClient.deleteFirewall(findFirewall(name));
        } catch (DigitalOceanException | RequestUnsuccessfulException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void ensureRuleExists(
        String securityGroupName,
        @Nullable
        Integer protocol,
        List<String> cidrList,
        @Nullable Integer startPort,
        @Nullable Integer endPort,
        @Nullable Integer icmpType,
        @Nullable Integer icmpCode
    ) {
        try {
            Firewall firewall = apiClient.getFirewallInfo(findFirewall(securityGroupName));

            List<String> protocols = new ArrayList<>();
            if (protocol != null) {
                String protocolName;
                if (protocol == 58) {
                    protocolName = "icmp";
                } else {
                    protocolName = Protocols.getInstance().getProtocolNameByNumber(protocol);
                }
                protocols.add(protocolName);
            } else {
                protocols.add("tcp");
                protocols.add("udp");
                protocols.add("icmp");
            }

            List<InboundRules> inboundRules = firewall.getInboundRules();
            for (String protocolName : protocols) {
                InboundRules inboundRule = new InboundRules();
                if (startPort != null && endPort != null) {
                    inboundRule.setPorts(startPort + "-" + endPort);
                } else {
                    inboundRule.setPorts("1-65535");
                }
                inboundRule.setProtocol(protocolName);
                Sources sources = new Sources();
                sources.setAddresses(cidrList);
                inboundRule.setSources(sources);

                inboundRules.add(inboundRule);
            }
            firewall.setInboundRules(inboundRules);

            apiClient.updateFirewall(firewall);
        } catch (RequestUnsuccessfulException | DigitalOceanException e) {
            throw new RuntimeException(e);
        }
    }
}
