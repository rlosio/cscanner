package com.opsbears.cscanner.s3;

import com.opsbears.cscanner.core.RuleBuilder;

import javax.annotation.ParametersAreNonnullByDefault;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.regex.Pattern;

@ParametersAreNonnullByDefault
public class S3PublicReadProhibitedRuleBuilder implements RuleBuilder<S3PublicReadProhibitedRule, S3Connection> {
    @Override
    public String getType() {
        return S3PublicReadProhibitedRule.RULE;
    }

    @Override
    public Class<S3Connection> getConnectionType() {
        return S3Connection.class;
    }

    @Override
    public S3PublicReadProhibitedRule create(Map<String, Object> options) {
        List<Pattern> includePatterns = new ArrayList<>();
        if (options.containsKey("include")) {
            Object include = options.get("include");
            if (!(include instanceof List)) {
                throw new RuntimeException("The option 'include' should be a list, " + include.getClass().getSimpleName() + " given.");
            }
            List includeList = (List) include;

            for (Object includeObject : includeList) {
                if (!(includeObject instanceof String)) {
                    throw new RuntimeException("The include rule should be a string, " + includeObject.getClass().getSimpleName() + " given.");
                }
                includePatterns.add(Pattern.compile((String)includeObject));
            }
        }

        List<Pattern> excludePatterns = new ArrayList<>();
        if (options.containsKey("exclude")) {
            Object exclude = options.get("exclude");
            if (!(exclude instanceof List)) {
                throw new RuntimeException("The option 'exclude' should be a list, " + exclude.getClass().getSimpleName() + " given.");
            }
            List excludeList = (List) exclude;

            for (Object excludeObject : excludeList) {
                if (!(excludeObject instanceof String)) {
                    throw new RuntimeException("The exclude rule should be a string, " + excludeObject.getClass().getSimpleName() + " given.");
                }
                excludePatterns.add(Pattern.compile((String)excludeObject));
            }
        }

        boolean scanContents = false;
        if (options.containsKey("scanContents") && (boolean) options.get("scanContents")) {
            scanContents = true;
        }

        return new S3PublicReadProhibitedRule(
            scanContents,
            includePatterns,
            excludePatterns
        );
    }
}
