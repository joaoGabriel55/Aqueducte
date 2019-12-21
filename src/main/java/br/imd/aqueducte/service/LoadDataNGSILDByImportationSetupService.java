package br.imd.aqueducte.service;

import br.imd.aqueducte.models.ImportationSetup;
import org.springframework.stereotype.Component;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

@Component
public interface LoadDataNGSILDByImportationSetupService<T> {

    List<LinkedHashMap<String, Object>> loadData(T importationSetup);

    String sendRelationshipMapForAqueconnect(List<Map<String, Map>> relationshipMap);
}
