package com.opsbears.cscanner.aws;

import com.amazonaws.auth.*;
import com.amazonaws.auth.profile.ProfileCredentialsProvider;
import com.amazonaws.services.securitytoken.AWSSecurityTokenService;
import com.amazonaws.services.securitytoken.AWSSecurityTokenServiceClientBuilder;
import com.amazonaws.services.securitytoken.model.GetCallerIdentityRequest;
import com.amazonaws.services.securitytoken.model.GetCallerIdentityResult;

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
    
    public AWSCredentialsProvider getCredentialsProvider() {
        if (accessKeyId != null && secretAccessKey != null) {
            if (sessionToken != null) {
                return new AWSSessionCredentialsProvider() {
                    @Override
                    public AWSSessionCredentials getCredentials() {
                        return new AWSSessionCredentials() {
                            @Override
                            public String getSessionToken() {
                                return sessionToken;
                            }

                            @Override
                            public String getAWSAccessKeyId() {
                                return accessKeyId;
                            }

                            @Override
                            public String getAWSSecretKey() {
                                return secretAccessKey;
                            }
                        };
                    }

                    @Override
                    public void refresh() {

                    }
                };
            } else {
                return new AWSCredentialsProvider() {
                    @Override
                    public AWSCredentials getCredentials() {
                        return new AWSCredentials() {
                            @Override
                            public String getAWSAccessKeyId() {
                                return accessKeyId;
                            }

                            @Override
                            public String getAWSSecretKey() {
                                return secretAccessKey;
                            }
                        };
                    }

                    @Override
                    public void refresh() {

                    }
                };
            }
        } else if (profile != null) {
            return new ProfileCredentialsProvider(profile);
        } else {
            return new DefaultAWSCredentialsProviderChain();
        }
    }

    public String getAccountId() {
        AWSSecurityTokenServiceClientBuilder builder = AWSSecurityTokenServiceClientBuilder.standard();
        builder.withCredentials(getCredentialsProvider());
        AWSSecurityTokenService client = builder.build();

        GetCallerIdentityResult callerIdentity = client.getCallerIdentity(new GetCallerIdentityRequest());
        return callerIdentity.getAccount();
    }

    public void validateCredentials() {
        getAccountId();
    }
}
