package br.imd.smartsysnc.repositories;

import br.imd.smartsysnc.models.ImportationSetupWithoutContext;
import br.imd.smartsysnc.models.LinkedIdsForRelationship;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;

import java.util.List;

public interface ImportationSetupWithoutContextRepository
        extends MongoRepository<ImportationSetupWithoutContext, String> {

}
