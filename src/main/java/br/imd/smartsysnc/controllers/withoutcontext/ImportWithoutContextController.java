package br.imd.smartsysnc.controllers.withoutcontext;

import java.util.LinkedHashMap;
import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import br.imd.smartsysnc.processors.withoutcontext.ImportWithoutContextTreat;

@RestController
@RequestMapping("/sync/withoutContext")
@CrossOrigin(origins = "*")
public class ImportWithoutContextController {

	@PostMapping(value = "/convertToNgsild/{layerPath}")
	public ResponseEntity<List<LinkedHashMap<Object, Object>>> getDataFromSigEducToSgeol(
			@PathVariable(required = true) String layerPath, @RequestBody List<Object> data) {

		ImportWithoutContextTreat importWithoutContextTreat = new ImportWithoutContextTreat();
		List<LinkedHashMap<Object, Object>> listNGSILD;
		listNGSILD = importWithoutContextTreat.converterToEntityNGSILD(data, layerPath, null);

		return ResponseEntity.ok(listNGSILD);
	}

}
