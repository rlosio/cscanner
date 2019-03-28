package com.opsbears.cscanner.exoscale;

import br.com.autonomiccs.apacheCloudStack.client.ApacheCloudStackClient;
import br.com.autonomiccs.apacheCloudStack.client.ApacheCloudStackRequest;
import br.com.autonomiccs.apacheCloudStack.client.beans.ApacheCloudStackUser;
import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.opsbears.cscanner.firewall.FirewallClient;
import com.opsbears.cscanner.firewall.FirewallGroup;
import com.opsbears.cscanner.firewall.FirewallRule;

import javax.annotation.ParametersAreNonnullByDefault;
import java.util.ArrayList;
import java.util.List;

@ParametersAreNonnullByDefault
public class ExoscaleFirewallClient implements FirewallClient {
    private final String apiKey;
    private final String apiSecret;

    public ExoscaleFirewallClient(
        String apiKey,
        String apiSecret
    ) {
        this.apiKey = apiKey;
        this.apiSecret = apiSecret;
    }

    @Override
    public List<FirewallGroup> listFirewallGroups() {
        ApacheCloudStackUser apacheCloudStackUser = new ApacheCloudStackUser(apiSecret, apiKey);
        ApacheCloudStackClient apacheCloudStackClient = new ApacheCloudStackClient("https://api.exoscale.ch/compute", apacheCloudStackUser);

        ApacheCloudStackRequest apacheCloudStackRequest = new ApacheCloudStackRequest("listSecurityGroups");
        String response = apacheCloudStackClient.executeRequest(apacheCloudStackRequest);
        JsonObject responseObject = new Gson().fromJson(response, JsonObject.class);
        JsonArray securityGroupList = responseObject
            .get("listsecuritygroupsresponse")
            .getAsJsonObject()
            .get("securitygroup")
            .getAsJsonArray();

        List<FirewallGroup> firewallGroups = new ArrayList<>();
        for (int i = 0; i < responseObject.get("listsecuritygroupsresponse").getAsJsonObject().get("count").getAsInt(); i++) {
            JsonObject securityGroup = securityGroupList.get(i).getAsJsonObject();


            JsonArray ingressRules = securityGroup.get("ingressrule").getAsJsonArray();
            JsonArray egressRules = securityGroup.get("egressrule").getAsJsonArray();

            List<FirewallRule> firewallRules = new ArrayList<>();
            firewallRules.addAll(extractRules(ingressRules, FirewallRule.Direction.INGRESS));
            firewallRules.addAll(extractRules(egressRules, FirewallRule.Direction.EGRESS));
            firewallGroups.add(new FirewallGroup(
                securityGroup.get("name").getAsString(),
                firewallRules
            ));
        }
        return firewallGroups;
    }

    private List<FirewallRule> extractRules(JsonArray rules, FirewallRule.Direction direction) {
        List<FirewallRule> firewallRules = new ArrayList<>();
        for (int i = 0; i < rules.size(); i++) {
            JsonObject rule = rules.get(i).getAsJsonObject();

            int protocol;
            switch(rule.get("protocol").getAsString()) {
                case "tcp":
                    protocol = 6;
                    break;
                case "udp":
                    protocol = 17;
                    break;
                case "icmp":
                    protocol = 1;
                    break;
                case "icmpv6":
                    protocol = 58;
                    break;
                case "ah":
                    protocol = 51;
                    break;
                case "esp":
                    protocol = 50;
                    break;
                case "gre":
                    protocol = 47;
                    break;
                case "ipip":
                    protocol = 94;
                    break;
                default:
                    throw new RuntimeException("Unsupported protocol: " + rule.get("protocol").getAsString());
            }
            Integer fromPort = null;
            Integer toPort = null;
            Integer icmpType = null;
            Integer icmpCode = null;
            String cidr = null;
            String securityGroupName = null;
            if (rule.has("startport")) {
                fromPort = rule.get("startport").getAsInt();
            }
            if (rule.has("endport")) {
                toPort = rule.get("endport").getAsInt();
            }
            if (rule.has("cidr")) {
                cidr = rule.get("cidr").getAsString();
            }
            if (rule.has("securitygroupname")) {
                securityGroupName = rule.get("securitygroupname").getAsString();
            }
            if (rule.has("icmptype")) {
                icmpType = rule.get("icmptype").getAsInt();
            }
            if (rule.has("icmpcode")) {
                icmpCode = rule.get("icmpcode").getAsInt();
            }

            firewallRules.add(new FirewallRule(
                protocol,
                cidr,
                securityGroupName,
                fromPort,
                toPort,
                icmpType,
                icmpCode,
                direction,
                FirewallRule.Rule.ALLOW
            ));
        }
        if (direction == FirewallRule.Direction.INGRESS) {
            firewallRules.add(new FirewallRule(
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
        } else if (direction == FirewallRule.Direction.EGRESS) {
            if (rules.size() == 0) {
                firewallRules.add(new FirewallRule(
                    null,
                    null,
                    null,
                    null,
                    null,
                    null,
                    null,
                    direction,
                    FirewallRule.Rule.ALLOW
                ));
            } else {
                firewallRules.add(new FirewallRule(
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
            }
        }
        return firewallRules;
    }
}
