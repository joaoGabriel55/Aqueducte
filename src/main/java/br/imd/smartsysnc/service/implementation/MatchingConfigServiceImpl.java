package br.imd.smartsysnc.service.implementation;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import br.imd.smartsysnc.models.MatchingConfig;
import br.imd.smartsysnc.repositories.MatchingConfigRepository;
import br.imd.smartsysnc.service.MatchingConfigService;

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
