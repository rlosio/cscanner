package com.opsbears.cscanner.s3;

import com.opsbears.cscanner.core.ConnectionBuilder;
import com.opsbears.cscanner.core.Plugin;
import com.opsbears.cscanner.core.RuleBuilder;

import javax.annotation.ParametersAreNonnullByDefault;
import java.util.Arrays;
import java.util.List;

@ParametersAreNonnullByDefault
public class S3Plugin implements Plugin {
    @Override
    public List<ConnectionBuilder<?>> getSupportedConnections() {
        return Arrays.asList(
            new S3ConnectionBuilder()
        );
    }

    @Override
    public List<RuleBuilder<?, ?>> getSupportedRules() {
        //noinspection unchecked
        return Arrays.asList(
            new S3PublicReadProhibitedRuleBuilder()
        );
    }
}
