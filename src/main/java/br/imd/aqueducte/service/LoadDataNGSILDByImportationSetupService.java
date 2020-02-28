package br.imd.aqueducte.service;

import br.imd.aqueducte.models.dtos.DataSetRelationship;
import org.springframework.stereotype.Component;

import java.util.LinkedHashMap;
import java.util.List;

@Component
public interface LoadDataNGSILDByImportationSetupService<T> {

    List<LinkedHashMap<String, Object>> loadData(T importationSetup, String userToken);

    List<LinkedHashMap<String, Object>> loadDataWebService(T importationSetup);

    List<LinkedHashMap<String, Object>> loadDataFile(T importationSetup, String userToken);

    int makeDataRelationshipAqueconnect(DataSetRelationship dataSetRelationship);
}
