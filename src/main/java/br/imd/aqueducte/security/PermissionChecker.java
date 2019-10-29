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
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import br.imd.aqueducte.filters.RequestResponseLoggingFilter;
import br.imd.aqueducte.utils.RequestsUtils;

public class PermissionChecker {
	private final static Logger LOG = LoggerFactory.getLogger(RequestResponseLoggingFilter.class);

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

				LOG.info("Logging ProtocolVersion :{}", response.getProtocolVersion());
				LOG.info("Logging Status Code :{}", response.getStatusLine().getStatusCode());

				HttpEntity entity = response.getEntity();
				if (entity != null) {
					// return it as a String
					String result = EntityUtils.toString(entity);
					JSONObject userLoggedJson = new JSONObject(result);
					req.setAttribute("user-id", userLoggedJson.getString("id"));
					JSONArray roles = userLoggedJson.getJSONArray("roles");

					for (Object role : roles) {
						JSONObject roleJson = new JSONObject(role.toString());
						if (roleJson.getString("name").toString().equals("smart_sync")) {
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
