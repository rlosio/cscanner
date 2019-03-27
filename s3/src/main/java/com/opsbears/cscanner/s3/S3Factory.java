package com.opsbears.cscanner.s3;

import com.amazonaws.regions.Region;
import com.amazonaws.regions.RegionImpl;
import com.amazonaws.services.s3.AmazonS3;

import javax.annotation.Nullable;
import javax.annotation.ParametersAreNonnullByDefault;
import java.util.function.Supplier;

@ParametersAreNonnullByDefault
public interface S3Factory {
    AmazonS3 get(@Nullable String region);
}
