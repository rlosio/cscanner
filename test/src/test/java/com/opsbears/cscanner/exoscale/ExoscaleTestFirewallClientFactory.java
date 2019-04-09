package com.opsbears.cscanner.exoscale;

import com.opsbears.cscanner.core.ConnectionConfiguration;
import com.opsbears.cscanner.core.Plugin;
import com.opsbears.cscanner.core.ScannerCore;
import com.opsbears.cscanner.core.ScannerCoreFactory;
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
public class ExoscaleTestFirewallClientFactory implements TestFirewallClientFactory {
    @Nullable
    private static final String apiKey;
    @Nullable
    private static final String apiSecret;

    static {
        apiKey = System.getenv("EXOSCALE_KEY");
        apiSecret = System.getenv("EXOSCALE_SECRET");
    }

    @Override
    public ScannerCoreFactory getScannerCore() {
        return rules -> {
            Map<String, ConnectionConfiguration> connections = new HashMap<>();
            Map<String, Object> options = new HashMap<>();
            options.put("key", apiKey);
            options.put("secret", apiSecret);
            connections.put("exo", new ConnectionConfiguration(
                "exoscale",
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
                new ExoscalePlugin()
            ));
            plugins.add(new FirewallPlugin());
            return new ScannerCore(plugins);
        };
    }

    @Override
    public TestFirewallClient get() {
        if (apiKey == null || apiSecret == null) {
            return null;
        }
        return new ExoscaleTestFirewallClient(
            apiKey,
            apiSecret
        );
    }
}
