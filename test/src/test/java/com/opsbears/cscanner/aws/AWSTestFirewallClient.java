package com.opsbears.cscanner.aws;

import com.amazonaws.auth.AWSCredentials;
import com.amazonaws.auth.AWSCredentialsProvider;
import com.amazonaws.regions.Regions;
import com.amazonaws.services.ec2.AmazonEC2;
import com.amazonaws.services.ec2.AmazonEC2ClientBuilder;
import com.amazonaws.services.ec2.model.*;
import com.opsbears.cscanner.firewall.Protocols;
import com.opsbears.cscanner.firewall.TestFirewallClient;

import javax.annotation.Nullable;
import javax.annotation.ParametersAreNonnullByDefault;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

@ParametersAreNonnullByDefault
public class AWSTestFirewallClient implements TestFirewallClient {
    private final String apiKey;
    private final String apiSecret;
    private final AmazonEC2 client;

    public AWSTestFirewallClient(
        String apiKey,
        String apiSecret
    ) {
        this.apiKey = apiKey;
        this.apiSecret = apiSecret;

        this.client = AmazonEC2ClientBuilder.standard().withCredentials(new AWSCredentialsProvider() {
            @Override
            public AWSCredentials getCredentials() {
                return new AWSCredentials() {
                    @Override
                    public String getAWSAccessKeyId() {
                        return apiKey;
                    }

                    @Override
                    public String getAWSSecretKey() {
                        return apiSecret;
                    }
                };
            }

            @Override
            public void refresh() {

            }
        })
            .withRegion(Regions.DEFAULT_REGION)
            .build();
    }

    @Override
    public void ensureSecurityGroupExists(String name) {
        CreateVpcResult vpc = client.createVpc(new CreateVpcRequest("10.0.0.0/24"));
        try {
            client.createTags(
                new CreateTagsRequest()
                    .withTags(new Tag().withKey("Name").withValue(name))
                    .withResources(vpc.getVpc().getVpcId())
            );
            CreateSecurityGroupResult sg = client.createSecurityGroup(
                new CreateSecurityGroupRequest()
                    .withDescription("CScanner Test")
                    .withGroupName(name)
                    .withVpcId(vpc.getVpc().getVpcId())
            );
            client.createTags(
                new CreateTagsRequest()
                    .withTags(new Tag().withKey("Name").withValue(name))
                    .withResources(sg.getGroupId())
            );
        } catch (Exception e) {
            client.deleteVpc(new DeleteVpcRequest(vpc.getVpc().getVpcId()));
            throw e;
        }
    }

    @Override
    public void ensureSecurityGroupAbsent(String name) {

        String nextToken = null;
        do {
            DescribeVpcsResult describeVpcs = client.describeVpcs(
                new DescribeVpcsRequest()
                    .withFilters(
                        Arrays.asList(new Filter("tag:Name", Arrays.asList(name)))
                    )
                    .withNextToken(nextToken)
            );
            for (Vpc vpc : describeVpcs.getVpcs()) {
                String sgNextToken = null;
                do {
                    DescribeSecurityGroupsResult securityGroups = client
                        .describeSecurityGroups(new DescribeSecurityGroupsRequest()
                            .withFilters(
                                new Filter("vpc-id", Arrays.asList(vpc.getVpcId())),
                                new Filter("tag:Name", Arrays.asList(name))
                            )
                        )
                        .withNextToken(sgNextToken);
                    for (SecurityGroup securityGroup : securityGroups.getSecurityGroups()) {
                        client.deleteSecurityGroup(
                            new DeleteSecurityGroupRequest().withGroupId(securityGroup.getGroupId())
                        );
                    }
                    sgNextToken = securityGroups.getNextToken();
                } while (sgNextToken != null);
                client.deleteVpc(new DeleteVpcRequest().withVpcId(vpc.getVpcId()));
            }
            nextToken = describeVpcs.getNextToken();
        } while (nextToken != null);
    }

    @Override
    public void ensureRuleExists(
        String securityGroupName,
        @Nullable
        Integer protocol,
        List<String> cidrList,
        @Nullable Integer startPort,
        @Nullable Integer endPort,
        @Nullable Integer icmpType,
        @Nullable Integer icmpCode
    ) {
        String nextToken = null;
        do {
            DescribeVpcsResult describeVpcs = client.describeVpcs(
                new DescribeVpcsRequest()
                    .withFilters(
                        Arrays.asList(new Filter("tag:Name", Arrays.asList(securityGroupName)))
                    )
                    .withNextToken(nextToken)
            );
            for (Vpc vpc : describeVpcs.getVpcs()) {
                String sgNextToken = null;
                do {
                    DescribeSecurityGroupsResult securityGroups = client
                        .describeSecurityGroups(new DescribeSecurityGroupsRequest()
                            .withFilters(
                                new Filter("vpc-id", Arrays.asList(vpc.getVpcId())),
                                new Filter("tag:Name", Arrays.asList(securityGroupName))
                            )
                        )
                        .withNextToken(sgNextToken);
                    for (SecurityGroup securityGroup : securityGroups.getSecurityGroups()) {
                        AuthorizeSecurityGroupIngressRequest request = new AuthorizeSecurityGroupIngressRequest();
                        request.setGroupId(securityGroup.getGroupId());
                        request.setIpPermissions(
                            cidrList
                                .stream()
                                .map(
                                    cidr -> {
                                        IpPermission permission = new IpPermission();
                                        permission
                                            .withFromPort(startPort)
                                            .withToPort(endPort)
                                            ;
                                        if (protocol != null) {
                                            String protocolName = Protocols.getInstance().getProtocolNameByNumber(protocol);
                                            if (protocolName.equalsIgnoreCase("ipv6-icmp")) {
                                                protocolName = "icmpv6";
                                            }
                                            permission.withIpProtocol(protocolName);
                                        } else {
                                            permission.withIpProtocol("-1");
                                        }
                                        if (cidr.contains(":")) {
                                            //IPv6
                                            permission.withIpv6Ranges(new Ipv6Range().withCidrIpv6(cidr));
                                        } else {
                                            //IPv4
                                            permission.withIpv4Ranges(new IpRange().withCidrIp(cidr));
                                        }
                                        return permission;
                                    }
                                )
                                .collect(Collectors.toList())
                        );
                        client.authorizeSecurityGroupIngress(request);
                    }
                    sgNextToken = securityGroups.getNextToken();
                } while (sgNextToken != null);
            }
            nextToken = describeVpcs.getNextToken();
        } while (nextToken != null);
    }
}
