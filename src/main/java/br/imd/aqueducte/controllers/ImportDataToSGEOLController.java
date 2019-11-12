package br.imd.aqueducte.controllers;

import br.imd.aqueducte.models.response.Response;
import br.imd.aqueducte.treats.NGSILDTreat;
import br.imd.aqueducte.treats.impl.NGSILDTreatImpl;
import br.imd.aqueducte.utils.RequestsUtils;
import org.json.JSONArray;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import static br.imd.aqueducte.utils.RequestsUtils.APP_TOKEN;
import static br.imd.aqueducte.utils.RequestsUtils.USER_TOKEN;

@RestController
@RequestMapping("/sync/importToSgeol")
@CrossOrigin(origins = "*")
public class ImportDataToSGEOLController {

	@SuppressWarnings("rawtypes")
	@PostMapping(value = "/{layer}")
	public ResponseEntity<Response<List<String>>> importToSGEOL(@RequestHeader(APP_TOKEN) String appToken,
			@RequestHeader(USER_TOKEN) String userToken, @PathVariable String layer,
			@RequestBody Map<String, Object> dataNGSILD) {
		Response<List<String>> response = new Response<>();
		try {
			JSONArray jsonArrayNGSILD = new JSONArray((ArrayList) dataNGSILD.get("data_ngsild"));
			String url = RequestsUtils.URL_SGEOL + "v2/" + layer;

			NGSILDTreat ngsildTreat = new NGSILDTreatImpl();
			List<String> jsonArrayResponse = ngsildTreat.importToSGEOL(url, appToken, userToken,
					jsonArrayNGSILD);
			response.setData(jsonArrayResponse);
		} catch (Exception e) {
			response.getErrors().add(e.getLocalizedMessage());
			return ResponseEntity.badRequest().body(response);
		}
		return ResponseEntity.ok(response);
	}

}
