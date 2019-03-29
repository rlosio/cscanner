package com.opsbears.cscanner.s3;

import com.amazonaws.regions.Region;
import com.amazonaws.regions.RegionImpl;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.model.Bucket;
import com.amazonaws.services.s3.model.GetBucketAclRequest;
import com.amazonaws.services.s3.model.Grant;
import com.amazonaws.services.s3.model.Permission;
import com.opsbears.cscanner.core.CloudProvider;
import com.opsbears.cscanner.core.Rule;
import com.opsbears.cscanner.core.RuleResult;

import javax.annotation.ParametersAreNonnullByDefault;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.regex.Pattern;

@ParametersAreNonnullByDefault
public class S3PublicReadProhibitedRule implements S3Rule {
    private final List<Pattern> include;
    private final List<Pattern> exclude;

    /**
     * @param include Regular expressions of buckets to include from this type.
     * @param exclude Regular expression of buckets to exclude from this type.
     */
    public S3PublicReadProhibitedRule(
        List<Pattern> include,
        List<Pattern> exclude
    ) {
        this.include = include;
        this.exclude = exclude;
    }

    @Override
    public List<RuleResult> evaluate(S3Connection s3Connection) {
        S3Factory s3ClientFactory = s3Connection.getS3Factory();
        AmazonS3 s3Client = s3ClientFactory.get(null);
        List<Bucket> buckets = s3Client.listBuckets();
        List<RuleResult> results = new ArrayList<>();
        for (Bucket bucket : buckets) {
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
                    compliancy = RuleResult.Compliancy.NONCOMPLIANT;
                }
            }
            results.add(
                new RuleResult(
                    s3Connection.getConnectionName(),
                    "s3",
                    bucket.getName(),
                    compliancy
                )
            );
        }
        return results;
    }
}
