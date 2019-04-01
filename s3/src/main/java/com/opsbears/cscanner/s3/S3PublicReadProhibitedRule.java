package com.opsbears.cscanner.s3;

import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.model.*;
import com.opsbears.cscanner.core.RuleResult;

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

    /**
     * @param scanContents
     * @param include Regular expressions of buckets to include from this type.
     * @param exclude Regular expression of buckets to exclude from this type.
     */
    public S3PublicReadProhibitedRule(
        boolean scanContents,
        List<Pattern> include,
        List<Pattern> exclude
    ) {
        this.scanContents = scanContents;
        this.include = include;
        this.exclude = exclude;
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
            }
            if (compliancy == RuleResult.Compliancy.COMPLIANT && scanContents) {
                //Scan files
                ListObjectsV2Request req = new ListObjectsV2Request().withBucketName(bucket.getName());
                ListObjectsV2Result result;
                do {
                    result = secondaryS3Client.listObjectsV2(req);

                    for (S3ObjectSummary objectSummary : result.getObjectSummaries()) {
                        AccessControlList acls = secondaryS3Client.getObjectAcl(bucket.getName(), objectSummary.getKey());
                        if (!checkGrantList(acls.getGrantsAsList())) {
                            compliancy = RuleResult.Compliancy.NONCOMPLIANT;
                            break;
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
                    bucket.getName(),
                    compliancy
                )
            );
        }
        return results;
    }
}
