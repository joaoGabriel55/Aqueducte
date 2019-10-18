package br.imd.smartsysnc.security;

import java.io.IOException;

import org.apache.http.HttpEntity;
import org.apache.http.ParseException;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;
import org.json.JSONArray;
import org.json.JSONObject;

public class PermissionChecker {

	/**
	 * Check if User from IDM have permission to access Smart Sync API.
	 */
	public boolean checkSmartSyncPerssisionAccess(String userToken) {
		if (userToken != "" && userToken != null) {

			HttpGet request = new HttpGet("http://sgeolayers.imd.ufrn.br/sgeol-dm/idm/users/info");

			// add request headers
			request.addHeader("user-token", userToken);

			try (CloseableHttpClient httpClient = HttpClients.createDefault();
					CloseableHttpResponse response = httpClient.execute(request)) {

				// Get HttpResponse Status
				System.out.println(response.getProtocolVersion()); // HTTP/1.1
				System.out.println(response.getStatusLine().getStatusCode()); // 200
				System.out.println(response.getStatusLine().getReasonPhrase()); // OK
				System.out.println(response.getStatusLine().toString()); // HTTP/1.1 200 OK

				HttpEntity entity = response.getEntity();
				if (entity != null) {
					// return it as a String
					String result = EntityUtils.toString(entity);
					JSONObject userLoggedJson = new JSONObject(result);
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
