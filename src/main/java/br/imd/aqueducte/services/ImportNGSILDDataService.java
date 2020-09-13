package br.imd.aqueducte.services;

import org.json.JSONArray;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;

@Component
public interface ImportNGSILDDataService {
    void importData(String layer, Map<String, String> headers, Map<String, String> allParams, JSONArray jsonArray) throws Exception;
}
