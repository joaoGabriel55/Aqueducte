package br.imd.smartsysnc.controllers;

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
import org.springframework.beans.factory.annotation.Autowired;
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

import br.imd.smartsysnc.models.EntityWithLinkedID;
import br.imd.smartsysnc.models.ReferenceForRelationship;
import br.imd.smartsysnc.processors.EntityNGSILDProcessor;
import br.imd.smartsysnc.processors.MunicipioEntityNGSILDProcessor;
import br.imd.smartsysnc.processors.sigeduc.SigEducAPIEntityUtils;
import br.imd.smartsysnc.service.impl.EntityWithLinkedIDServiceImpl;
import br.imd.smartsysnc.utils.CsvToNGSILDUtil;
import br.imd.smartsysnc.utils.FormatterUtils;
import br.imd.smartsysnc.utils.MessageUtils;
import br.imd.smartsysnc.utils.RequestsUtils;

/**
 * Classe para consumir e prover dados para importação
 */
@RestController
@RequestMapping("/sync")
@CrossOrigin(origins = "*")
public class SmartSyncController {

	@Autowired
	private EntityWithLinkedIDServiceImpl entityService;

	@SuppressWarnings({ "unchecked" })
	@PostMapping(value = "/{entity}")
	public List<LinkedHashMap<Object, Object>> consumeRestApi(@PathVariable(required = true) String entity,
			@RequestParam(value = "limit", defaultValue = "100") int limit,
			@RequestParam(value = "offset", defaultValue = "1") int offset,
			@RequestParam(value = "ownLayerName", defaultValue = "") String ownLayerName,
			@RequestBody Map<Object, Object> contextLink) {

		String baseUrl = RequestsUtils.URL_SIGEDUC + entity + "?limit=" + limit + "&offset=" + offset;

		try {

			HttpURLConnection con = RequestsUtils.sendRequest(baseUrl, "GET", true);
			ObjectMapper mapper = new ObjectMapper();

			if (con.getResponseCode() == RequestsUtils.STATUS_OK) {
				String body = RequestsUtils.readBodyReq(con);
				Object credenciais = mapper.readValue(body, Object.class);

				EntityNGSILDProcessor entityNGSILD = new EntityNGSILDProcessor();

				return entityNGSILD.converterToEntityNGSILD((LinkedHashMap<Object, Object>) credenciais, ownLayerName,
						entity, contextLink);

			}
		} catch (IOException e) {
			e.printStackTrace();
		}
		return new ArrayList<>();
	}

	@GetMapping(value = "/colsFromSigEduc/{entity}")
	public List<Object> getColsFromSigEduc(@PathVariable(required = true) String entity) throws Exception {
		SigEducAPIEntityUtils apiEntityProcessor = new SigEducAPIEntityUtils();
		return apiEntityProcessor.getColsFromSigEduc(entity);

	}

	/**
	 * Generate Data to import for SGEOL (With matchings)
	 */
	@SuppressWarnings({ "unchecked" })
	@PostMapping(value = "/dataFromSigEducToSgeol/{entity}")
	public Object getDataFromSigEducToSgeol(@PathVariable(required = true) String entity,
			@RequestParam(value = "limit", defaultValue = "0") int limit,
			@RequestParam(value = "offset", defaultValue = "0") int offset,
			@RequestParam(value = "ownLayerName", defaultValue = "") String ownLayerName,
			@RequestParam(value = "dateOfImportation", defaultValue = "") String dateOfImportation,
			@RequestBody Map<Object, Object> matchingJson) {

		String limitParam = limit > 0 ? "limit=" + limit : "";
		String offsetParam = offset > 0 ? "&offset=" + offset : "";
		String date = dateOfImportation != "" ? "?date=" + dateOfImportation : "";

		String baseUrl = RequestsUtils.URL_SIGEDUC + entity + limitParam + offsetParam
				+ FormatterUtils.dataFormattedForSIGEduc(date);

		try {

			HttpURLConnection con = RequestsUtils.sendRequest(baseUrl, "GET", true);
			ObjectMapper mapper = new ObjectMapper();

			if (con.getResponseCode() == RequestsUtils.STATUS_OK) {
				String body = RequestsUtils.readBodyReq(con);
				Object credenciais = mapper.readValue(body, Object.class);

				List<LinkedHashMap<Object, Object>> listNGSILD;

				EntityNGSILDProcessor entityNGSILD = new EntityNGSILDProcessor();

				listNGSILD = entityNGSILD.converterToEntityNGSILD((LinkedHashMap<Object, Object>) credenciais,
						ownLayerName, entity, matchingJson);

				HashMap<Object, HashMap<Object, Object>> propertiesBasedOnContext = new HashMap<>();

				List<Object> listMatches = (List<Object>) matchingJson.get("matches");

				entityNGSILD.matchingWithContext(listMatches, listNGSILD, propertiesBasedOnContext);

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

		String entitySGEOL = null;
		List<ReferenceForRelationship> listIDsLinked = new ArrayList<>();
		String entityTeste = "escolas_seec";
		try {
			boolean importationToo = true;

			EntityWithLinkedID entityWithLinkedID = entityService.findByEntitySGEOL(entity);
			if (entityWithLinkedID != null)
				importationToo = false;

			int count = 0;
			for (Map<Object, Object> entidadeToImport : dataToImport) {

				if (count == 0) {
					Map<Object, Object> objNGSILD = (LinkedHashMap<Object, Object>) entidadeToImport.get("objNGSILD");
					entitySGEOL = objNGSILD.get("type").toString();
				}

				EntityNGSILDProcessor entityNGSILDProcessor = new EntityNGSILDProcessor();
				Map<Object, Object> idsLinked = (HashMap<Object, Object>) entityNGSILDProcessor
						.getLinkedIdListAndImportDataToSGEOL(entidadeToImport, entityTeste, importationToo);
				if (idsLinked != null) {
					String idSGEOL = (String) idsLinked.get("idSGEOL");
					Map<String, Object> idsRelationship = (LinkedHashMap<String, Object>) idsLinked
							.get("idForRelationShip");
					listIDsLinked.add(new ReferenceForRelationship(idSGEOL, idsRelationship));
				}
				count++;
			}

			if (entityWithLinkedID != null) {
				EntityWithLinkedIDServiceImpl entityWithLinkedIDService = new EntityWithLinkedIDServiceImpl();
				entityWithLinkedIDService.findLinkedIdsWhichAlreadyExists(listIDsLinked, entityWithLinkedID);
			} else {
				entityWithLinkedID = new EntityWithLinkedID();
				entityWithLinkedID.setEntitySGEOL(entitySGEOL);
				entityWithLinkedID.setEntityWebService(entityTeste);
				entityWithLinkedID.setWebService("SIGEduc"); // TODO Get the web service via DevBoard.
				entityWithLinkedID.setRefList(listIDsLinked);

				entityService.createOrUpdate(entityWithLinkedID);
			}

		} catch (Exception e) {
			e.printStackTrace();
		}

		return ResponseEntity.ok(HttpStatus.OK);
	}

	@PostMapping(value = "/jsonStateRN")
	public ResponseEntity<Object> getJsonStateRN() throws MalformedURLException, IOException {
		InputStreamReader is = new InputStreamReader(
				getClass().getResourceAsStream("/br/imd/smartsysnc/utils/rn_geojson.json"), "utf-8");
		try {
			MunicipioEntityNGSILDProcessor municipioEntityNGSILDProcessor = new MunicipioEntityNGSILDProcessor();
			BufferedReader rd = new BufferedReader(is);
			String jsonText = RequestsUtils.readAll(rd);
			JSONObject json = new JSONObject(jsonText);

			List<Object> listStatesNGSILD = municipioEntityNGSILDProcessor
					.converterStateRNJsonToEntityNGSILD(json.getJSONArray("features").toList());

			RestTemplate rt = new RestTemplate();
			rt.getMessageConverters().add(new StringHttpMessageConverter());

			String url = RequestsUtils.URL_SGEOL + "municipio";

			for (int i = 0; i < listStatesNGSILD.size(); i++) {
				rt.postForEntity(url, listStatesNGSILD.get(i), String.class);
			}

			return MessageUtils.sendMessage("Data was imported!", HttpStatus.OK);

		} catch (Exception e) {
			return MessageUtils.sendMessage("Error on importation", HttpStatus.BAD_REQUEST);
		} finally {
			is.close();
		}
	}

	@SuppressWarnings({ "rawtypes", "unchecked" })
	@PostMapping(value = "/csvToJson")
	public ResponseEntity<Object> getCSVToJson(@RequestParam("file") MultipartFile uploadfile)
			throws MalformedURLException, IOException {

		if (uploadfile.isEmpty()) {
			return new ResponseEntity("Please select a file!", HttpStatus.OK);
		} else {
			InputStreamReader is = new InputStreamReader(uploadfile.getInputStream());
			try {
				List<Object> listWheaterJson = CsvToNGSILDUtil.convertCsvToNSGILD(is);
				return MessageUtils.sendMessage(listWheaterJson, HttpStatus.BAD_REQUEST);
			} catch (Exception e) {
				return MessageUtils.sendMessage("Error on importation", HttpStatus.BAD_REQUEST);
			}
		}
	}
}
