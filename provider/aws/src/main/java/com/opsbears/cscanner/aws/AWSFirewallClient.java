package com.opsbears.cscanner.aws;

import com.amazonaws.AmazonClientException;
import com.amazonaws.regions.Regions;
import com.amazonaws.services.ec2.AmazonEC2;
import com.amazonaws.services.ec2.AmazonEC2ClientBuilder;
import com.amazonaws.services.ec2.model.*;
import com.opsbears.cscanner.firewall.FirewallClient;
import com.opsbears.cscanner.firewall.FirewallGroup;
import com.opsbears.cscanner.firewall.FirewallRule;
import com.opsbears.cscanner.firewall.Protocols;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.annotation.ParametersAreNonnullByDefault;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@ParametersAreNonnullByDefault
public class AWSFirewallClient implements FirewallClient {
    private final static Logger logger = LoggerFactory.getLogger(AWSFirewallClient.class);
    private final AWSConfiguration awsConfiguration;
    private final Protocols protocols = Protocols.getInstance();
    private final List<FirewallGroup> firewallGroupCache = new ArrayList<>();

    public AWSFirewallClient(AWSConfiguration awsConfiguration) {
        this.awsConfiguration = awsConfiguration;
    }

    private String getArn(Regions region, String accountId, String securityGroupId) {
        return "arn:aws:ec2:" + region.getName() + ":" + accountId + ":security-group/" + securityGroupId;
    }

    private List<FirewallRule> extractFirewallRules(Regions region, String accountId, SecurityGroup securityGroup, List<IpPermission> permissions, FirewallRule.Direction direction) {
        logger.debug("Converting firewall rules in region " + region + " for security group " + securityGroup.getGroupName() + " " + direction + "...");
        List<FirewallRule> firewallRules = new ArrayList<>();
        for (IpPermission permission : permissions) {
            for (IpRange ipvRange : permission.getIpv4Ranges()) {
                firewallRules.add(new FirewallRule(
                    null,
                    permission.getIpProtocol().equals("-1") ?null:protocols.getProtocolIdByName(permission.getIpProtocol()),
                    ipvRange.getCidrIp(),
                    null,
                    permission.getFromPort(),
                    permission.getToPort(),
                    null,
                    null,
                    direction,
                    FirewallRule.Rule.ALLOW
                ));
            }
            for (Ipv6Range ipvRange : permission.getIpv6Ranges()) {
                firewallRules.add(new FirewallRule(
                    null,
                    permission.getIpProtocol().equals("-1") ?null:protocols.getProtocolIdByName(permission.getIpProtocol()),
                    ipvRange.getCidrIpv6(),
                    null,
                    permission.getFromPort(),
                    permission.getToPort(),
                    null,
                    null,
                    direction,
                    FirewallRule.Rule.ALLOW
                ));
            }
            for (UserIdGroupPair userIdGroupPair : permission.getUserIdGroupPairs()) {
                firewallRules.add(new FirewallRule(
                    null,
                    permission.getIpProtocol().equals("-1") ?null:protocols.getProtocolIdByName(permission.getIpProtocol()),
                    null,
                    getArn(region, userIdGroupPair.getUserId(), userIdGroupPair.getGroupId()),
                    permission.getFromPort(),
                    permission.getToPort(),
                    null,
                    null,
                    direction,
                    FirewallRule.Rule.ALLOW
                ));
            }
        }
        firewallRules.add(new FirewallRule(
            null,
            null,
            null,
            null,
            null,
            null,
            null,
            null,
            direction,
            FirewallRule.Rule.DENY
        ));
        logger.debug("Conversion complete, " + firewallRules.size() + " rules returned.");
        return firewallRules;
    }

    private FirewallGroup convert(Regions region, String accountId, SecurityGroup securityGroup) {
        List<FirewallRule> firewallRules = new ArrayList<>();

        firewallRules.addAll(extractFirewallRules(
            region,
            accountId,
            securityGroup,
            securityGroup.getIpPermissions(),
            FirewallRule.Direction.INGRESS
        ));
        firewallRules.addAll(extractFirewallRules(
            region,
            accountId,
            securityGroup,
            securityGroup.getIpPermissionsEgress(),
            FirewallRule.Direction.EGRESS
        ));

        return new FirewallGroup(
            securityGroup.getGroupName(),
            region.toString(),
            firewallRules
        );
    }

    @Override
    public List<FirewallGroup> listFirewallGroups() {
        String accountId = awsConfiguration.getAccountId();
        List<FirewallGroup> firewallGroups = new ArrayList<>();
        if (!firewallGroupCache.isEmpty()) {
            return firewallGroupCache;
        }
        for (Regions region : Regions.values()) {
            try {
                logger.debug("Fetching firewall groups for region " + region.getName() + "...");
                AmazonEC2ClientBuilder builder = AmazonEC2ClientBuilder.standard();
                builder.withCredentials(awsConfiguration.getCredentialsProvider());
                builder.withRegion(region);
                AmazonEC2 client = builder.build();

                String nextToken = null;
                do {
                    DescribeSecurityGroupsResult describeSecurityGroups = client.describeSecurityGroups(
                        new DescribeSecurityGroupsRequest()
                            .withNextToken(nextToken)
                    );
                    firewallGroups.addAll(
                        describeSecurityGroups
                            .getSecurityGroups()
                            .stream()
                            .map(sg -> convert(region, accountId, sg))
                            .collect(Collectors.toList())
                    );
                    nextToken = describeSecurityGroups.getNextToken();
                } while (nextToken != null);
                logger.warn("Region fetch complete.");
            } catch (AmazonClientException clientException) {
                //Region must be disabled
                logger.warn("Failed to fetch region " + region + " despite good credentials, region probably disabled or missing permissions.");
            }
        }

        firewallGroupCache.addAll(firewallGroups);

        return firewallGroups;
    }
}
