package com.opsbears.cscanner.firewall;

import com.opsbears.cscanner.core.RuleConfiguration;
import com.opsbears.cscanner.core.RuleResult;
import com.opsbears.cscanner.core.ScannerCore;
import com.opsbears.cscanner.core.ScannerCoreFactory;
import com.opsbears.cscanner.exoscale.ExoscaleTestFirewallClientFactory;
import com.opsbears.cscanner.s3.S3TestClientSupplier;
import org.testng.Assert;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

import javax.annotation.ParametersAreNonnullByDefault;
import java.util.*;
import java.util.stream.Collectors;

/**
 * This test suite tests the Exoscale firewall behavior. It requires a live Exoscale connection.
 */
@ParametersAreNonnullByDefault
public class FirewallTest {
    @SuppressWarnings("unchecked")
    private static List<Class<TestFirewallClientFactory>> factories = Arrays.<Class<S3TestClientSupplier>>asList(
        new Class[]{ExoscaleTestFirewallClientFactory.class}
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
                factory -> factory.get() != null
            )
            .map(
                factory -> new Object[]{
                    resourcePrefix,
                    factory.get(),
                    factory.getScannerCore()
                }
            ).collect(Collectors.toList());

        return params.toArray(new Object[][]{});
    }


    @Test(dataProvider = "dataProvider")
    public void testCompliantSecurityGroup(
        String resourcePrefix,
        TestFirewallClient testClient,
        ScannerCoreFactory scannerCoreFactory
    ) {
        //Setup
        String sgName = resourcePrefix + "compliant";
        testClient.ensureSecurityGroupExists(sgName);
        try {
            testClient.ensureRuleExists(sgName, 58, Arrays.asList("::/0"), null, null, 128, 0);
            List<RuleConfiguration> rules = new ArrayList<>();
            Map<String, Object> options = new HashMap<>();
            options.put("protocol", "tcp");
            options.put("ports", Arrays.asList(22));
            rules.add(new RuleConfiguration(
                FirewallPublicServiceProhibitedRule.RULE,
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
                .filter(result -> result.resourceName.equalsIgnoreCase(sgName))
                .filter(result -> result.resourceType.equalsIgnoreCase(FirewallConnection.RESOURCE_TYPE))
                .collect(Collectors.toList());

            Assert.assertEquals(1, filteredResults.size());
            Assert.assertEquals(RuleResult.Compliancy.COMPLIANT, filteredResults.get(0).compliancy);

        } finally {
            //Cleanup
            testClient.ensureSecurityGroupAbsent(sgName);
        }
    }

    @Test(dataProvider = "dataProvider")
    public void testNonCompliantSecurityGroup(
        String resourcePrefix,
        TestFirewallClient testClient,
        ScannerCoreFactory scannerCoreFactory
    ) {
        //Setup
        String sgName = resourcePrefix + "noncompliant";
        testClient.ensureSecurityGroupExists(sgName);
        try {
            testClient.ensureRuleExists(sgName, 6, Arrays.asList("0.0.0.0/0"), 22, 22, null, null);
            List<RuleConfiguration> rules = new ArrayList<>();
            Map<String, Object> options = new HashMap<>();
            options.put("protocol", "tcp");
            options.put("ports", Arrays.asList(22));
            rules.add(new RuleConfiguration(
                FirewallPublicServiceProhibitedRule.RULE,
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
                .filter(result -> result.resourceName.equalsIgnoreCase(sgName))
                .filter(result -> result.resourceType.equalsIgnoreCase(FirewallConnection.RESOURCE_TYPE))
                .collect(Collectors.toList());

            Assert.assertEquals(1, filteredResults.size());
            Assert.assertEquals(RuleResult.Compliancy.NONCOMPLIANT, filteredResults.get(0).compliancy);
        } finally {
            //Cleanup
            testClient.ensureSecurityGroupAbsent(sgName);
        }
    }

    @Test(dataProvider = "dataProvider")
    public void testProtocolAll(
        String resourcePrefix,
        TestFirewallClient testClient,
        ScannerCoreFactory scannerCoreFactory
    ) {
        //Setup
        String sgName = resourcePrefix + "protocol-all";
        testClient.ensureSecurityGroupExists(sgName);
        try {
            testClient.ensureRuleExists(sgName, null, Arrays.asList("0.0.0.0/0"), null, null, null, null);
            List<RuleConfiguration> rules = new ArrayList<>();
            Map<String, Object> options = new HashMap<>();
            options.put("protocol", "tcp");
            options.put("ports", Arrays.asList(22));
            rules.add(new RuleConfiguration(
                FirewallPublicServiceProhibitedRule.RULE,
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
                .filter(result -> result.resourceName.equalsIgnoreCase(sgName))
                .filter(result -> result.resourceType.equalsIgnoreCase(FirewallConnection.RESOURCE_TYPE))
                .collect(Collectors.toList());

            Assert.assertEquals(1, filteredResults.size());
            Assert.assertEquals(RuleResult.Compliancy.NONCOMPLIANT, filteredResults.get(0).compliancy);
        } finally {
            //Cleanup
            testClient.ensureSecurityGroupAbsent(sgName);
        }
    }
}
