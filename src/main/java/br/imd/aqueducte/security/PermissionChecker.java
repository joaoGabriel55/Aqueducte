package br.imd.aqueducte.security;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.ParseException;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.util.EntityUtils;
import org.json.JSONArray;
import org.json.JSONObject;

import javax.servlet.http.HttpServletRequest;
import java.io.IOException;

import static br.imd.aqueducte.config.PropertiesParams.ROLE_AQUEDUCTE;
import static br.imd.aqueducte.config.PropertiesParams.URL_SGEOL;
import static br.imd.aqueducte.logger.LoggerMessage.logError;
import static br.imd.aqueducte.logger.LoggerMessage.logInfo;
import static br.imd.aqueducte.utils.RequestsUtils.getHttpClientInstance;

public class PermissionChecker {

    /**
     * Check if User from IDM have permission to access Smart Sync API.
     */
    public boolean checkSmartSyncPermissionAccess(String userToken, HttpServletRequest req) {
        if (userToken != null && !userToken.equals("")) {
            String IDM_URL = URL_SGEOL.replace("/v2", "");
            HttpGet request = new HttpGet(IDM_URL + "idm/users/info");

            // add request headers
            request.addHeader("user-token", userToken);

            try {
                HttpResponse response = getHttpClientInstance().execute(request);
                // Get HttpResponse Status
                logInfo("Logging ProtocolVersion: {}", response.getProtocolVersion());
                logInfo("Logging Status Code: {}", response.getStatusLine().getStatusCode());

                HttpEntity entity = response.getEntity();
                if (entity != null) {
                    // return it as a String
                    String result = EntityUtils.toString(entity);
                    JSONObject userLoggedJson = new JSONObject(result);
                    req.setAttribute("user-id", userLoggedJson.getString("id"));
                    JSONArray roles = userLoggedJson.getJSONArray("roles");

                    for (Object role : roles) {
                        JSONObject roleJson = new JSONObject(role.toString());
                        if (roleJson.getString("name").equals(ROLE_AQUEDUCTE)) {
                            logInfo("Authentication Status: {}", "SUCCESS");
                            return true;
                        }
                    }
                    logInfo("Authentication Status: {}", "WRONG");

                }
            } catch (ParseException e) {
                e.printStackTrace();
                logError("ParseException: {}", e.getMessage());
            } catch (IOException e) {
                e.printStackTrace();
                logError("IOException: {}", e.getMessage());
            }
        }
        return false;
    }

}
