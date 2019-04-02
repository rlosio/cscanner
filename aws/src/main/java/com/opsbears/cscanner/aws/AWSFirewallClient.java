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

import javax.annotation.ParametersAreNonnullByDefault;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@ParametersAreNonnullByDefault
public class AWSFirewallClient implements FirewallClient {
    private final AWSConfiguration awsConfiguration;
    private final Protocols protocols = Protocols.getInstance();

    public AWSFirewallClient(AWSConfiguration awsConfiguration) {
        this.awsConfiguration = awsConfiguration;
    }

    private String getArn(Regions region, String accountId, String securityGroupId) {
        return "arn:aws:ec2:" + region.getName() + ":" + accountId + ":security-group/" + securityGroupId;
    }

    private List<FirewallRule> getFirewallRules(Regions region, String accountId, SecurityGroup securityGroup, List<IpPermission> permissions, FirewallRule.Direction direction) {
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
                    FirewallRule.Direction.INGRESS,
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
                    FirewallRule.Direction.INGRESS,
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
                    FirewallRule.Direction.INGRESS,
                    FirewallRule.Rule.ALLOW
                ));
            }
        }
        return firewallRules;
    }

    private FirewallGroup convert(Regions region, String accountId, SecurityGroup securityGroup) {
        List<FirewallRule> firewallRules = new ArrayList<>();

        firewallRules.addAll(getFirewallRules(
            region,
            accountId,
            securityGroup,
            securityGroup.getIpPermissions(),
            FirewallRule.Direction.INGRESS
        ));
        firewallRules.addAll(getFirewallRules(
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
        for (Regions region : Regions.values()) {
            try {
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
            } catch (AmazonClientException clientException) {
                //Region must be disabled
            }
        }

        return firewallGroups;
    }
}
