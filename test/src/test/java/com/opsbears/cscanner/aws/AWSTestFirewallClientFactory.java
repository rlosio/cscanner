package com.opsbears.cscanner.aws;

import com.opsbears.cscanner.core.ConnectionConfiguration;
import com.opsbears.cscanner.core.Plugin;
import com.opsbears.cscanner.core.ScannerCore;
import com.opsbears.cscanner.core.ScannerCoreFactory;
import com.opsbears.cscanner.exoscale.ExoscaleTestFirewallClient;
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
public class AWSTestFirewallClientFactory implements TestFirewallClientFactory {
    @Nullable
    private static final String apiKey;
    @Nullable
    private static final String apiSecret;
    @Nullable
    private static final AWSConfiguration awsConfiguration;

    static {
        apiKey = System.getenv("AWS_ACCESS_KEY_ID");
        apiSecret = System.getenv("AWS_SECRET_ACCESS_KEY");
        if (apiKey != null && apiSecret != null) {
            awsConfiguration =
                new AWSConfiguration(
                    apiKey,
                    apiSecret,
                    null,
                    null
                );
        } else {
            awsConfiguration = null;
        }
    }

    @Override
    public ScannerCoreFactory getScannerCore() {
        return rules -> {
            Map<String, ConnectionConfiguration> connections = new HashMap<>();
            Map<String, Object> options = new HashMap<>();
            options.put("accessKeyId", apiKey);
            options.put("secretAccessKey", apiSecret);
            connections.put("aws", new ConnectionConfiguration(
                "aws",
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
                new AWSPlugin()
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
        return new AWSTestFirewallClient(
            apiKey,
            apiSecret
        );
    }
}
