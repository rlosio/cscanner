package com.opsbears.cscanner.exoscale;

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
public class ExoscaleS3TestClientFactory implements S3TestClientSupplier {
    @Nullable
    private static final String apiKey;
    @Nullable
    private static final String apiSecret;
    private static final ExoscaleConfiguration exoscaleConfiguration;

    static {
        apiKey = System.getenv("EXOSCALE_KEY");
        apiSecret = System.getenv("EXOSCALE_SECRET");
        if (apiKey != null && apiSecret != null) {
            exoscaleConfiguration =
                new ExoscaleConfiguration(
                    apiKey,
                    apiSecret,
                    null,
                    null
            );
        } else {
            exoscaleConfiguration = null;
        }
    }

    @Override
    public boolean isConfigured() {
        return apiKey != null && apiSecret != null;
    }

    @Override
    public String getDefaultZone() {
        return "at-vie-1";
    }

    @Override
    public AmazonS3 get(@Nullable String region) {
        return new ExoscaleS3ClientSupplier(exoscaleConfiguration).get(region);
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
            plugins.add(new S3Plugin());
            return new ScannerCore(plugins);
        };
    }
}
