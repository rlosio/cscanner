package com.opsbears.cscanner.s3;

import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.model.*;
import com.opsbears.cscanner.core.CScannerParameter;
import com.opsbears.cscanner.core.EmptyListSupplier;
import com.opsbears.cscanner.core.FalseSupplier;
import com.opsbears.cscanner.core.RuleResult;

import javax.annotation.Nullable;
import javax.annotation.ParametersAreNonnullByDefault;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;

@ParametersAreNonnullByDefault
public class S3PublicReadProhibitedRule implements S3Rule {
    public final static String RULE = "S3_PUBLIC_READ_PROHIBITED";
    private final boolean scanContents;
    private final List<Pattern> include;
    private final List<Pattern> exclude;

    public S3PublicReadProhibitedRule(
        Options options
    ) {
        this.scanContents = options.scanContents;
        this.include = options.include;
        this.exclude = options.exclude;
    }

    private boolean checkGrantList(List<Grant> grants) {
        for (Grant grant : grants) {
            if (
                (
                    grant.getPermission().equals(Permission.Read) ||
                        grant.getPermission().equals(Permission.FullControl)
                ) &&
                    grant
                        .getGrantee()
                        .getIdentifier()
                        .equalsIgnoreCase("http://acs.amazonaws.com/groups/global/AllUsers")
            ) {
                return false;
            }
        }
        return true;
    }

    @Override
    public List<RuleResult> evaluate(S3Connection s3Connection) {
        S3Factory s3ClientFactory = s3Connection.getS3Factory();
        AmazonS3 s3Client = s3ClientFactory.get(null);
        List<Bucket> buckets = s3Client.listBuckets();
        List<RuleResult> results = new ArrayList<>();
        for (Bucket bucket : buckets) {
            List<RuleResult.Violation> violations = new ArrayList<>();
            if (!include.isEmpty()) {
                boolean includeMatches = false;
                for (Pattern includePattern : include) {
                    if (includePattern.matcher(bucket.getName()).matches()) {
                        includeMatches = true;
                        break;
                    }
                }
                if (!includeMatches) {
                    continue;
                }
            }
            if (!exclude.isEmpty()) {
                boolean excludeMatches = false;
                for (Pattern excludePattern : exclude) {
                    if (excludePattern.matcher(bucket.getName()).matches()) {
                        excludeMatches = true;
                        break;
                    }
                }
                if (excludeMatches) {
                    continue;
                }
            }

            RuleResult.Compliancy compliancy = RuleResult.Compliancy.COMPLIANT;

            String region = s3Client.getBucketLocation(bucket.getName());
            AmazonS3 secondaryS3Client = s3ClientFactory.get(region);

            List<Grant> grants = secondaryS3Client
                .getBucketAcl(
                    new GetBucketAclRequest(
                        bucket.getName()
                    )
                )
                .getGrantsAsList();
            if (!checkGrantList(grants)) {
                compliancy = RuleResult.Compliancy.NONCOMPLIANT;
                violations.add(new RuleResult.Violation(null, "Bucket has a public-read ACL"));
            }
            if (compliancy == RuleResult.Compliancy.COMPLIANT && scanContents) {
                //Scan files
                ListObjectsV2Request req = new ListObjectsV2Request().withBucketName(bucket.getName());
                ListObjectsV2Result result;
                do {
                    result = secondaryS3Client.listObjectsV2(req);

                    for (S3ObjectSummary objectSummary : result.getObjectSummaries()) {
                        AccessControlList acls = secondaryS3Client.getObjectAcl(
                            bucket.getName(),
                            objectSummary.getKey()
                        );
                        if (!checkGrantList(acls.getGrantsAsList())) {
                            compliancy = RuleResult.Compliancy.NONCOMPLIANT;
                            violations.add(new RuleResult.Violation(
                                objectSummary.getKey(),
                                "Object has a public-read ACL"
                            ));
                        }
                    }
                    // If there are more than maxKeys keys in the bucket, get a continuation token
                    // and list the next objects.
                    String token = result.getNextContinuationToken();
                    req.setContinuationToken(token);
                } while (result.isTruncated() && compliancy == RuleResult.Compliancy.COMPLIANT);
            }

            results.add(
                new RuleResult(
                    s3Connection.getConnectionName(),
                    S3Rule.RESOURCE_TYPE,
                    region,
                    bucket.getName(),
                    compliancy,
                    violations
                )
            );
        }
        return results;
    }

    public static class Options {
        @Nullable
        public final boolean scanContents;
        @Nullable
        public final List<Pattern> include;
        @Nullable
        public final List<Pattern> exclude;

        public Options(
            @CScannerParameter(
                value = "scanContents",
                description = "Can the contents of S3 buckets for ACL violations. Keep in mind that this can take a long time.",
                defaultSupplier = FalseSupplier.class
            )
                boolean scanContents,
            @CScannerParameter(
                value = "include",
                description = "A list of bucket regexps to include.",
                defaultSupplier = EmptyListSupplier.class
            )
                List<Pattern> include,
            @CScannerParameter(
                value = "exclude",
                description = "A list of bucket regexps to exclude. Exclude takes precedence over include.",
                defaultSupplier = EmptyListSupplier.class
            )
                List<Pattern> exclude
        ) {
            this.scanContents = scanContents;
            this.include = include;
            this.exclude = exclude;
        }
    }
}
