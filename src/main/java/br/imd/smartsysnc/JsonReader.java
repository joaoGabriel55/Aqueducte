package br.imd.smartsysnc;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.net.URL;
import java.nio.charset.Charset;

import org.json.JSONException;
import org.json.JSONObject;

public class JsonReader {
	private static String readAll(Reader rd) throws IOException {
		StringBuilder sb = new StringBuilder();
		int cp;
		while ((cp = rd.read()) != -1) {
			sb.append((char) cp);
		}
		return sb.toString();
	}

	public static Object readJsonFromUrl(String url) throws IOException, JSONException {
		InputStream is = new URL(url).openStream();
		try {
			BufferedReader rd = new BufferedReader(new InputStreamReader(is, Charset.forName("UTF-8")));
			String jsonText = readAll(rd);
			JSONObject json = new JSONObject(jsonText);
			return jsonText;
		} finally {
			is.close();
		}
	}

	public static void main(String[] args) throws IOException, JSONException {
		Object json = readJsonFromUrl(
				"https://raw.githubusercontent.com/tbrugz/geodata-br/master/geojson/geojs-24-mun.json");
		System.out.println(json.toString());
		//System.out.println(json.get("id"));
	}
}
