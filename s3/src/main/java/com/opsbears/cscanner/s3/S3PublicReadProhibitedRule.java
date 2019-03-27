package com.opsbears.cscanner.s3;

import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.model.Bucket;
import com.amazonaws.services.s3.model.GetBucketAclRequest;
import com.amazonaws.services.s3.model.Grant;
import com.amazonaws.services.s3.model.Permission;
import com.opsbears.cscanner.core.Rule;

import javax.annotation.ParametersAreNonnullByDefault;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;

@ParametersAreNonnullByDefault
public class S3PublicReadProhibitedRule implements Rule {
    private final AmazonS3 s3Client;
    private final String connectionName;
    private final List<Pattern> include;
    private final List<Pattern> exclude;

    /**
     * @param s3Client the AWS S3 client
     * @param include Regular expressions of buckets to include from this type.
     * @param exclude Regular expression of buckets to exclude from this type.
     */
    public S3PublicReadProhibitedRule(
        AmazonS3 s3Client,
        String connectionName,
        List<Pattern> include,
        List<Pattern> exclude
    ) {

        this.s3Client = s3Client;
        this.connectionName = connectionName;
        this.include = include;
        this.exclude = exclude;
    }

    @Override
    public List<Result> evaluate() {
        List<Bucket> buckets = s3Client.listBuckets();
        List<Result> results = new ArrayList<>();
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

            Compliancy compliancy = Compliancy.COMPLIANT;
            List<Grant> grants = s3Client
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
                    compliancy = Compliancy.NONCOMPLIANT;
                }
            }
            results.add(
                new Result(
                    connectionName,
                    "s3",
                    bucket.getName(),
                    compliancy
                )
            );
        }
        return results;
    }
}
