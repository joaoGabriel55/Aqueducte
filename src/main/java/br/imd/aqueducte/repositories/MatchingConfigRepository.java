package br.imd.aqueducte.repositories;

import org.springframework.data.mongodb.repository.MongoRepository;

import br.imd.aqueducte.models.MatchingConfig;

public interface MatchingConfigRepository extends MongoRepository<MatchingConfig, String>{

}
