package com.opsbears.cscanner.exoscale;

import com.opsbears.cscanner.core.ConnectionConfiguration;
import com.opsbears.cscanner.core.Plugin;
import com.opsbears.cscanner.core.RuleConfiguration;
import com.opsbears.cscanner.core.ScannerCore;
import com.opsbears.cscanner.firewall.FirewallPlugin;
import com.opsbears.cscanner.s3.S3Plugin;
import com.opsbears.cscanner.test.TestConfigurationLoader;
import com.opsbears.cscanner.test.TestPlugin;

import javax.annotation.ParametersAreNonnullByDefault;
import java.util.*;

@ParametersAreNonnullByDefault
public class ScannerCoreFactory {
    public static ScannerCore create(String apiKey, String apiSecret, List<RuleConfiguration> rules, List<Plugin> pluginsUnderTest) {
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
        plugins.addAll(pluginsUnderTest);
        return new ScannerCore(plugins);
    }
}
