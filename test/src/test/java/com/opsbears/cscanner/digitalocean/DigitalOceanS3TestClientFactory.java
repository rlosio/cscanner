package com.opsbears.cscanner.digitalocean;

import com.amazonaws.services.s3.AmazonS3;
import com.opsbears.cscanner.core.ConnectionConfiguration;
import com.opsbears.cscanner.core.Plugin;
import com.opsbears.cscanner.core.ScannerCore;
import com.opsbears.cscanner.core.ScannerCoreFactory;
import com.opsbears.cscanner.s3.S3Plugin;
import com.opsbears.cscanner.s3.S3TestClientSupplier;
import com.opsbears.cscanner.test.TestConfigurationLoader;
import com.opsbears.cscanner.test.TestPlugin;

import javax.annotation.Nullable;
import javax.annotation.ParametersAreNonnullByDefault;
import java.util.*;

@ParametersAreNonnullByDefault
public class DigitalOceanS3TestClientFactory implements S3TestClientSupplier {
    @Nullable
    private static final String apiKey;
    @Nullable
    private static final String apiSecret;
    private static final DigitalOceanConfiguration digitaloceanConfiguration;

    static {
        apiKey = System.getenv("DIGITALOCEAN_SPACES_KEY");
        apiSecret = System.getenv("DIGITALOCEAN_SPACES_SECRET");
        if (apiKey != null && apiSecret != null) {
            digitaloceanConfiguration =
                new DigitalOceanConfiguration(
                    null,
                    apiKey,
                    apiSecret
            );
        } else {
            digitaloceanConfiguration = null;
        }
    }

    @Override
    public boolean isConfigured() {
        return apiKey != null && apiSecret != null;
    }

    @Override
    public String getDefaultZone() {
        return "ams3";
    }

    @Override
    public AmazonS3 get(@Nullable String region) {
        return new DigitalOceanS3ClientSupplier(digitaloceanConfiguration).get(region);
    }

    @Override
    public ScannerCoreFactory getScannerCore() {
        return rules -> {
            Map<String, ConnectionConfiguration> connections = new HashMap<>();
            Map<String, Object> options = new HashMap<>();
            options.put("spacesKey", apiKey);
            options.put("spacesSecret", apiSecret);
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
            plugins.add(new S3Plugin());
            return new ScannerCore(plugins);
        };
    }
}
