package br.imd.smartsysnc.service;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Reader;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;

import org.json.JSONObject;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.fasterxml.jackson.databind.ObjectMapper;

import br.imd.smartsysnc.utilEntity.EntityNGSILD;

/**
 * Classe para consumir e prover dados para importação
 */
@RestController
@RequestMapping("/sync")
@CrossOrigin(origins = "*")
public class SmartSyncController {

	private static String TOKEN = "1ab6b2a5b3c47fba85e4a987774707d6";
	private static int STATUS_OK = 200;

	@SuppressWarnings({ "unchecked" })
	@GetMapping(value = "/{entity}")
	public Object testConsumeRestApi(@PathVariable(required = true) String entity,
			@RequestParam(value = "limit", defaultValue = "100") int limit,
			@RequestParam(value = "offset", defaultValue = "1") int offset,
			@RequestParam(value = "ownLayerName", defaultValue = "") String ownLayerName) {
		URL url;

		String baseUrl = "https://quarkbi.esig.com.br/api/v1/dw/entity/" + entity + "?limit=" + limit + "&offset="
				+ offset;

		try {
			url = new URL(baseUrl);
			HttpURLConnection con = (HttpURLConnection) url.openConnection();
			con.setDoOutput(true);
			con.setRequestMethod("GET");
			con.setRequestProperty("Content-Type", "application/json");
			con.setRequestProperty("TOKEN", TOKEN);

			ObjectMapper mapper = new ObjectMapper();
			con.connect();

			if (con.getResponseCode() == STATUS_OK) {
				String body = readBodyReq(con);
				Object credenciais = mapper.readValue(body, Object.class);

				List<Object> listNGSILD = new ArrayList<>();

				listNGSILD = EntityNGSILD.converterToEntityNGSILD((LinkedHashMap<Object, Object>) credenciais,
						ownLayerName, entity);

				return listNGSILD;
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
		return null;
	}

	@GetMapping(value = "/jsonStateRN")
	public List<Object> getJsonStateRN() throws MalformedURLException, IOException {
//		InputStream is = new URL("https://raw.githubusercontent.com/tbrugz/geodata-br/master/geojson/geojs-24-mun.json")
//				.openStream();
		InputStreamReader is = new InputStreamReader(
				getClass().getResourceAsStream("/br/imd/smartsysnc/utilEntity/rn_geojson.json"), "utf-8");
		try {
			BufferedReader rd = new BufferedReader(is);
			String jsonText = readAll(rd);
			JSONObject json = new JSONObject(jsonText);

			List<Object> listStatesNGSILD = EntityNGSILD
					.converterStateRNJsonToEntityNGSILD(json.getJSONArray("features").toList());

			return listStatesNGSILD;
		} finally {
			is.close();
		}
	}

	private static String readAll(Reader rd) throws IOException {
		StringBuilder sb = new StringBuilder();
		int cp;
		while ((cp = rd.read()) != -1) {
			sb.append((char) cp);
		}
		return sb.toString();
	}

	private String readBodyReq(HttpURLConnection con) throws UnsupportedEncodingException, IOException {
		/* Lendo body */
		BufferedReader br = new BufferedReader(new InputStreamReader(con.getInputStream(), "UTF-8"));
		String body = "", temp = null;
		while ((temp = br.readLine()) != null)
			body += temp;

		return body;
	}
}
