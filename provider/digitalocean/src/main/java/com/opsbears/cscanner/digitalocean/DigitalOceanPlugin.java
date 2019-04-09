package com.opsbears.cscanner.digitalocean;

import com.opsbears.cscanner.core.CloudProvider;
import com.opsbears.cscanner.core.Plugin;

import javax.annotation.ParametersAreNonnullByDefault;
import java.util.Arrays;
import java.util.List;

@ParametersAreNonnullByDefault
public class DigitalOceanPlugin implements Plugin {
    @Override
    public List<CloudProvider<?, ?>> getCloudProviders() {
        return Arrays.asList(
            new DigitalOceanCloudProvider()
        );
    }
}
