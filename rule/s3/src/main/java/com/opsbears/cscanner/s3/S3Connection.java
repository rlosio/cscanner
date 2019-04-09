package com.opsbears.cscanner.s3;

import com.amazonaws.services.s3.AmazonS3;
import com.opsbears.cscanner.core.CloudProviderConnection;

import javax.annotation.ParametersAreNonnullByDefault;
import java.util.function.Supplier;

@ParametersAreNonnullByDefault
public interface S3Connection extends CloudProviderConnection {
    S3Factory getS3Factory();
}
