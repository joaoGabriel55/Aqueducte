package br.imd.aqueducte.service.implementation;

import br.imd.aqueducte.models.dtos.DataSetRelationship;
import br.imd.aqueducte.models.dtos.GeoLocationConfig;
import br.imd.aqueducte.models.dtos.ImportNSILDDataWithoutContextConfig;
import br.imd.aqueducte.models.mongodocuments.ImportationSetupWithoutContext;
import br.imd.aqueducte.service.LoadDataNGSILDByImportationSetupService;
import br.imd.aqueducte.treats.JsonFlatConvertTreat;
import br.imd.aqueducte.treats.NGSILDTreat;
import br.imd.aqueducte.treats.impl.NGSILDTreatImpl;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;

import static br.imd.aqueducte.models.mongodocuments.ImportationSetup.FILE;
import static br.imd.aqueducte.models.mongodocuments.ImportationSetup.WEB_SERVICE;

@Service
public class LoadDataNGSILDByImportSetupWithoutContextServiceImpl
        extends LoadDataNGSILDByImportSetup
        implements LoadDataNGSILDByImportationSetupService<ImportationSetupWithoutContext> {

    @Override
    public List<LinkedHashMap<String, Object>> loadData(
            ImportationSetupWithoutContext ImportationSetupWithoutContext,
            String userToken
    ) {
        if (ImportationSetupWithoutContext.getImportType().equals(WEB_SERVICE)) {
            return loadDataWebService(ImportationSetupWithoutContext);
        } else if (ImportationSetupWithoutContext.getImportType().equals(FILE)) {
            return loadDataFile(ImportationSetupWithoutContext, userToken);
        }
        return null;
    }

    @Override
    public List<LinkedHashMap<String, Object>> loadDataWebService(ImportationSetupWithoutContext importationSetup) {
        JsonFlatConvertTreat jsonFlatConvertTreat = new JsonFlatConvertTreat();

        // Load data from Webservice
        Map<String, Object> responseWSResult = loadDataWebservice(importationSetup);
        Object dataFound = null;
        if (responseWSResult.get("data") instanceof ArrayList) {
            List<Map<String, Object>> responseListWSResultFlat = (List<Map<String, Object>>) jsonFlatConvertTreat.getJsonFlat(responseWSResult.get("data"));
            dataFound = responseListWSResultFlat;
        } else {
            Map<String, Object> responseWSResultFlat = (Map<String, Object>) jsonFlatConvertTreat.getJsonFlat(responseWSResult.get("data"));
            dataFound = findDataRecursive(responseWSResultFlat, importationSetup.getDataSelected());
        }
        // Get data chosen
        if (dataFound instanceof List) {
            // Flat Json collection
            List<Object> dataCollectionFlat = (List<Object>) jsonFlatConvertTreat.getJsonFlat(dataFound);
            List<Map<String, Object>> dataForConvert = filterFieldsSelectedIntoArray(
                    dataCollectionFlat,
                    importationSetup
            );

            // Convert o NGSI-LD
            NGSILDTreat ngsildTreat = new NGSILDTreatImpl();
            try {
                ImportNSILDDataWithoutContextConfig importConfig = new ImportNSILDDataWithoutContextConfig();
                importConfig.setGeoLocationConfig(importationSetup.getFieldsGeolocationSelectedConfigs());
                importConfig.setDataContentForNGSILDConversion(dataForConvert);

                List<LinkedHashMap<String, Object>> listConvertedIntoNGSILD = ngsildTreat.convertToEntityNGSILD(
                        importConfig,
                        importationSetup.getLayerSelected(),
                        null
                );
                return listConvertedIntoNGSILD;
            } catch (Exception e) {
                e.printStackTrace();
                return null;
            }
        }
        return null;
    }

    @Override
    public List<LinkedHashMap<String, Object>> loadDataFile(
            ImportationSetupWithoutContext importationSetup, String userToken) {
        try {
            Map<String, Integer> fieldsFiltered = getFieldsForImportSetupStandardWithFile(
                    getFileFields(userToken, importationSetup),
                    importationSetup.getFieldsSelected(),
                    importationSetup.getFieldsGeolocationSelectedConfigs()
            );
            if (fieldsFiltered != null && fieldsFiltered.size() > 0) {
                List<Map<String, Object>> fileConvertedIntoJSON = convertToJSON(userToken, importationSetup, fieldsFiltered);
                // Convert o NGSI-LD
                NGSILDTreat ngsildTreat = new NGSILDTreatImpl();
                try {
                    ImportNSILDDataWithoutContextConfig importConfig = new ImportNSILDDataWithoutContextConfig();
                    importConfig.setGeoLocationConfig(importationSetup.getFieldsGeolocationSelectedConfigs());
                    importConfig.setDataContentForNGSILDConversion(fileConvertedIntoJSON);

                    List<LinkedHashMap<String, Object>> listConvertedIntoNGSILD = ngsildTreat.convertToEntityNGSILD(
                            importConfig,
                            importationSetup.getLayerSelected(),
                            null
                    );
                    return listConvertedIntoNGSILD;
                } catch (Exception e) {
                    e.printStackTrace();
                    return null;
                }
            } else {
                return null;
            }
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    private Map<String, Integer> getFieldsForImportSetupStandardWithFile(
            Map<String, Integer> fileFields,
            List<String> fieldsSelected,
            List<GeoLocationConfig> fieldsGeolocationSelectedConfigs
    ) {
        if (fileFields != null) {
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
            return filteredFieldsMap;
        } else {
            return null;
        }
    }

    @Override
    public int makeDataRelationshipAqueconnect(DataSetRelationship dataSetRelationship) {
        return 0;
    }
}
