package br.imd.smartsysnc.repositories;

import org.springframework.data.mongodb.repository.MongoRepository;

import br.imd.smartsysnc.models.MatchingConfig;

public interface MatchingConfigRepository extends MongoRepository<MatchingConfig, String>{

}
