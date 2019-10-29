package br.imd.aqueducte.service;

import java.util.List;

import org.springframework.stereotype.Component;

import br.imd.aqueducte.models.MatchingConfig;

@Component
public interface MatchingConfigService {

	MatchingConfig createOrUpdate(MatchingConfig matchingConfig);

	List<MatchingConfig> findAll();

}
