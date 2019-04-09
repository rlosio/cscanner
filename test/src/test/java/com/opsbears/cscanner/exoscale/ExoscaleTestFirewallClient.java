package com.opsbears.cscanner.exoscale;

import br.com.autonomiccs.apacheCloudStack.client.ApacheCloudStackClient;
import br.com.autonomiccs.apacheCloudStack.client.ApacheCloudStackRequest;
import br.com.autonomiccs.apacheCloudStack.client.beans.ApacheCloudStackUser;
import br.com.autonomiccs.apacheCloudStack.exceptions.ApacheCloudStackClientRequestRuntimeException;
import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.opsbears.cscanner.firewall.Protocols;
import com.opsbears.cscanner.firewall.TestFirewallClient;

import javax.annotation.Nullable;
import javax.annotation.ParametersAreNonnullByDefault;
import java.util.List;

@ParametersAreNonnullByDefault
public class ExoscaleTestFirewallClient implements TestFirewallClient {
    private final String apiKey;
    private final String apiSecret;
    private final ApacheCloudStackUser apacheCloudStackUser;
    private final ApacheCloudStackClient apacheCloudStackClient;

    public ExoscaleTestFirewallClient(
        String apiKey,
        String apiSecret
    ) {
        this.apiKey = apiKey;
        this.apiSecret = apiSecret;

        apacheCloudStackUser = new ApacheCloudStackUser(apiSecret, apiKey);
        apacheCloudStackClient = new ApacheCloudStackClient("https://api.exoscale.ch/compute", apacheCloudStackUser);
    }

    @Override
    public void ensureSecurityGroupExists(String name) {
        try {
            ApacheCloudStackRequest apacheCloudStackRequest = new ApacheCloudStackRequest("createSecurityGroup");
            apacheCloudStackRequest.addParameter("name", name);
            String response = apacheCloudStackClient.executeRequest(apacheCloudStackRequest);
        } catch (ApacheCloudStackClientRequestRuntimeException e) {
            if (e.getStatusCode() != 431 && e.getResponse().contains("Unable to create security group, a group with name compliant already exisits.")) {
                throw e;
            }
        }
    }

    @Override
    public void ensureSecurityGroupAbsent(String name) {
        try {
            ApacheCloudStackRequest apacheCloudStackRequest = new ApacheCloudStackRequest("deleteSecurityGroup");
            apacheCloudStackRequest.addParameter("name", name);
            String response = apacheCloudStackClient.executeRequest(apacheCloudStackRequest);
        } catch (ApacheCloudStackClientRequestRuntimeException e) {
            throw e;
        }
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
        try {
            ApacheCloudStackRequest apacheCloudStackRequest = new ApacheCloudStackRequest(
                "authorizeSecurityGroupIngress");
            apacheCloudStackRequest.addParameter("securitygroupname", securityGroupName);
            if (protocol == null) {
                apacheCloudStackRequest.addParameter("protocol", "all");
            } else {
                apacheCloudStackRequest.addParameter("protocol", Protocols.getInstance().getProtocolNameByNumber(protocol).replaceAll("ipv6-icmp", "icmpv6"));
            }
            apacheCloudStackRequest.addParameter("cidrList", String.join(",", cidrList));
            if (startPort != null) {
                apacheCloudStackRequest.addParameter("startPort", startPort);
            }
            if (endPort != null) {
                apacheCloudStackRequest.addParameter("endPort", endPort);
            }
            if (icmpType != null) {
                apacheCloudStackRequest.addParameter("icmptype", icmpType);
            }
            if (icmpCode != null) {
                apacheCloudStackRequest.addParameter("icmpcode", icmpCode);
            }
            String response = apacheCloudStackClient.executeRequest(apacheCloudStackRequest);
            JsonObject responseObject = new Gson().fromJson(response, JsonObject.class);
            String jobId = responseObject
                .get("authorizesecuritygroupingressresponse")
                .getAsJsonObject()
                .get("jobid")
                .getAsString();

            JsonObject r;
            do {
                ApacheCloudStackRequest asyncRequest = new ApacheCloudStackRequest("queryAsyncJobResult");
                asyncRequest.addParameter("jobid", jobId);
                String result = apacheCloudStackClient.executeRequest(asyncRequest);
                responseObject = new Gson().fromJson(result, JsonObject.class);
                r = responseObject.get("queryasyncjobresultresponse").getAsJsonObject();
                if (r.get("jobstatus").getAsInt() == 2) {
                    JsonObject jobResult = r.get("jobresult").getAsJsonObject();;
                    throw new ApacheCloudStackClientRequestRuntimeException(
                        jobResult.get("errorcode").getAsInt(),
                        result,
                        r.get("cmd").getAsString()
                    );
                }
            } while (r.get("jobstatus").getAsInt() != 1);
        } catch (ApacheCloudStackClientRequestRuntimeException e) {
            if (!e.getResponse().contains("No new rule would be created.")) {
                throw e;
            }
        }
    }
}
