package br.imd.smartsysnc.utils;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Reader;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.URL;

public class RequestsUtils {
	public static int STATUS_OK = 200;
	private static String TOKEN = "dd0ce06d6d51c059f52924aefb2a6aad";

	public static String URL_SIGEDUC = "https://quarkbi.esig.com.br/api/v1/dw/entity/";
	public static String URL_SGEOL = "http://localhost:8091/sgeol-dm/v2/"; // Local
//    private static String URL_SGEOL = "http://10.7.52.26:8080/sgeol-dm/v2/"; //Test;
//	private static String URL_SGEOL = "http://10.7.52.76:8080/sgeol-dm/v2/"; // Production;

	public static String readAll(Reader rd) throws IOException {
		StringBuilder sb = new StringBuilder();
		int cp;
		while ((cp = rd.read()) != -1) {
			sb.append((char) cp);
		}
		return sb.toString();
	}

	public static HttpURLConnection sendRequest(String baseUrl, String method, boolean needToken) throws IOException {
		URL url = new URL(baseUrl);
		HttpURLConnection con = (HttpURLConnection) url.openConnection();
		con.setDoOutput(true);
		con.setRequestMethod(method);
		con.setRequestProperty("Content-Type", "application/json");
		if (needToken)
			con.setRequestProperty("TOKEN", TOKEN);

		con.connect();
		return con;
	}

	public static String readBodyReq(HttpURLConnection con) throws UnsupportedEncodingException, IOException {
		/* Lendo body */
		BufferedReader br = new BufferedReader(new InputStreamReader(con.getInputStream(), "UTF-8"));
		String body = "";
		String temp = null;
		while ((temp = br.readLine()) != null)
			body += temp;

		return body;
	}

}
