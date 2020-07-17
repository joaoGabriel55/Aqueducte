package br.imd.aqueducte.security;

import lombok.extern.log4j.Log4j2;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.ParseException;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.util.EntityUtils;
import org.json.JSONArray;
import org.json.JSONObject;

import javax.servlet.http.HttpServletRequest;
import java.io.IOException;

import static br.imd.aqueducte.utils.RequestsUtils.getHttpClientInstance;

@Log4j2
public class PermissionChecker {

    /**
     * Check if User from IDM have permission to access Smart Sync API.
     */
    public boolean checkSmartSyncPermissionAccess(String sgeolInstance, String userToken, HttpServletRequest req) {
        if (userToken != null && !userToken.equals("") && sgeolInstance != null && !sgeolInstance.equals("")) {
            HttpGet request = new HttpGet(sgeolInstance + "/idm/users/info");
            // add request headers
            request.addHeader("user-token", userToken);
            try {
                HttpResponse response = getHttpClientInstance().execute(request);
                // Get HttpResponse Status
                log.info("Logging ProtocolVersion: {}", response.getProtocolVersion());
                log.info("Logging Status Code: {}", response.getStatusLine().getStatusCode());

                HttpEntity entity = response.getEntity();
                if (entity != null) {
                    // return it as a String
                    String result = EntityUtils.toString(entity);
                    JSONObject userLoggedJson = new JSONObject(result);
                    req.setAttribute("user-id", userLoggedJson.getString("id"));
                    JSONArray roles = userLoggedJson.getJSONArray("roles");

                    for (Object role : roles) {
                        JSONObject roleJson = new JSONObject(role.toString());
                        if (roleJson.getString("name").contains("gerente")) {
                            log.info("Authentication Status: {}", "SUCCESS");
                            return true;
                        }
                    }
                    log.info("Authentication Status: {}", "WRONG");

                }
            } catch (ParseException e) {
                e.printStackTrace();
                log.error("ParseException: {}", e.getMessage());
            } catch (IOException e) {
                e.printStackTrace();
                log.error("IOException: {}", e.getMessage());
            }
        }
        return false;
    }

}
