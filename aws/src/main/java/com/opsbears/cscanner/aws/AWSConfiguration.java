package com.opsbears.cscanner.aws;

import javax.annotation.Nullable;
import javax.annotation.ParametersAreNonnullByDefault;

public class AWSConfiguration {
    @Nullable
    public final String accessKeyId;
    @Nullable
    public final String secretAccessKey;
    @Nullable
    public final String sessionToken;
    @Nullable
    public final String profile;

    /**
     * @param accessKeyId The AWS access key ID to use. Leave empty for default AWS behavior.
     * @param secretAccessKey The AWS secret access key to use. Leave empty for default AWS behavior.
     * @param sessionToken The AWS session token to use. Leave empty for default AWS behavior.
     * @param profile The profile to use from the AWS config file. Leave empty for default AWS behavior.
     */
    public AWSConfiguration(
        @Nullable String accessKeyId,
        @Nullable String secretAccessKey,
        @Nullable String sessionToken,
        @Nullable String profile
    ) {
        this.accessKeyId = accessKeyId;
        this.secretAccessKey = secretAccessKey;
        this.sessionToken = sessionToken;
        this.profile = profile;
    }
}
