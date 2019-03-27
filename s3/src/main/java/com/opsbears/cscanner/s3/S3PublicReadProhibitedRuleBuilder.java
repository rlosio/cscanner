package com.opsbears.cscanner.s3;

import com.amazonaws.services.s3.AmazonS3;
import com.opsbears.cscanner.core.ConnectionBuilder;
import com.opsbears.cscanner.core.RuleBuilder;

import javax.annotation.ParametersAreNonnullByDefault;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.function.Supplier;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

@ParametersAreNonnullByDefault
public class S3PublicReadProhibitedRuleBuilder implements RuleBuilder<S3PublicReadProhibitedRule, AmazonS3> {
    @Override
    public String getType() {
        return "S3_PUBLIC_READ_PROHIBITED";
    }

    @Override
    public Class<AmazonS3> getConnectionType() {
        return AmazonS3.class;
    }

    @Override
    public S3PublicReadProhibitedRule create(
        Map<String, Object> options,
        String connectionName,
        Supplier<AmazonS3> clientFactory
    ) {
        List<Pattern> include = new ArrayList<>();
        if (options.containsKey("include")) {
            if (!(options.get("include") instanceof List)) {
                throw new RuntimeException("The include option needs to be a list, " + options.get("include").getClass().getSimpleName() + " found.");
            }
            include = ((List<String>)options.get("include"))
                .stream()
                .map(Pattern::compile)
                .collect(Collectors.toList());
        }

        List<Pattern> exclude = new ArrayList<>();
        if (options.containsKey("exclude")) {
            if (!(options.get("exclude") instanceof List)) {
                throw new RuntimeException("The exclude option needs to be a list, " + options.get("exclude").getClass().getSimpleName() + " found.");
            }
            exclude = ((List<String>)options.get("exclude"))
                .stream()
                .map(Pattern::compile)
                .collect(Collectors.toList());
        }

        return new S3PublicReadProhibitedRule(
            clientFactory.get(),
            connectionName,
            include,
            exclude
        );
    }
}
