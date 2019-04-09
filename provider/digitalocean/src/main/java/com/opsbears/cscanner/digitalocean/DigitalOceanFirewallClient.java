package com.opsbears.cscanner.digitalocean;

import com.myjeeva.digitalocean.DigitalOcean;
import com.myjeeva.digitalocean.exception.DigitalOceanException;
import com.myjeeva.digitalocean.exception.RequestUnsuccessfulException;
import com.myjeeva.digitalocean.impl.DigitalOceanClient;
import com.myjeeva.digitalocean.pojo.Firewalls;
import com.myjeeva.digitalocean.pojo.Pages;
import com.opsbears.cscanner.firewall.FirewallClient;
import com.opsbears.cscanner.firewall.FirewallGroup;
import com.opsbears.cscanner.firewall.Protocols;

import javax.annotation.ParametersAreNonnullByDefault;
import java.util.ArrayList;
import java.util.List;

@ParametersAreNonnullByDefault
public class DigitalOceanFirewallClient implements FirewallClient {
    private final Protocols protocols = Protocols.getInstance();
    private final List<FirewallGroup> firewallGroupsCache = new ArrayList<>();
    private final String apiToken;

    public DigitalOceanFirewallClient(
        String apiToken
    ) {
        this.apiToken = apiToken;
    }

    @Override
    public List<FirewallGroup> listFirewallGroups() {
        if (!firewallGroupsCache.isEmpty()) {
            return firewallGroupsCache;
        }

        List<FirewallGroup> firewallGroups = new ArrayList<>();

        DigitalOcean apiClient = new DigitalOceanClient(apiToken);
        try {
            Firewalls firewalls = apiClient.getAvailableFirewalls(0, 100);
            Pages totalPages = firewalls.getLinks().getPages();
        } catch (DigitalOceanException | RequestUnsuccessfulException e) {
            throw new RuntimeException(e);
        }

        firewallGroupsCache.addAll(firewallGroups);
        return firewallGroups;
    }
}
