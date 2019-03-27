package com.opsbears.cscanner.s3;

import com.amazonaws.services.s3.AmazonS3;
import com.opsbears.cscanner.core.CloudProvider;
import com.opsbears.cscanner.core.CloudProviderConnection;

import javax.annotation.ParametersAreNonnullByDefault;
import java.util.function.Supplier;

@ParametersAreNonnullByDefault
public interface S3CloudProvider<CONFIGURATIONTYPE, CONNECTIONTYPE extends S3Connection> extends CloudProvider<CONFIGURATIONTYPE, CONNECTIONTYPE> {
}
