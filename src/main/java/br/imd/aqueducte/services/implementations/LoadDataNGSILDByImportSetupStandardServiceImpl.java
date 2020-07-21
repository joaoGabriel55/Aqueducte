package br.imd.aqueducte.services.implementations;

import br.imd.aqueducte.models.dtos.GeoLocationConfig;
import br.imd.aqueducte.models.dtos.ImportNSILDStandardDataConfig;
import br.imd.aqueducte.models.mongodocuments.ImportationSetupStandard;
import br.imd.aqueducte.services.LoadDataNGSILDByImportationSetupService;
import br.imd.aqueducte.services.NGSILDConverterService;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;

import static br.imd.aqueducte.models.mongodocuments.ImportationSetup.FILE;
import static br.imd.aqueducte.models.mongodocuments.ImportationSetup.WEB_SERVICE;

@SuppressWarnings("ALL")
@Service
@Log4j2
public class LoadDataNGSILDByImportSetupStandardServiceImpl
        extends LoadDataNGSILDByImportSetup
        implements LoadDataNGSILDByImportationSetupService<ImportationSetupStandard> {

    @Autowired
    private NGSILDConverterService ngsildConverterService;

    @Override
    public List<LinkedHashMap<String, Object>> loadData(
            ImportationSetupStandard ImportationSetupStandard,
            String sgeolInstance,
            String userToken
    ) throws Exception {
        if (ImportationSetupStandard.getImportType().equals(WEB_SERVICE)) {
            return loadDataWebService(ImportationSetupStandard, sgeolInstance);
        } else if (ImportationSetupStandard.getImportType().equals(FILE)) {
            return loadDataFile(ImportationSetupStandard, sgeolInstance, userToken);
        }
        log.error("Load data error");
        throw new Exception();
    }

    @Override
    public List<LinkedHashMap<String, Object>> loadDataWebService(
            ImportationSetupStandard importationSetup, String sgeolInstance
    ) throws Exception {
        JsonDataServiceImpl jsonFlatTreatImpl = new JsonDataServiceImpl();

        // Load data from Webservice
        Map<String, Object> responseWSResult = loadDataWebservice(importationSetup);
        Object dataFound = null;
        if (responseWSResult.get("data") instanceof ArrayList) {
            List<Map<String, Object>> responseListWSResultFlat = (List<Map<String, Object>>) jsonFlatTreatImpl.getFlatJSON(responseWSResult.get("data"));
            dataFound = responseListWSResultFlat;
        } else {
            Map<String, Object> responseWSResultFlat = (Map<String, Object>) jsonFlatTreatImpl.getFlatJSON(responseWSResult.get("data"));
            dataFound = findDataRecursive(responseWSResultFlat, importationSetup.getDataSelected());
        }
        // Get data chosen
        if (!(dataFound instanceof List)) {
            log.error("dataFound is not List type");
            throw new Exception();
        }
        // Flat Json collection
        List<Object> dataCollectionFlat = (List<Object>) jsonFlatTreatImpl.getFlatJSON(dataFound);
        List<Map<String, Object>> dataForConvert = filterFieldsSelectedIntoArray(
                dataCollectionFlat,
                importationSetup
        );

        try {
            ImportNSILDStandardDataConfig importConfig = new ImportNSILDStandardDataConfig();
            importConfig.setGeoLocationConfig(importationSetup.getFieldsGeolocationSelectedConfigs());
            importConfig.setDataContentForNGSILDConversion(dataForConvert);

            List<LinkedHashMap<String, Object>> listConvertedIntoNGSILD = ngsildConverterService.standardConverterNGSILD(
                    sgeolInstance,
                    importConfig,
                    importationSetup.getLayerPathSelected(),
                    null
            );
            log.info("loadData WebService successfuly");
            return listConvertedIntoNGSILD;
        } catch (Exception e) {
            log.error("loadData WebService error", e.getStackTrace());
            throw new Exception();
        }
    }

    @Override
    public List<LinkedHashMap<String, Object>> loadDataFile(
            ImportationSetupStandard importationSetup, String sgeolInstance, String userToken) throws Exception {
        try {
            Map<String, Integer> fieldsFiltered = getFieldsForImportSetupStandardWithFile(
                    getFileFields(sgeolInstance, userToken, importationSetup),
                    importationSetup.getFieldsSelected(),
                    importationSetup.getFieldsGeolocationSelectedConfigs()
            );
            if (fieldsFiltered == null) {
                log.error("fieldsFiltered is null");
                throw new Exception();
            }
            if (fieldsFiltered.size() == 0) {
                log.error("fieldsFiltered is empty");
                throw new Exception();
            }

            List<Map<String, Object>> fileConvertedIntoJSON = convertToJSON(
                    sgeolInstance, userToken, importationSetup, fieldsFiltered
            );

            ImportNSILDStandardDataConfig importConfig = new ImportNSILDStandardDataConfig();
            importConfig.setGeoLocationConfig(importationSetup.getFieldsGeolocationSelectedConfigs());
            importConfig.setDataContentForNGSILDConversion(fileConvertedIntoJSON);

            List<LinkedHashMap<String, Object>> listConvertedIntoNGSILD = ngsildConverterService.standardConverterNGSILD(
                    sgeolInstance,
                    importConfig,
                    importationSetup.getLayerPathSelected(),
                    null
            );
            log.info("loadData File successfuly");
            return listConvertedIntoNGSILD;
        } catch (Exception e) {
            log.error("loadData File error", e.getStackTrace());
            throw new Exception();
        }
    }

    private Map<String, Integer> getFieldsForImportSetupStandardWithFile(
            Map<String, Integer> fileFields,
            List<String> fieldsSelected,
            List<GeoLocationConfig> fieldsGeolocationSelectedConfigs
    ) throws Exception {
        if (fileFields == null) {
            log.error("fileFields is empty");
            throw new Exception();
        }
        Map<String, Integer> filteredFieldsMap = new HashMap<>();
        for (String key : fileFields.keySet()) {
            if (fieldsSelected.contains(key)) {
                filteredFieldsMap.put(key, fileFields.get(key));
            }
        }
        if (fieldsGeolocationSelectedConfigs != null && fieldsGeolocationSelectedConfigs.size() > 0) {
            List<String> geolocationKeys = fieldsGeolocationSelectedConfigs.stream().map(
                    (elem) -> elem.getKey()
            ).collect(Collectors.toList());
            for (String key : fileFields.keySet()) {
                if (geolocationKeys.contains(key)) {
                    filteredFieldsMap.put(key, fileFields.get(key));
                }
            }
        }
        log.info("getFieldsForImportSetupContextWithFile successfuly");
        return filteredFieldsMap;
    }
}
