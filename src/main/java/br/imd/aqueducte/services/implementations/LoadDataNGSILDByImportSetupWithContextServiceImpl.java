package br.imd.aqueducte.services.implementations;

import br.imd.aqueducte.models.dtos.MatchingConfig;
import br.imd.aqueducte.models.mongodocuments.ImportationSetupWithContext;
import br.imd.aqueducte.services.LoadDataNGSILDByImportationSetupService;
import br.imd.aqueducte.treats.NGSILDTreat;
import br.imd.aqueducte.treats.impl.JsonFlatTreatImpl;
import br.imd.aqueducte.treats.impl.NGSILDTreatImpl;
import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;

import static br.imd.aqueducte.models.mongodocuments.ImportationSetup.FILE;
import static br.imd.aqueducte.models.mongodocuments.ImportationSetup.WEB_SERVICE;

@SuppressWarnings("ALL")
@Service
@Log4j2
public class LoadDataNGSILDByImportSetupWithContextServiceImpl
        extends LoadDataNGSILDByImportSetup
        implements LoadDataNGSILDByImportationSetupService<ImportationSetupWithContext> {

    public LoadDataNGSILDByImportSetupWithContextServiceImpl() {
        super();
    }

    @Override
    public List<LinkedHashMap<String, Object>> loadData(
            ImportationSetupWithContext importationSetupWithContext,
            String sgeolInstance,
            String userToken
    ) throws Exception {
        if (importationSetupWithContext.getImportType().equals(WEB_SERVICE)) {
            return loadDataWebService(importationSetupWithContext, sgeolInstance);
        } else if (importationSetupWithContext.getImportType().equals(FILE)) {
            return loadDataFile(importationSetupWithContext, sgeolInstance, userToken);
        }
        log.error("Load data error");
        throw new Exception();
    }

    @Override
    public List<LinkedHashMap<String, Object>> loadDataWebService(
            ImportationSetupWithContext importationSetupWithContext, String sgeolInstance
    ) throws Exception {
        JsonFlatTreatImpl jsonFlatTreatImpl = new JsonFlatTreatImpl();

        // Load data from Webservice
        Map<String, Object> responseWSResult = loadDataWebservice(importationSetupWithContext);
        Map<String, Object> responseWSResultFlat = (Map<String, Object>) jsonFlatTreatImpl.getFlatJSON(responseWSResult);
        // Get data chosen
        Object dataFound = findDataRecursive(responseWSResultFlat, importationSetupWithContext.getDataSelected());
        if (!(dataFound instanceof List)) {
            log.error("dataFound is not List type");
            throw new Exception();
        }
        // Flat Json collection
        List<Object> dataCollectionFlat = (List<Object>) jsonFlatTreatImpl.getFlatJSON(dataFound);
        List<Map<String, Object>> dataForConvert = filterFieldsSelectedIntoArray(
                dataCollectionFlat,
                importationSetupWithContext
        );

        // Convert o NGSI-LD
        NGSILDTreat ngsildTreat = new NGSILDTreatImpl();
        try {
            List<LinkedHashMap<String, Object>> listConvertedIntoNGSILD = ngsildTreat.matchingWithContextAndConvertToEntityNGSILD(
                    sgeolInstance,
                    getContextLinks(importationSetupWithContext.getContextSources().values()),
                    importationSetupWithContext.getMatchingConfigList(),
                    dataForConvert,
                    importationSetupWithContext.getLayerPathSelected()
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
            ImportationSetupWithContext importationSetup,
            String sgeolInstance,
            String userToken) throws Exception {
        try {
            Map<String, Integer> fieldsFiltered = getFieldsForImportSetupContextWithFile(
                    getFileFields(sgeolInstance, userToken, importationSetup), importationSetup.getMatchingConfigList()
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
            NGSILDTreat ngsildTreat = new NGSILDTreatImpl();
            List<LinkedHashMap<String, Object>> listConvertedIntoNGSILD = ngsildTreat.matchingWithContextAndConvertToEntityNGSILD(
                    sgeolInstance,
                    getContextLinks(importationSetup.getContextSources().values()),
                    importationSetup.getMatchingConfigList(),
                    fileConvertedIntoJSON,
                    importationSetup.getLayerPathSelected()
            );
            log.info("loadData File successfuly");
            return listConvertedIntoNGSILD;
        } catch (Exception e) {
            log.error("loadData File error", e.getStackTrace());
            throw new Exception();
        }
    }

    private Map<String, Integer> getFieldsForImportSetupContextWithFile(
            Map<String, Integer> fileFields,
            List<MatchingConfig> matchingConfigList
    ) throws Exception {
        if (fileFields == null) {
            log.error("fileFields is empty");
            throw new Exception();
        }
        List<String> foreignPropertiesSelected = (List<String>) matchingConfigList.stream()
                .filter((elem) -> elem.getForeignProperty() != null || elem.getGeoLocationConfig().size() > 0)
                .map((elemFilted) -> {
                    if (elemFilted.getGeoLocationConfig().size() > 0) {
                        return elemFilted.getGeoLocationConfig().stream().map((elem) -> elem.getKey()).collect(Collectors.toList());
                    }
                    return elemFilted.getForeignProperty();
                }).reduce(new ArrayList<String>(), (arrayFinal, elem) -> {
                    if (elem instanceof ArrayList) {
                        ((ArrayList) elem).stream().forEach((el) -> ((ArrayList) arrayFinal).add(el));
                    } else {
                        ((ArrayList) arrayFinal).add(elem);
                    }
                    return arrayFinal;
                });

        Map<String, Integer> filteredFieldsMap = new HashMap<>();
        for (String key : foreignPropertiesSelected) {
            if (foreignPropertiesSelected.contains(key)) {
                filteredFieldsMap.put(key, fileFields.get(key));
            }
        }
        log.info("getFieldsForImportSetupContextWithFile successfuly");
        return filteredFieldsMap;
    }

    private List<String> getContextLinks(Collection<String> links) {
        List<String> linkList = links.stream().map((e) -> e).collect(Collectors.toList());
        return linkList;
    }
}
