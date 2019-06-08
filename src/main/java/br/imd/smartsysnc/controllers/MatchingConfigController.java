package br.imd.smartsysnc.controllers;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.mongodb.DuplicateKeyException;

import br.imd.smartsysnc.models.MatchingConfig;
import br.imd.smartsysnc.service.MatchingConfigService;

@RestController
@RequestMapping("/sync/matchingConfigController")
@CrossOrigin(origins = "*")
public class MatchingConfigController {

	@Autowired
	private MatchingConfigService matchingConfigService;

	@PostMapping
	public ResponseEntity<Object> createMatchingConfig(@RequestBody MatchingConfig matchingConfig) {

		try {
			matchingConfig.setDateCreated(new Date());
			matchingConfig.setDataModified(null);
			matchingConfigService.createOrUpdate(matchingConfig);
		} catch (DuplicateKeyException e) {
			return ResponseEntity.badRequest().body(e.getMessage());
		} catch (Exception e) {
			return ResponseEntity.badRequest().body(e.getMessage());
		}
		return ResponseEntity.ok(HttpStatus.OK);
	}

	@GetMapping
	public ResponseEntity<Object> getAllMatchingConfig() {
		List<MatchingConfig> listMatching = new ArrayList<>();
		try {
			listMatching = matchingConfigService.findAll();
		} catch (DuplicateKeyException e) {
			return ResponseEntity.badRequest().body(e.getMessage());
		} catch (Exception e) {
			return ResponseEntity.badRequest().body(e.getMessage());
		}
		return ResponseEntity.ok(listMatching);
	}

}
