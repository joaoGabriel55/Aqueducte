package br.imd.smartsysnc.repositories;

import org.springframework.data.mongodb.repository.MongoRepository;

import br.imd.smartsysnc.models.ImportationSetupWithoutContext;

public interface ImportationSetupWithoutContextRepository
		extends MongoRepository<ImportationSetupWithoutContext, String> {

}
