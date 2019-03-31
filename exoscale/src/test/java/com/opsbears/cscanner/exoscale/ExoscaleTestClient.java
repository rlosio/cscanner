package com.opsbears.cscanner.exoscale;

import br.com.autonomiccs.apacheCloudStack.client.ApacheCloudStackClient;
import br.com.autonomiccs.apacheCloudStack.client.ApacheCloudStackRequest;
import br.com.autonomiccs.apacheCloudStack.client.beans.ApacheCloudStackUser;
import br.com.autonomiccs.apacheCloudStack.exceptions.ApacheCloudStackClientRequestRuntimeException;
import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;

import javax.annotation.ParametersAreNonnullByDefault;
import java.util.List;

@ParametersAreNonnullByDefault
public class ExoscaleTestClient {
    private final String apiKey;
    private final String apiSecret;
    private final ApacheCloudStackUser apacheCloudStackUser;
    private final ApacheCloudStackClient apacheCloudStackClient;


    public ExoscaleTestClient(
        String apiKey,
        String apiSecret
    ) {
        this.apiKey = apiKey;
        this.apiSecret = apiSecret;

        apacheCloudStackUser = new ApacheCloudStackUser(apiSecret, apiKey);
        apacheCloudStackClient = new ApacheCloudStackClient("https://api.exoscale.ch/compute", apacheCloudStackUser);
    }

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

    public void ensureRuleExists(String securityGroupName, String protocol, List<String> cidrList, int startPort, int endPort) {
        try {
            ApacheCloudStackRequest apacheCloudStackRequest = new ApacheCloudStackRequest("authorizeSecurityGroupIngress");
            apacheCloudStackRequest.addParameter("securitygroupname", securityGroupName);
            apacheCloudStackRequest.addParameter("protocol", protocol);
            apacheCloudStackRequest.addParameter("cidrList", String.join(",", cidrList));
            apacheCloudStackRequest.addParameter("startPort", startPort);
            apacheCloudStackRequest.addParameter("endPort", endPort);
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
