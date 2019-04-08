package com.opsbears.cscanner.test;

import com.opsbears.cscanner.core.ConfigLoader;
import com.opsbears.cscanner.core.Plugin;

import javax.annotation.ParametersAreNonnullByDefault;
import java.util.List;

@ParametersAreNonnullByDefault
public class TestPlugin implements Plugin {
    private final List<ConfigLoader> configLoaders;

    public TestPlugin(List<ConfigLoader> configLoaders) {
        this.configLoaders = configLoaders;
    }

    @Override
    public List<ConfigLoader> getConfigLoaders() {
        return configLoaders;
    }
}
