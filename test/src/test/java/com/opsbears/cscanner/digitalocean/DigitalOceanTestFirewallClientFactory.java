package com.opsbears.cscanner.digitalocean;

import com.opsbears.cscanner.core.ConnectionConfiguration;
import com.opsbears.cscanner.core.Plugin;
import com.opsbears.cscanner.core.ScannerCore;
import com.opsbears.cscanner.core.ScannerCoreFactory;
import com.opsbears.cscanner.digitalocean.DigitalOceanPlugin;
import com.opsbears.cscanner.firewall.FirewallPlugin;
import com.opsbears.cscanner.firewall.TestFirewallClient;
import com.opsbears.cscanner.firewall.TestFirewallClientFactory;
import com.opsbears.cscanner.test.TestConfigurationLoader;
import com.opsbears.cscanner.test.TestPlugin;

import javax.annotation.Nullable;
import javax.annotation.ParametersAreNonnullByDefault;
import java.util.*;

@SuppressWarnings("unused")
@ParametersAreNonnullByDefault
public class DigitalOceanTestFirewallClientFactory implements TestFirewallClientFactory {
    @Nullable
    private static final String apiToken;

    static {
        apiToken = System.getenv("DIGITALOCEAN_TOKEN");
    }

    @Override
    public ScannerCoreFactory getScannerCore() {
        return rules -> {
            Map<String, ConnectionConfiguration> connections = new HashMap<>();
            Map<String, Object> options = new HashMap<>();
            options.put("apiToken", apiToken);
            connections.put("do", new ConnectionConfiguration(
                "digitalocean",
                options
            ));

            List<Plugin> plugins = new ArrayList<>(Arrays.asList(
                new TestPlugin(
                    Arrays.asList(
                        new TestConfigurationLoader(
                            connections,
                            rules
                        )
                    )
                ),
                new DigitalOceanPlugin()
            ));
            plugins.add(new FirewallPlugin());
            return new ScannerCore(plugins);
        };
    }

    @Override
    public TestFirewallClient get() {
        if (apiToken == null) {
            return null;
        }
        return new DigitalOceanTestFirewallClient(
            apiToken
        );
    }
}
