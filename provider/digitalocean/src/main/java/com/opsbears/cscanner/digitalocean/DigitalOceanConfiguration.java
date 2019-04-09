package com.opsbears.cscanner.digitalocean;

import com.opsbears.cscanner.core.CScannerParameter;
import com.opsbears.cscanner.core.NullSupplier;

import javax.annotation.Nullable;
import javax.annotation.ParametersAreNonnullByDefault;

@ParametersAreNonnullByDefault
public class DigitalOceanConfiguration {
    @Nullable
    public final String apiToken;
    @Nullable
    public final String spacesKey;
    @Nullable
    public final String spacesSecret;

    public DigitalOceanConfiguration(
        @CScannerParameter(
            value = "apiToken",
            defaultSupplier = NullSupplier.class
        )
        @Nullable
        String apiToken,
        @CScannerParameter(
            value = "spacesKey",
            defaultSupplier = NullSupplier.class
        )
        @Nullable String spacesKey,
        @CScannerParameter(
            value = "spacesSecret",
            defaultSupplier = NullSupplier.class
        )
        @Nullable String spacesSecret
    ) {
        this.apiToken = apiToken;
        this.spacesKey = spacesKey;
        this.spacesSecret = spacesSecret;
    }
}
