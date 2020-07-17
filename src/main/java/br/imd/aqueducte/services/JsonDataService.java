package br.imd.aqueducte.services;

import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;

@Component
public interface JsonDataService {

    Object getFlatJSON(Object dataForConversion) throws Exception;

    List<String> getCollectionKeysFromJSON(Map<String, Object> dataForConversion);
}
