package br.imd.aqueducte.service.implementation;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import br.imd.aqueducte.models.MatchingConfig;
import br.imd.aqueducte.repositories.MatchingConfigRepository;
import br.imd.aqueducte.service.MatchingConfigService;

@Service
public class MatchingConfigServiceImpl implements MatchingConfigService {

	@Autowired
	private MatchingConfigRepository matchingConfigRepository;

	@Override
	public MatchingConfig createOrUpdate(MatchingConfig matchingConfig) {
		return this.matchingConfigRepository.save(matchingConfig);
	}

	@Override
	public List<MatchingConfig> findAll() {
		return this.matchingConfigRepository.findAll();
	}

}
