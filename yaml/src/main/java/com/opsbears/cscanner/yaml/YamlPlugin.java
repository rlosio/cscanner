package com.opsbears.cscanner.yaml;

import com.opsbears.cscanner.core.ConfigLoader;
import com.opsbears.cscanner.core.Plugin;

import javax.annotation.ParametersAreNonnullByDefault;
import java.util.Arrays;
import java.util.List;

@ParametersAreNonnullByDefault
public class YamlPlugin implements Plugin {
    private final String filename;

    public YamlPlugin(String filename) {
        this.filename = filename;
    }

    @Override
    public List<ConfigLoader> getConfigLoaders() {
        return Arrays.asList(
            new YamlConfigLoader(filename)
        );
    }
}
