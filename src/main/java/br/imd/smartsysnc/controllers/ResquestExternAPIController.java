package br.imd.smartsysnc.controllers;

import java.io.IOException;
import java.util.Map;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import br.imd.smartsysnc.models.response.Response;
import br.imd.smartsysnc.utils.RequestsUtils;

@RestController
@RequestMapping("/sync/resquestExternAPIController")
@CrossOrigin(origins = "*")
public class ResquestExternAPIController {

	@PostMapping
	public ResponseEntity<Response<Map<String, Object>>> resquestDataExternAPI(@RequestBody Map<Object, Object> paramsResquest) {

		Response<Map<String, Object>> response = new Response<>();
		try {
			response.setData(RequestsUtils.resquestToAPI(paramsResquest));
		} catch (IOException e) {
			response.getErrors().add("Error at retrive data");
			e.printStackTrace();
		}

		return ResponseEntity.ok(response);
	}

}
