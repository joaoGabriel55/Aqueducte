package br.imd.aqueducte.services.implementations;

import br.imd.aqueducte.models.dtos.MatchingConverterSetup;
import br.imd.aqueducte.models.mongodocuments.ImportNGSILDDataSetup;
import br.imd.aqueducte.services.LoadDataNGSILDByImportSetupService;
import br.imd.aqueducte.services.NGSILDConverterService;
import br.imd.aqueducte.services.validators.ImportNGSILDDataSetupValidator;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;

import static br.imd.aqueducte.models.mongodocuments.ImportationSetup.FILE;
import static br.imd.aqueducte.models.mongodocuments.ImportationSetup.WEB_SERVICE;
import static br.imd.aqueducte.utils.NGSILDConverterUtils.GEO_PROPERTY_TYPE;

@Service
@Log4j2
public class LoadDataNGSILDByImportNGSILDDataSetupServiceImpl
        extends LoadDataNGSILDByImportSetup
        implements LoadDataNGSILDByImportSetupService<ImportNGSILDDataSetup> {

    @Autowired
    private NGSILDConverterService ngsildConverterService;

    @Autowired
    private ImportNGSILDDataSetupValidator validator;

    public LoadDataNGSILDByImportNGSILDDataSetupServiceImpl() {
        super();
    }

    @Override
    public List<LinkedHashMap<String, Object>> loadData(ImportNGSILDDataSetup importationSetup, String sgeolInstance, String userToken) throws Exception {
        importationSetup.setLabel("JustLoad");
        validator.validImportNGSILDDataSetup(importationSetup);

        if (importationSetup.getImportType().equals(WEB_SERVICE)) {
            return loadDataWebService(importationSetup, sgeolInstance);
        } else if (importationSetup.getImportType().equals(FILE)) {
            return loadDataFile(importationSetup, sgeolInstance, userToken);
        }
        log.error("Load data error");
        throw new Exception();
    }

    @Override
    public List<LinkedHashMap<String, Object>> loadDataWebService(ImportNGSILDDataSetup importationSetup, String sgeolInstance) throws Exception {
        JsonDataServiceImpl jsonFlatTreatImpl = new JsonDataServiceImpl();

        // Load data from Webservice
        Map<String, Object> responseWSResult = loadDataWebservice(importationSetup);
        Map<String, Object> responseWSResultFlat = (Map<String, Object>) jsonFlatTreatImpl.getFlatJSON(responseWSResult);
        // Get data chosen
        Object dataFound = findDataRecursive(responseWSResultFlat, importationSetup.getDataSelected());
        if (!(dataFound instanceof List)) {
            log.error("dataFound is not List type");
            throw new Exception();
        }
        // Flat Json collection
        List<Map<String, Object>> dataCollectionFlat = (List<Map<String, Object>>) jsonFlatTreatImpl.getFlatJSON(dataFound);

        try {
            List<LinkedHashMap<String, Object>> listConvertedIntoNGSILD = ngsildConverterService.convertIntoNGSILD(
                    sgeolInstance,
                    getContextLinks(importationSetup.getContextSources().values()),
                    importationSetup.getMatchingConverterSetup(),
                    dataCollectionFlat,
                    importationSetup.getLayerPathSelected()
            );
            log.info("loadData WebService successfully");
            return listConvertedIntoNGSILD;
        } catch (Exception e) {
            log.error("loadData WebService error", e.getStackTrace());
            throw new Exception();
        }
    }

    @Override
    public List<LinkedHashMap<String, Object>> loadDataFile(ImportNGSILDDataSetup importationSetup, String sgeolInstance, String userToken) throws Exception {
        try {
            Map<String, Integer> fieldsFiltered = getFieldsForImportSetupFromFile(
                    getFileFields(sgeolInstance, userToken, importationSetup), importationSetup.getMatchingConverterSetup()
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

            List<LinkedHashMap<String, Object>> listConvertedIntoNGSILD = ngsildConverterService.convertIntoNGSILD(
                    sgeolInstance,
                    getContextLinks(importationSetup.getContextSources().values()),
                    importationSetup.getMatchingConverterSetup(),
                    fileConvertedIntoJSON,
                    importationSetup.getLayerPathSelected()
            );
            log.info("loadData File successfully");
            return listConvertedIntoNGSILD;
        } catch (Exception e) {
            log.error("loadData File error", e.getStackTrace());
            throw new Exception();
        }
    }

    private Map<String, Integer> getFieldsForImportSetupFromFile(
            Map<String, Integer> fileFields,
            LinkedHashMap<String, MatchingConverterSetup> matchingConverterSetup
    ) throws Exception {
        if (fileFields == null) {
            log.error("fileFields is empty");
            throw new Exception();
        }

        List<String> foreignPropertiesSelected = matchingConverterSetup.keySet().stream().collect(Collectors.toList());

        Map<String, Integer> filteredFieldsMap = new HashMap<>();
        for (String key : foreignPropertiesSelected) {
            if (fileFields.containsKey(key)) {
                filteredFieldsMap.put(key, fileFields.get(key));
            } else if (GEO_PROPERTY_TYPE.contains(key)) {
                matchingConverterSetup.get(key).getGeoLocationConfig().keySet().forEach(k -> {
                    filteredFieldsMap.put(k, fileFields.get(k));
                });
            }
        }
        log.info("getFieldsForImportSetupContextWithFile successfully");
        return filteredFieldsMap;
    }

    private List<String> getContextLinks(Collection<String> links) {
        List<String> linkList = links.stream().map((e) -> e).collect(Collectors.toList());
        return linkList;
    }
}
