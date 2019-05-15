package br.imd.smartsysnc.service;

import java.util.List;

import org.springframework.stereotype.Component;

import br.imd.smartsysnc.models.MatchingConfig;

@Component
public interface MatchingConfigService {

	MatchingConfig createOrUpdate(MatchingConfig matchingConfig);

	List<MatchingConfig> findAll();

}
