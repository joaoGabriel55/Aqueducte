package br.imd.smartsysnc.controller;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.json.JSONObject;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.StringHttpMessageConverter;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.multipart.MultipartFile;

import com.fasterxml.jackson.databind.ObjectMapper;

import br.imd.smartsysnc.utils.CsvToNGSILDUtil;
import br.imd.smartsysnc.utils.EntityNGSILDUtils;
import br.imd.smartsysnc.utils.RequestsUtils;

/**
 * Classe para consumir e prover dados para importação
 */
@RestController
@RequestMapping("/sync")
@CrossOrigin(origins = "*")
public class SmartSyncController {

	private static String URL_SIGEDUC = "https://quarkbi.esig.com.br/api/v1/dw/entity/";
	public static String URL_SGEOL = "http://localhost:8091/sgeol-dm/v2/"; // Local
//    private static String URL_SGEOL = "http://10.7.52.26:8080/sgeol-dm/v2/"; //Test;
//	private static String URL_SGEOL = "http://10.7.52.76:8080/sgeol-dm/v2/"; // Production;

	@SuppressWarnings({ "unchecked" })
	@PostMapping(value = "/{entity}")
	public Object consumeRestApi(@PathVariable(required = true) String entity,
			@RequestParam(value = "limit", defaultValue = "100") int limit,
			@RequestParam(value = "offset", defaultValue = "1") int offset,
			@RequestParam(value = "ownLayerName", defaultValue = "") String ownLayerName,
			@RequestBody Map<Object, Object> contextLink) {

		String baseUrl = URL_SIGEDUC + entity + "?limit=" + limit + "&offset=" + offset;

		try {

			HttpURLConnection con = RequestsUtils.sendRequest(baseUrl, "GET", true);
			ObjectMapper mapper = new ObjectMapper();

			if (con.getResponseCode() == RequestsUtils.STATUS_OK) {
				String body = RequestsUtils.readBodyReq(con);
				Object credenciais = mapper.readValue(body, Object.class);

				List<Object> listNGSILD = new ArrayList<>();

				listNGSILD = EntityNGSILDUtils.converterToEntityNGSILD((LinkedHashMap<Object, Object>) credenciais,
						ownLayerName, entity, contextLink);

//				RestTemplate rt = new RestTemplate();
//				rt.getMessageConverters().add(new StringHttpMessageConverter());
//
//				String url = URL_SGEOL + ownLayerName;
//
//				for (int i = 0; i < listNGSILD.size(); i++) {
//					List<Object> entityForSGEOL = listNGSILD;
//					entityForSGEOL.add(listNGSILD.get(i).toString());
//					rt.postForEntity(url, entityForSGEOL, String.class);
//				}

				return listNGSILD;
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
		return null;
	}

	@SuppressWarnings({ "unchecked" })
	@GetMapping(value = "/colsFromSigEduc/{entity}")
	public List<Object> getColsFromSigEduc(@PathVariable(required = true) String entity) {
		String baseUrl = URL_SIGEDUC + entity + "?limit=1";
		try {

			HttpURLConnection con = RequestsUtils.sendRequest(baseUrl, "GET", true);
			ObjectMapper mapper = new ObjectMapper();

			if (con.getResponseCode() == RequestsUtils.STATUS_OK) {
				String body = RequestsUtils.readBodyReq(con);
				Object credenciais = mapper.readValue(body, Object.class);

				List<Object> listCols = new ArrayList<>();
				listCols = EntityNGSILDUtils.getColsFromSigEduc((LinkedHashMap<Object, Object>) credenciais);
				return listCols;
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
		return null;
	}

	@SuppressWarnings({ "unused", "unchecked" })
	@PostMapping(value = "/dataFromSigEducToSgeol/{entity}")
	public Object getDataFromSigEducToSgeol(@PathVariable(required = true) String entity,
			@RequestParam(value = "limit", defaultValue = "5") int limit,
			@RequestParam(value = "offset", defaultValue = "1") int offset,
			@RequestParam(value = "ownLayerName", defaultValue = "") String ownLayerName,
			@RequestBody Map<Object, Object> matchingJson) {

		String baseUrl = URL_SIGEDUC + entity + "?limit=" + limit + "&offset=" + offset;

		try {

			HttpURLConnection con = RequestsUtils.sendRequest(baseUrl, "GET", true);
			ObjectMapper mapper = new ObjectMapper();

			if (con.getResponseCode() == RequestsUtils.STATUS_OK) {
				String body = RequestsUtils.readBodyReq(con);
				Object credenciais = mapper.readValue(body, Object.class);

				List<Object> listNGSILD;
				List<Object> propertiesConvertNGSILD = new ArrayList<Object>();

				listNGSILD = EntityNGSILDUtils.converterToEntityNGSILD((LinkedHashMap<Object, Object>) credenciais,
						ownLayerName, entity, matchingJson);

				HashMap<Object, HashMap<Object, Object>> propertiesBasedOnContext = new HashMap<Object, HashMap<Object, Object>>();

				List<Object> listMatches = (List<Object>) matchingJson.get("matches");

				EntityNGSILDUtils.matchingWithContext(listMatches, listNGSILD, propertiesBasedOnContext);

				return listNGSILD;
			}
		} catch (

		IOException e) {
			e.printStackTrace();
		}
		return null;
	}

	@SuppressWarnings({ "unchecked" })
	@PostMapping(value = "/importDataToSGEOL/{entity}")
	@ResponseBody
	public ResponseEntity<HttpStatus> importDataToSGEOL(@PathVariable(required = true) String entity,
			@RequestBody List<Map<Object, Object>> dataToImport) throws UnsupportedEncodingException, IOException {

		for (Map<Object, Object> entidadeToImport : dataToImport) {
			String inepCode = (String) ((LinkedHashMap<Object, Object>) entidadeToImport.get("inepCod")).get("value");
			Boolean isExistis = EntityNGSILDUtils.isExistisEntity(entity, inepCode);
			if (isExistis != null) {
				if (!isExistis) {
					RestTemplate rt = new RestTemplate();
					rt.getMessageConverters().add(new StringHttpMessageConverter());
					String url = URL_SGEOL + entity;
					rt.postForEntity(url, entidadeToImport, String.class);
				}
			}
		}

		return ResponseEntity.ok(HttpStatus.OK);
	}

	@PostMapping(value = "/jsonStateRN")
	public String getJsonStateRN() throws MalformedURLException, IOException {
		InputStreamReader is = new InputStreamReader(
				getClass().getResourceAsStream("/br/imd/smartsysnc/utils/rn_geojson.json"), "utf-8");
		try {
			BufferedReader rd = new BufferedReader(is);
			String jsonText = RequestsUtils.readAll(rd);
			JSONObject json = new JSONObject(jsonText);

			List<Object> listStatesNGSILD = EntityNGSILDUtils
					.converterStateRNJsonToEntityNGSILD(json.getJSONArray("features").toList());

			RestTemplate rt = new RestTemplate();
			rt.getMessageConverters().add(new StringHttpMessageConverter());

			String url = URL_SGEOL + "municipio";

			for (int i = 0; i < listStatesNGSILD.size(); i++) {
				rt.postForEntity(url, listStatesNGSILD.get(i), String.class);
			}

			return "Data was imported!";

		} catch (Exception e) {
			throw e;
		} finally {
			is.close();
		}
	}

	@SuppressWarnings({ "rawtypes", "unchecked" })
	@PostMapping(value = "/csvToJson")
	public Object getCSVToJson(@RequestParam("file") MultipartFile uploadfile)
			throws MalformedURLException, IOException {

		if (uploadfile.isEmpty()) {
			return new ResponseEntity("Please select a file!", HttpStatus.OK);
		} else {
			InputStreamReader is = new InputStreamReader(uploadfile.getInputStream());
			try {
				List<Object> listWheaterJson = CsvToNGSILDUtil.convertCsvToNSGILD(is);
				return listWheaterJson;
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		return new ResponseEntity("Success", HttpStatus.OK);
	}
}
