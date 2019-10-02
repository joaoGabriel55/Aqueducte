package br.imd.smartsysnc.service.impl;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import br.imd.smartsysnc.models.ImportationSetupWithoutContext;
import br.imd.smartsysnc.repositories.ImportationSetupWithoutContextRepository;
import br.imd.smartsysnc.service.ImportationSetupWithoutContextService;

@Service
public class ImportationSetupWithoutContextServiceImpl implements ImportationSetupWithoutContextService {

	@Autowired
	private ImportationSetupWithoutContextRepository importationSetupWithoutContextRepository;

	@Override
	public ImportationSetupWithoutContext createOrUpdate(
			ImportationSetupWithoutContext importationSetupWithoutContext) {
		return this.importationSetupWithoutContextRepository.save(importationSetupWithoutContext);
	}

	@Override
	public List<ImportationSetupWithoutContext> findAll() {
		return this.importationSetupWithoutContextRepository.findAll();
	}

	@Override
	public Optional<ImportationSetupWithoutContext> findById(String id) {
		return this.importationSetupWithoutContextRepository.findById(id);
	}

	@Override
	public String delete(String id) {
		Optional<ImportationSetupWithoutContext> impSetupWithoutCxt = findById(id);
		if (!impSetupWithoutCxt.isPresent())
			return null;
		String idForDelete = impSetupWithoutCxt.get().getId();
		importationSetupWithoutContextRepository.deleteById(idForDelete);
		return idForDelete;

	}

}
