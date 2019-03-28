package com.opsbears.cscanner.firewall;

import javax.annotation.ParametersAreNonnullByDefault;
import java.util.List;

@ParametersAreNonnullByDefault
public interface FirewallClient {
    List<FirewallGroup> listFirewallGroups();
}
