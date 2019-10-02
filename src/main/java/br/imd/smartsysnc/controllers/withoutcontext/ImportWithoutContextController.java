package br.imd.smartsysnc.controllers.withoutcontext;

import java.util.Date;
import java.util.LinkedHashMap;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.mongodb.DuplicateKeyException;

import br.imd.smartsysnc.models.ImportationSetupWithoutContext;
import br.imd.smartsysnc.models.response.Response;
import br.imd.smartsysnc.processors.withoutcontext.ImportWithoutContextTreat;
import br.imd.smartsysnc.service.ImportationSetupWithoutContextService;

@RestController
@RequestMapping("/sync/withoutContextSetup")
@CrossOrigin(origins = "*")
public class ImportWithoutContextController {

	@Autowired
	private ImportationSetupWithoutContextService impSetupWithoutCxtService;

	@PostMapping(value = "/convertToNgsild/{layerPath}")
	public ResponseEntity<List<LinkedHashMap<Object, Object>>> convertToNgsildWithoutContext(
			@PathVariable(required = true) String layerPath, @RequestBody List<Object> data) {

		ImportWithoutContextTreat importWithoutContextTreat = new ImportWithoutContextTreat();
		List<LinkedHashMap<Object, Object>> listNGSILD;
		listNGSILD = importWithoutContextTreat.converterToEntityNGSILD(data, layerPath, null);

		return ResponseEntity.ok(listNGSILD);
	}

	@GetMapping
	public ResponseEntity<Response<List<ImportationSetupWithoutContext>>> findAllImportationSetupWithoutContext() {

		Response<List<ImportationSetupWithoutContext>> response = new Response<>();
		List<ImportationSetupWithoutContext> list = impSetupWithoutCxtService.findAll();
		response.setData(list);

		return ResponseEntity.ok(response);
	}

	@PostMapping
	public ResponseEntity<Response<ImportationSetupWithoutContext>> saveImportationSetupWithoutContext(
			@RequestBody ImportationSetupWithoutContext importationSetupWithoutContext) {

		Response<ImportationSetupWithoutContext> response = new Response<>();

		try {
			importationSetupWithoutContext.setDateCreated(new Date());
			importationSetupWithoutContext.setDateModified(null);
			ImportationSetupWithoutContext impSetupWithoutCxtPersisted = impSetupWithoutCxtService
					.createOrUpdate(importationSetupWithoutContext);
			response.setData(impSetupWithoutCxtPersisted);
		} catch (DuplicateKeyException e) {
			response.getErrors().add("Duplicate ID");
			return ResponseEntity.badRequest().body(response);
		} catch (Exception e) {
			response.getErrors().add(e.getMessage());
			return ResponseEntity.badRequest().body(response);
		}
		return ResponseEntity.ok(response);
	}

	@DeleteMapping(value = "/{id}")
	public ResponseEntity<Response<String>> deleteImportationSetupWithoutContext(
			@PathVariable(required = true) String id) {
		Response<String> response = new Response<>();
		try {

			String idDeleted = impSetupWithoutCxtService.delete(id);
			if (idDeleted != null)
				response.setData(idDeleted);
		} catch (Exception e) {
			response.getErrors().add(e.getMessage());
			return ResponseEntity.badRequest().body(response);
		}
		return ResponseEntity.ok(response);
	}

}
