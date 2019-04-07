package com.opsbears.cscanner.exoscale;

import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.model.CannedAccessControlList;
import com.amazonaws.services.s3.model.ObjectMetadata;
import com.amazonaws.services.s3.model.SetBucketAclRequest;
import com.opsbears.cscanner.core.RuleConfiguration;
import com.opsbears.cscanner.core.RuleResult;
import com.opsbears.cscanner.core.ScannerCore;
import com.opsbears.cscanner.s3.S3Plugin;
import com.opsbears.cscanner.s3.S3PublicReadProhibitedRule;
import com.opsbears.cscanner.s3.S3Rule;
import org.testng.SkipException;
import org.testng.annotations.BeforeTest;
import org.testng.annotations.Test;

import javax.annotation.Nullable;
import javax.annotation.ParametersAreNonnullByDefault;
import java.io.ByteArrayInputStream;
import java.util.*;
import java.util.stream.Collectors;

import static org.testng.Assert.assertEquals;

@ParametersAreNonnullByDefault
public class ExoscaleS3Test {
    @Nullable
    private static String apiKey;
    @Nullable
    private static String apiSecret;
    private static ExoscaleS3ClientSupplier testClientSupplier;

    static {
        apiKey = System.getenv("EXOSCALE_KEY");
        apiSecret = System.getenv("EXOSCALE_SECRET");
        if (apiKey == null || apiSecret == null) {
            testClientSupplier = null;
        } else {
            testClientSupplier = new ExoscaleS3ClientSupplier(new ExoscaleConfiguration(apiKey, apiSecret, null, null));
        }
    }

    @BeforeTest
    public void beforeMethod() {
        if (testClientSupplier == null) {
            throw new SkipException("Exoscale credentials not supplied (EXOSCALE_KEY and EXOSCALE_SECRET env variables), skipping tests.");
        }
    }

    private ScannerCore createScannerCore(List<RuleConfiguration> rules) {
        return ScannerCoreFactory.create(apiKey, apiSecret, rules, Arrays.asList(
            new S3Plugin()
        ));
    }

    @Test
    public void testCompliantBucket() {
        //Setup
        AmazonS3 client = testClientSupplier.get("at-vie-1");
        client.createBucket("compliant-bucket.cscanner.io");
        client.setBucketAcl(new SetBucketAclRequest(
            "compliant-bucket.cscanner.io",
            CannedAccessControlList.Private
        ));
        List<RuleConfiguration> rules = new ArrayList<>();
        Map<String, Object> options = new HashMap<>();
        rules.add(new RuleConfiguration(
            S3PublicReadProhibitedRule.RULE,
            new ArrayList<>(),
            options
        ));

        ScannerCore scannerCore = createScannerCore(
            rules
        );

        //Execute
        List<RuleResult> results = scannerCore.scan();

        //Assert
        List<RuleResult> filteredResults = results
            .stream()
            .filter(result -> result.connectionName.equals("exo"))
            .filter(result -> result.resourceName.equalsIgnoreCase("compliant-bucket.cscanner.io"))
            .filter(result -> result.resourceType.equalsIgnoreCase(S3Rule.RESOURCE_TYPE))
            .collect(Collectors.toList());

        assertEquals(1, filteredResults.size());
        assertEquals(RuleResult.Compliancy.COMPLIANT, filteredResults.get(0).compliancy);
    }

    private void assertNonCompliantFilePublicAcl(boolean scanContents) {
        //Setup
        String bucketName = "non-compliant-bucket.cscanner.io";
        AmazonS3 client = testClientSupplier.get("at-vie-1");
        client.createBucket(bucketName);
        client.putObject(bucketName, "/test.txt", new ByteArrayInputStream("Hello world!".getBytes()), new ObjectMetadata());
        client.setObjectAcl(bucketName, "/test.txt", CannedAccessControlList.PublicRead);
        List<RuleConfiguration> rules = new ArrayList<>();
        Map<String, Object> options = new HashMap<>();
        options.put("scanContents", scanContents);
        rules.add(new RuleConfiguration(
            S3PublicReadProhibitedRule.RULE,
            new ArrayList<>(),
            options
        ));

        ScannerCore scannerCore = createScannerCore(
            rules
        );

        //Execute
        List<RuleResult> results = scannerCore.scan();

        //Assert
        List<RuleResult> filteredResults = results
            .stream()
            .filter(result -> result.connectionName.equals("exo"))
            .filter(result -> result.resourceName.equalsIgnoreCase(bucketName))
            .filter(result -> result.resourceType.equalsIgnoreCase(S3Rule.RESOURCE_TYPE))
            .collect(Collectors.toList());

        assertEquals(1, filteredResults.size());
        assertEquals(scanContents?RuleResult.Compliancy.NONCOMPLIANT:RuleResult.Compliancy.COMPLIANT, filteredResults.get(0).compliancy);

    }

    @Test
    public void testNonCompliantFilePublicAclNoScanContents() {
        assertNonCompliantFilePublicAcl(false);
    }

    @Test
    public void testNonCompliantFilePublicAclScanContents() {
        assertNonCompliantFilePublicAcl(true);
    }
}
