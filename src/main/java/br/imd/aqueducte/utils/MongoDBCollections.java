package br.imd.aqueducte.utils;

import static br.imd.aqueducte.utils.PropertiesParams.TEST_ENV;

public class MongoDBCollections {

    public static final String TASK = TEST_ENV ? "task_test" : "task";
}
