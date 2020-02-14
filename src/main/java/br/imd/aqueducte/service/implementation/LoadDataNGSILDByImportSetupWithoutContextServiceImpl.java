package br.imd.aqueducte.service.implementation;

import br.imd.aqueducte.models.mongodocuments.ImportationSetupWithoutContext;
import br.imd.aqueducte.models.pojos.DataSetRelationship;
import br.imd.aqueducte.models.pojos.ImportNSILDDataWithoutContextConfig;
import br.imd.aqueducte.service.LoadDataNGSILDByImportationSetupService;
import br.imd.aqueducte.treats.JsonFlatConvertTreat;
import br.imd.aqueducte.treats.NGSILDTreat;
import br.imd.aqueducte.treats.impl.NGSILDTreatImpl;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

@Service
public class LoadDataNGSILDByImportSetupWithoutContextServiceImpl
        extends LoadDataNGSILDByImportSetup
        implements LoadDataNGSILDByImportationSetupService<ImportationSetupWithoutContext> {

    @Override
    public List<LinkedHashMap<String, Object>> loadData(ImportationSetupWithoutContext importationSetup) {
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
            List<LinkedHashMap<String, Object>> dataForConvert = filterFieldsSelectedIntoArray(
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
    public int makeDataRelationshipAqueconnect(DataSetRelationship dataSetRelationship) {
        return 0;
    }
}
