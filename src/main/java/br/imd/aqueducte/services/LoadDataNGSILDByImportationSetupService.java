package br.imd.aqueducte.services;

import org.springframework.stereotype.Component;

import java.util.LinkedHashMap;
import java.util.List;

@Component
public interface LoadDataNGSILDByImportationSetupService<T> {

    List<LinkedHashMap<String, Object>> loadData(T importationSetup, String sgeolInstance, String userToken) throws Exception;

    List<LinkedHashMap<String, Object>> loadDataWebService(T importationSetup, String sgeolInstance) throws Exception;

    List<LinkedHashMap<String, Object>> loadDataFile(T importationSetup, String sgeolInstance, String userToken) throws Exception;
}
