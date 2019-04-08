package com.opsbears.cscanner.s3;

import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.model.CannedAccessControlList;
import com.amazonaws.services.s3.model.DeleteObjectRequest;
import com.amazonaws.services.s3.model.ObjectMetadata;
import com.amazonaws.services.s3.model.SetBucketAclRequest;
import com.opsbears.cscanner.core.RuleConfiguration;
import com.opsbears.cscanner.core.RuleResult;
import com.opsbears.cscanner.core.ScannerCore;
import com.opsbears.cscanner.core.ScannerCoreFactory;
import com.opsbears.cscanner.exoscale.ExoscaleS3TestClientFactory;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

import javax.annotation.ParametersAreNonnullByDefault;
import java.io.ByteArrayInputStream;
import java.util.*;
import java.util.stream.Collectors;

import static org.testng.Assert.assertEquals;

@ParametersAreNonnullByDefault
public class S3Test {
    @SuppressWarnings("unchecked")
    private static List<Class<S3TestClientSupplier>> factories = Arrays.<Class<S3TestClientSupplier>>asList(
        new Class[]{ExoscaleS3TestClientFactory.class}
    );

    @DataProvider(name = "dataProvider")
    public static Object[][] dataProvider() {
        String resourcePrefix;
        if (System.getenv("TEST_RESOURCE_PREFIX") == null || System.getenv("TEST_RESOURCE_PREFIX").equals("")) {
            resourcePrefix = "test-" + UUID.randomUUID().toString() + "-";
        } else {
            resourcePrefix = System.getenv("TEST_RESOURCE_PREFIX");
        }

        List<Object[]> params = factories.stream()
            .map(
                factory -> {
                    try {
                        return factory.newInstance();
                    } catch (InstantiationException | IllegalAccessException e) {
                        return null;
                    }
                }
            )
            .filter(Objects::nonNull)
            .filter(
                S3TestClientSupplier::isConfigured
            )
            .map(
                factory -> new Object[]{
                    resourcePrefix,
                    factory,
                    factory.getScannerCore()
                }
            ).collect(Collectors.toList());

        return params.toArray(new Object[][]{});
    }

    @Test(dataProvider = "dataProvider")
    public void testCompliantBucket(
        String resourcePrefix,
        S3TestClientSupplier testClientSupplier,
        ScannerCoreFactory scannerCoreFactory
    ) {
        //Setup
        String bucketName = resourcePrefix + "compliant-bucket";
        AmazonS3 client = testClientSupplier.get(testClientSupplier.getDefaultZone());
        client.createBucket(bucketName);
        try {
            client.setBucketAcl(new SetBucketAclRequest(
                bucketName,
                CannedAccessControlList.Private
            ));
            List<RuleConfiguration> rules = new ArrayList<>();
            Map<String, Object> options = new HashMap<>();
            rules.add(new RuleConfiguration(
                S3PublicReadProhibitedRule.RULE,
                new ArrayList<>(),
                options
            ));

            ScannerCore scannerCore = scannerCoreFactory.create(
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
            assertEquals(RuleResult.Compliancy.COMPLIANT, filteredResults.get(0).compliancy);
        } finally {
            //Cleanup
            client.deleteBucket(bucketName);
        }
    }

    private void assertNonCompliantFilePublicAcl(
        String resourcePrefix,
        S3TestClientSupplier testClientSupplier,
        ScannerCoreFactory scannerCoreFactory,
        CannedAccessControlList bucketAcl,
        CannedAccessControlList fileAcl,
        boolean scanContents,
        RuleResult.Compliancy expectedResult
    ) {
        //Setup
        String bucketName = resourcePrefix + "-bucket";
        AmazonS3 client = testClientSupplier.get(testClientSupplier.getDefaultZone());
        client.createBucket(bucketName);
        try {
            client.putObject(
                bucketName,
                "/test.txt",
                new ByteArrayInputStream("Hello world!".getBytes()),
                new ObjectMetadata()
            );
            client.setBucketAcl(bucketName, bucketAcl);
            client.setObjectAcl(bucketName, "/test.txt", fileAcl);
            List<RuleConfiguration> rules = new ArrayList<>();
            Map<String, Object> options = new HashMap<>();
            options.put("scanContents", scanContents);
            rules.add(new RuleConfiguration(
                S3PublicReadProhibitedRule.RULE,
                new ArrayList<>(),
                options
            ));

            ScannerCore scannerCore = scannerCoreFactory.create(
                rules
            );

            //Execute
            List<RuleResult> results = scannerCore.scan();

            //Assert
            List<RuleResult> filteredResults = results
                .stream()
                .filter(result -> result.resourceName.equalsIgnoreCase(bucketName))
                .filter(result -> result.resourceType.equalsIgnoreCase(S3Rule.RESOURCE_TYPE))
                .collect(Collectors.toList());

            assertEquals(1, filteredResults.size());
            assertEquals(
                expectedResult,
                filteredResults.get(0).compliancy
            );
        } finally {
            //Cleanup
            client.deleteObject(new DeleteObjectRequest(bucketName, "/test.txt"));
            client.deleteBucket(bucketName);
        }
    }

    @Test(dataProvider = "dataProvider")
    public void testNonCompliantBucketAcl(
        String resourcePrefix,
        S3TestClientSupplier testClientSupplier,
        ScannerCoreFactory scannerCoreFactory
    ) {
        assertNonCompliantFilePublicAcl(
            resourcePrefix,
            testClientSupplier,
            scannerCoreFactory,
            CannedAccessControlList.PublicRead,
            CannedAccessControlList.Private,
            false,
            RuleResult.Compliancy.NONCOMPLIANT
        );
    }


    @Test(dataProvider = "dataProvider")
    public void testNonCompliantFilePublicAclNoScanContents(
        String resourcePrefix,
        S3TestClientSupplier testClientSupplier,
        ScannerCoreFactory scannerCoreFactory
    ) {
        assertNonCompliantFilePublicAcl(
            resourcePrefix,
            testClientSupplier,
            scannerCoreFactory,
            CannedAccessControlList.Private,
            CannedAccessControlList.PublicRead,
            false,
            RuleResult.Compliancy.COMPLIANT
        );
    }

    @Test(dataProvider = "dataProvider")
    public void testNonCompliantFilePublicAclScanContents(
        String resourcePrefix,
        S3TestClientSupplier testClientSupplier,
        ScannerCoreFactory scannerCoreFactory
    ) {
        assertNonCompliantFilePublicAcl(
            resourcePrefix,
            testClientSupplier,
            scannerCoreFactory,
            CannedAccessControlList.Private,
            CannedAccessControlList.PublicRead,
            true,
            RuleResult.Compliancy.NONCOMPLIANT
        );
    }
}
