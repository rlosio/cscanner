package com.opsbears.cscanner.aws;

import com.amazonaws.auth.*;
import com.amazonaws.auth.profile.ProfileCredentialsProvider;
import com.amazonaws.services.securitytoken.AWSSecurityTokenService;
import com.amazonaws.services.securitytoken.AWSSecurityTokenServiceClientBuilder;
import com.amazonaws.services.securitytoken.model.GetCallerIdentityRequest;
import com.amazonaws.services.securitytoken.model.GetCallerIdentityResult;
import com.opsbears.cscanner.core.CScannerParameter;
import com.opsbears.cscanner.core.NullSupplier;

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

    public AWSConfiguration(
        @Nullable
        @CScannerParameter(
            value = "accessKeyId",
            description = "The AWS access key ID to use. Leave empty for default AWS behavior.",
            defaultSupplier = NullSupplier.class
        )
        String accessKeyId,
        @Nullable
        @CScannerParameter(
            value = "secretAccessKey",
            description = "The AWS secret access key to use. Leave empty for default AWS behavior.",
            defaultSupplier = NullSupplier.class
        )
        String secretAccessKey,
        @Nullable
        @CScannerParameter(
            value = "sessionToken",
            description = "The AWS session token to use. Leave empty for default AWS behavior.",
            defaultSupplier = NullSupplier.class
        )
        String sessionToken,
        @Nullable
        @CScannerParameter(
            value = "profile",
            description = "The profile to use from the AWS config file. Leave empty for default AWS behavior.",
            defaultSupplier = NullSupplier.class
        )
        String profile
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
