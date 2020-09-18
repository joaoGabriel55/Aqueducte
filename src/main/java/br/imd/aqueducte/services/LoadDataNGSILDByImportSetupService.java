package br.imd.aqueducte.services;

import org.springframework.stereotype.Component;

import java.util.LinkedHashMap;
import java.util.List;

@Component
public interface LoadDataNGSILDByImportSetupService<T> {

    List<LinkedHashMap<String, Object>> loadData(T importationSetup, String hashConfig) throws Exception;

    List<LinkedHashMap<String, Object>> loadDataWebService(T importationSetup) throws Exception;

    List<LinkedHashMap<String, Object>> loadDataFile(T importationSetup, String hashConfig) throws Exception;
}
