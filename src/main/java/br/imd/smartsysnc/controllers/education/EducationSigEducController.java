package br.imd.smartsysnc.controllers.education;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import br.imd.smartsysnc.models.EntityWithLinkedID;
import br.imd.smartsysnc.models.ReferenceForRelationship;
import br.imd.smartsysnc.processors.education.sigeduc.SigEducAPIEntityUtils;
import br.imd.smartsysnc.processors.education.sigeduc.treats.EscolaNGSILDTreat;
import br.imd.smartsysnc.service.impl.EntityWithLinkedIDServiceImpl;
import br.imd.smartsysnc.utils.FormatterUtils;
import br.imd.smartsysnc.utils.RequestsUtils;

/**
 * Classe para consumir e prover dados para importação
 */
@RestController
@RequestMapping("/sync")
@CrossOrigin(origins = "*")
public class EducationSigEducController {

	@Autowired
	private EntityWithLinkedIDServiceImpl entityService;

	@SuppressWarnings({ "unchecked" })
	@PostMapping(value = "/{entity}")
	public ResponseEntity<Object> consumeRestApi(
			@PathVariable(required = true) String entity,
			@RequestParam(value = "limit", defaultValue = "1024") int limit,
			@RequestParam(value = "offset", defaultValue = "1") int offset,
			@RequestParam(value = "ownLayerName", defaultValue = "") String ownLayerName,
			@RequestBody Map<Object, Object> contextLink)
			throws JsonParseException, JsonMappingException, UnsupportedEncodingException, IOException {

		String baseUrl = SigEducAPIEntityUtils.getUrlOfEntity("escola_seec", limit, offset);

		HttpURLConnection con = RequestsUtils.sendRequest(baseUrl, "GET", true);
		ObjectMapper mapper = new ObjectMapper();

		if (con.getResponseCode() == RequestsUtils.STATUS_OK) {
			String body = RequestsUtils.readBodyReq(con);
			Object credenciais = mapper.readValue(body, Object.class);

			EscolaNGSILDTreat entityNGSILD = new EscolaNGSILDTreat();
			List<LinkedHashMap<Object, Object>> response = entityNGSILD.convertToEntityNGSILD(
					(LinkedHashMap<Object, Object>) credenciais, ownLayerName, entity, contextLink);

			return ResponseEntity.ok(response);
		} else {
			return ResponseEntity.badRequest().body(con.getContent());
		}

	}

	@GetMapping(value = "/colsFromSigEduc/{entity}")
	public List<Object> getColsFromSigEduc(@PathVariable(required = true) String entity) throws Exception {
		SigEducAPIEntityUtils apiEntityProcessor = new SigEducAPIEntityUtils();
		return apiEntityProcessor.getColsFromSigEduc(entity);
	}

	/**
	 * Generate Data to import for SGEOL (With matchings)
	 * 
	 * @throws IOException
	 * @throws UnsupportedEncodingException
	 * @throws JsonMappingException
	 * @throws JsonParseException
	 */
	@SuppressWarnings({ "unchecked" })
	@PostMapping(value = "/dataFromSigEducToSgeol/{entitySgeol}/{entityService}")
	public ResponseEntity<List<LinkedHashMap<Object, Object>>> getDataFromSigEducToSgeol(
			@PathVariable(required = true) String entitySgeol,
			@PathVariable(required = true) String entityService,
			@RequestParam(value = "dateOfImportation", defaultValue = "") String dateOfImportation,
			@RequestParam(value = "limit", defaultValue = "0") int limit,
			@RequestParam(value = "offset", defaultValue = "0") int offset,
			@RequestBody Map<Object, Object> matchingJson)
			throws JsonParseException, JsonMappingException, UnsupportedEncodingException, IOException {

		String limitParam = limit > 0 ? "limit=" + limit : "";
		String offsetParam = offset > 0 ? "&offset=" + offset : "";
		String date = dateOfImportation.length() > 0  ? "?date=" + dateOfImportation : "";

		String baseUrl = RequestsUtils.URL_SIGEDUC + entityService + limitParam + offsetParam
				+ FormatterUtils.dataFormattedForSIGEduc(date);

		HttpURLConnection con = RequestsUtils.sendRequest(baseUrl, "GET", true);
		ObjectMapper mapper = new ObjectMapper();

		if (con.getResponseCode() == RequestsUtils.STATUS_OK) {
			String body = RequestsUtils.readBodyReq(con);
			Object credenciais = mapper.readValue(body, Object.class);

			List<LinkedHashMap<Object, Object>> listNGSILD;

			EscolaNGSILDTreat entityNGSILD = new EscolaNGSILDTreat();

			listNGSILD = entityNGSILD.convertToEntityNGSILD(
					(LinkedHashMap<Object, Object>) credenciais, 
					entitySgeol,
					entityService, 
					matchingJson);

			HashMap<Object, HashMap<Object, Object>> propertiesBasedOnContext = new HashMap<>();

			List<Object> listMatches = (List<Object>) matchingJson.get("matches");

			entityNGSILD.matchingWithContext(listMatches, listNGSILD, propertiesBasedOnContext);

			return ResponseEntity.ok(listNGSILD);
		} else {
			return ResponseEntity.badRequest().build();
		}

	}

	@SuppressWarnings({ "unchecked" })
	@PostMapping(value = "/importDataToSGEOL/{entityFromService}")
	@ResponseBody
	public ResponseEntity<HttpStatus> importDataToSGEOL(@PathVariable(required = true) String entityFromService,
			@RequestBody List<Map<Object, Object>> dataToImport) throws UnsupportedEncodingException, IOException {

		String entitySGEOL = null;
		List<ReferenceForRelationship> listIDsLinked = new ArrayList<>();
		
		try {
			int count = 0;
			for (Map<Object, Object> entidadeToImport : dataToImport) {

				if (count == 0) {
					Map<Object, Object> objNGSILD = (LinkedHashMap<Object, Object>) entidadeToImport.get("objNGSILD");
					entitySGEOL = objNGSILD.get("type").toString();
				}

				EscolaNGSILDTreat ngsildTreat = new EscolaNGSILDTreat();
				Map<Object, Object> idsLinked = (HashMap<Object, Object>) ngsildTreat
						.getLinkedIdListForImportDataToSGEOL(entidadeToImport);
				if (idsLinked != null) {
					String idSGEOL = (String) idsLinked.get("idSGEOL");
					Map<String, Object> idsRelationship = (LinkedHashMap<String, Object>) idsLinked
							.get("idForRelationShip");
					listIDsLinked.add(new ReferenceForRelationship(idSGEOL, idsRelationship));
				}
				count++;
			}

			EntityWithLinkedID entityWithLinkedID = entityService.findByEntitySGEOL(entitySGEOL);

			if (entityWithLinkedID != null) {
				EntityWithLinkedIDServiceImpl entityWithLinkedIDService = new EntityWithLinkedIDServiceImpl();
				entityWithLinkedID = entityWithLinkedIDService.findLinkedIdsWhichAlreadyExists(listIDsLinked,
						entityWithLinkedID, "id_escola");

			} else {
				entityWithLinkedID = new EntityWithLinkedID();
				entityWithLinkedID.setEntitySGEOL(entitySGEOL);
				entityWithLinkedID.setEntityWebService(entityFromService);
				entityWithLinkedID.setWebService("SIGEduc"); // TODO Get the web service via DevBoard.
				entityWithLinkedID.setRefList(listIDsLinked);

				entityService.createOrUpdate(entityWithLinkedID);
			}

		} catch (Exception e) {
			e.printStackTrace();
		}

		return ResponseEntity.ok(HttpStatus.OK);
	}

/*	@SuppressWarnings({ "rawtypes", "unchecked" })
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
	}*/
}