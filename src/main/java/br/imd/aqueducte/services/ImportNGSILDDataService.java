package br.imd.aqueducte.services;

import org.json.JSONArray;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public interface ImportNGSILDDataService {
    List<String> importData(String layer, String appToken, String userToken, JSONArray jsonArray) throws Exception;
}
