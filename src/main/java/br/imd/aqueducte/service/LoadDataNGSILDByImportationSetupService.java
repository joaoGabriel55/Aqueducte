package br.imd.aqueducte.service;

import br.imd.aqueducte.models.pojos.DataSetRelationship;
import org.springframework.stereotype.Component;

import java.util.LinkedHashMap;
import java.util.List;

@Component
public interface LoadDataNGSILDByImportationSetupService<T> {

    List<LinkedHashMap<String, Object>> loadData(T importationSetup);

    int makeDataRelationshipAqueconnect(DataSetRelationship dataSetRelationship);
}
