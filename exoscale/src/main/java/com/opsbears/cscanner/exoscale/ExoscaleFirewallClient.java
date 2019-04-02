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
import com.opsbears.cscanner.firewall.Protocols;

import javax.annotation.ParametersAreNonnullByDefault;
import java.util.ArrayList;
import java.util.List;

@ParametersAreNonnullByDefault
public class ExoscaleFirewallClient implements FirewallClient {
    private final Protocols protocols = Protocols.getInstance();
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

            Integer protocol;
            Integer fromPort = null;
            Integer toPort = null;
            Integer icmpType = null;
            Integer icmpCode = null;
            if (rule.get("protocol").getAsString().equalsIgnoreCase("all")) {
                protocol = null;
            } else {
                protocol = protocols.getProtocolIdByName(rule.get("protocol").getAsString());
            }
            String cidr = null;
            String securityGroupName = null;
            if (rule.has("startport")) {
                fromPort = rule.get("startport").getAsInt();
            }
            if (rule.has("endport")) {
                toPort = rule.get("endport").getAsInt();
            }
            if (fromPort != null && toPort != null && fromPort == 0 && toPort == 0 && protocol == null) {
                fromPort = null;
                toPort = null;
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
        if (direction == FirewallRule.Direction.EGRESS && rules.size() == 0) {
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
        }
        return firewallRules;
    }
}
