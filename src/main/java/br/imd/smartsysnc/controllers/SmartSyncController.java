package br.imd.smartsysnc.controllers;

import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/sync")
@CrossOrigin(origins = "*")
public class SmartSyncController {
	
	@GetMapping
	public void smartSyncHome() {
		return;
	}
	
}