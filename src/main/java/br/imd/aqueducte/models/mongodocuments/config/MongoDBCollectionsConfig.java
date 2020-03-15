package br.imd.aqueducte.models.mongodocuments.config;

import static br.imd.aqueducte.utils.PropertiesParams.TEST_ENV;

public class MongoDBCollectionsConfig {

    public static final String TASK = TEST_ENV ? "task_test" : "task";
    public static final String ENTITIES_RELATIONSHIP_SETUP = TEST_ENV ? "entitiesRelationshipSetup_test" : "entitiesRelationshipSetup";
}
