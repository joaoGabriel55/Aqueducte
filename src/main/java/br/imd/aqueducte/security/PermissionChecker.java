package br.imd.aqueducte.security;

import java.io.IOException;

import javax.servlet.http.HttpServletRequest;

import org.apache.http.HttpEntity;
import org.apache.http.ParseException;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;
import org.json.JSONArray;
import org.json.JSONObject;

import br.imd.aqueducte.logger.LoggerMessage;
import br.imd.aqueducte.utils.RequestsUtils;

public class PermissionChecker {
    private final static String ROLE_AQUEDUCTE = "aqueducte";
//    private final static String ROLE_AQUEDUCTE = "smart_sync";

    /**
     * Check if User from IDM have permission to access Smart Sync API.
     */
    public boolean checkSmartSyncPerssisionAccess(String userToken, HttpServletRequest req) {
        if (userToken != "" && userToken != null) {

            HttpGet request = new HttpGet(RequestsUtils.URL_SGEOL + "idm/users/info");

            // add request headers
            request.addHeader("user-token", userToken);

            try (CloseableHttpClient httpClient = HttpClients.createDefault();
                 CloseableHttpResponse response = httpClient.execute(request)) {

                // Get HttpResponse Status
                LoggerMessage.LOG.info("Logging ProtocolVersion: {}", response.getProtocolVersion());
                LoggerMessage.LOG.info("Logging Status Code: {}", response.getStatusLine().getStatusCode());

                HttpEntity entity = response.getEntity();
                if (entity != null) {
                    // return it as a String
                    String result = EntityUtils.toString(entity);
                    JSONObject userLoggedJson = new JSONObject(result);
                    req.setAttribute("user-id", userLoggedJson.getString("id"));
                    JSONArray roles = userLoggedJson.getJSONArray("roles");

                    for (Object role : roles) {
                        JSONObject roleJson = new JSONObject(role.toString());
                        if (roleJson.getString("name").toString().equals(ROLE_AQUEDUCTE)) {
                            return true;
                        }
                    }

                }

            } catch (ParseException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return false;
    }

}
