package com.opsbears.cscanner.s3;

import com.opsbears.cscanner.core.RuleBuilder;

import javax.annotation.ParametersAreNonnullByDefault;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.regex.Pattern;

@ParametersAreNonnullByDefault
public class S3PublicReadProhibitedRuleBuilder implements RuleBuilder<S3PublicReadProhibitedRule, S3Connection, S3PublicReadProhibitedRule.Options> {
    @Override
    public String getType() {
        return S3PublicReadProhibitedRule.RULE;
    }

    @Override
    public Class<S3Connection> getConnectionType() {
        return S3Connection.class;
    }

    @Override
    public Class<S3PublicReadProhibitedRule.Options> getConfigurationType() {
        return S3PublicReadProhibitedRule.Options.class;
    }

    @Override
    public S3PublicReadProhibitedRule create(S3PublicReadProhibitedRule.Options options) {
        return new S3PublicReadProhibitedRule(
            options
        );
    }
}
