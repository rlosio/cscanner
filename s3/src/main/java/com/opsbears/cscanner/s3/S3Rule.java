package com.opsbears.cscanner.s3;

import com.opsbears.cscanner.core.CloudProvider;
import com.opsbears.cscanner.core.Rule;

import javax.annotation.ParametersAreNonnullByDefault;

@ParametersAreNonnullByDefault
public interface S3Rule extends Rule<S3Connection> {

}
