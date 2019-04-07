package com.opsbears.cscanner.exoscale;

import com.opsbears.cscanner.core.*;
import com.opsbears.cscanner.firewall.FirewallConnection;
import com.opsbears.cscanner.firewall.FirewallPlugin;
import com.opsbears.cscanner.firewall.FirewallPublicServiceProhibitedRule;
import org.testng.SkipException;
import org.testng.annotations.BeforeTest;
import org.testng.annotations.Test;

import javax.annotation.Nullable;
import javax.annotation.ParametersAreNonnullByDefault;
import java.util.*;
import java.util.stream.Collectors;

import static org.testng.Assert.assertEquals;

/**
 * This test suite tests the Exoscale firewall behavior. It requires a live Exoscale connection.
 */
@ParametersAreNonnullByDefault
public class ExoscaleFirewallTest {
    @Nullable
    private static String apiKey;
    @Nullable
    private static String apiSecret;
    private static String resourcePrefix;
    private static ExoscaleTestClient testClient;

    static {
        apiKey = System.getenv("EXOSCALE_KEY");
        apiSecret = System.getenv("EXOSCALE_SECRET");
        resourcePrefix = System.getenv("TEST_RESOURCE_PREFIX");
        if (resourcePrefix == null || resourcePrefix.equals("")) {
            resourcePrefix = "test-" + UUID.randomUUID().toString() + "-";
        }
        if (apiKey == null || apiSecret == null) {
            testClient = null;
        } else {
            testClient = new ExoscaleTestClient(apiKey, apiSecret);
        }
    }

    private ScannerCore createScannerCore(List<RuleConfiguration> rules) {
        return ScannerCoreFactory.create(apiKey, apiSecret, rules, Arrays.asList(
            new FirewallPlugin()
        ));
    }

    @BeforeTest
    public void beforeMethod() {
        if (testClient == null) {
            throw new SkipException("Exoscale credentials not supplied (EXOSCALE_KEY and EXOSCALE_SECRET env variables), skipping tests.");
        }
    }

    @Test
    public void testCompliantSecurityGroup() {
        //Setup
        String sgName = resourcePrefix + "compliant";
        testClient.ensureSecurityGroupExists(sgName);
        try {
            testClient.ensureRuleExists(sgName, "icmpv6", Arrays.asList("::/0"), null, null, 128, 0);
            List<RuleConfiguration> rules = new ArrayList<>();
            Map<String, Object> options = new HashMap<>();
            options.put("protocol", "tcp");
            options.put("ports", Arrays.asList(22));
            rules.add(new RuleConfiguration(
                FirewallPublicServiceProhibitedRule.RULE,
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
                .filter(result -> result.resourceName.equalsIgnoreCase(sgName))
                .filter(result -> result.resourceType.equalsIgnoreCase(FirewallConnection.RESOURCE_TYPE))
                .collect(Collectors.toList());

            assertEquals(1, filteredResults.size());
            assertEquals(RuleResult.Compliancy.COMPLIANT, filteredResults.get(0).compliancy);

        } finally {
            //Cleanup
            testClient.ensureSecurityGroupAbsent(sgName);
        }
    }

    @Test
    public void testNonCompliantSecurityGroup() {
        //Setup
        String sgName = resourcePrefix + "noncompliant";
        testClient.ensureSecurityGroupExists(sgName);
        try {
            testClient.ensureRuleExists(sgName, "tcp", Arrays.asList("0.0.0.0/0"), 22, 22, null, null);
            List<RuleConfiguration> rules = new ArrayList<>();
            Map<String, Object> options = new HashMap<>();
            options.put("protocol", "tcp");
            options.put("ports", Arrays.asList(22));
            rules.add(new RuleConfiguration(
                FirewallPublicServiceProhibitedRule.RULE,
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
                .filter(result -> result.resourceName.equalsIgnoreCase(sgName))
                .filter(result -> result.resourceType.equalsIgnoreCase(FirewallConnection.RESOURCE_TYPE))
                .collect(Collectors.toList());

            assertEquals(1, filteredResults.size());
            assertEquals(RuleResult.Compliancy.NONCOMPLIANT, filteredResults.get(0).compliancy);
        } finally {
            //Cleanup
            testClient.ensureSecurityGroupAbsent(sgName);
        }
    }

    @Test
    public void testProtocolAll() {
        //Setup
        String sgName = resourcePrefix + "protocol-all";
        testClient.ensureSecurityGroupExists(sgName);
        try {
            testClient.ensureRuleExists(sgName, "all", Arrays.asList("0.0.0.0/0"), null, null, null, null);
            List<RuleConfiguration> rules = new ArrayList<>();
            Map<String, Object> options = new HashMap<>();
            options.put("protocol", "tcp");
            options.put("ports", Arrays.asList(22));
            rules.add(new RuleConfiguration(
                FirewallPublicServiceProhibitedRule.RULE,
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
                .filter(result -> result.resourceName.equalsIgnoreCase(sgName))
                .filter(result -> result.resourceType.equalsIgnoreCase(FirewallConnection.RESOURCE_TYPE))
                .collect(Collectors.toList());

            assertEquals(1, filteredResults.size());
            assertEquals(RuleResult.Compliancy.NONCOMPLIANT, filteredResults.get(0).compliancy);
        } finally {
            //Cleanup
            testClient.ensureSecurityGroupAbsent(sgName);
        }
    }
}
