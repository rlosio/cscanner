package com.opsbears.cscanner.exoscale;

import javax.annotation.Nullable;
import javax.annotation.ParametersAreNonnullByDefault;

public class ExoscaleConfiguration {
    @Nullable
    public final String key;
    @Nullable
    public final String secret;
    @Nullable
    public final String cloudStackConfig;
    @Nullable
    public final String profile;

    /**
     * @param key The Exoscale API key. Leave empty for default Exoscale behavior.
     * @param secret The Exoscale API secret. Leave empty for default Exoscale behavior.
     * @param cloudStackConfig The CloudStack configuration file. Leave empty for default behavior.
     * @param profile The CloudStack configuration file section to use. Leave empty for default behavior.
     */
    public ExoscaleConfiguration(
        @Nullable String key,
        @Nullable String secret,
        @Nullable String cloudStackConfig,
        @Nullable String profile
    ) {
        this.key = key;
        this.secret = secret;
        this.cloudStackConfig = cloudStackConfig;
        this.profile = profile;
    }
}
