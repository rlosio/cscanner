package com.opsbears.cscanner.s3;

import com.opsbears.cscanner.core.ScannerCoreFactory;

import javax.annotation.ParametersAreNonnullByDefault;

@ParametersAreNonnullByDefault
public interface S3TestClientSupplier extends S3Factory {
    boolean isConfigured();
    String getDefaultZone();
    ScannerCoreFactory getScannerCore();
}
