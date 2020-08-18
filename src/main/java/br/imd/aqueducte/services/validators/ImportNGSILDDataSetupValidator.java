package br.imd.aqueducte.services.validators;

import br.imd.aqueducte.models.dtos.MatchingConverterSetup;
import br.imd.aqueducte.models.mongodocuments.ImportNGSILDDataSetup;
import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Component;

import java.util.Map;

import static br.imd.aqueducte.models.mongodocuments.ImportationSetup.FILE;
import static br.imd.aqueducte.models.mongodocuments.ImportationSetup.WEB_SERVICE;
import static br.imd.aqueducte.utils.NGSILDConverterUtils.GEO_PROPERTY_TYPE;

@Component
@Log4j2
public class ImportNGSILDDataSetupValidator {

    private String errorMessage;

    public void validImportNGSILDDataSetup(ImportNGSILDDataSetup setup) throws Exception {
        if (setup.getIdUser() == null || setup.getIdUser() == "") {
            errorMessage = "ImportNGSILDDataSetup - user ID is required";
            log.error(errorMessage);
            throw new Exception(errorMessage);
        } else if (setup.getLabel() == null || setup.getLabel() == "") {
            errorMessage = "ImportNGSILDDataSetup - label is required";
            log.error(errorMessage);
            throw new Exception(errorMessage);
        } else if (setup.getImportType() == null) {
            errorMessage = "ImportNGSILDDataSetup - import type is required";
            log.error(errorMessage);
            throw new Exception(errorMessage);
        } else if (!setup.getImportType().equals(FILE) && !setup.getImportType().equals(WEB_SERVICE)) {
            errorMessage = "ImportNGSILDDataSetup - import type must be FILE or WEB_SERVICE";
            log.error(errorMessage);
            throw new Exception(errorMessage);
        }

        if (setup.getImportType().equals(WEB_SERVICE)) {
            if (setup.getBaseUrl() == null || setup.getBaseUrl() == "") {
                errorMessage = "ImportNGSILDDataSetup - Base URL is required";
                log.error(errorMessage);
                throw new Exception(errorMessage);
            } else if (setup.getPath() == null || setup.getPath() == "") {
                errorMessage = "ImportNGSILDDataSetup - Path is required";
                log.error(errorMessage);
                throw new Exception(errorMessage);
            } else if (setup.getHttpVerb() == null || setup.getHttpVerb() == "") {
                errorMessage = "ImportNGSILDDataSetup - HTTP Verb is required";
                log.error(errorMessage);
                throw new Exception(errorMessage);
            } else if (setup.isUseBodyData()) {
                if (setup.getBodyData() == null && setup.getBodyData().size() == 0) {
                    errorMessage = "ImportNGSILDDataSetup - BodyData is required";
                    log.error(errorMessage);
                    throw new Exception(errorMessage);
                }
            } else if (setup.getDataSelected() == null || setup.getDataSelected() == "") {
                errorMessage = "ImportNGSILDDataSetup - Data Selected is required";
                log.error(errorMessage);
                throw new Exception(errorMessage);
            }
        } else if (setup.getImportType().equals(FILE)) {
            if (setup.getFilePath() == null || setup.getFilePath() == "") {
                errorMessage = "ImportNGSILDDataSetup - File Path is required";
                log.error(errorMessage);
                throw new Exception(errorMessage);
            } else if (setup.getDelimiterFileContent() == null || setup.getDelimiterFileContent() == "") {
                errorMessage = "ImportNGSILDDataSetup - Delimiter File Content is required";
                log.error(errorMessage);
                throw new Exception(errorMessage);
            }
        }

        if (setup.getLayerSelected() == null || setup.getLayerSelected() == "") {
            errorMessage = "ImportNGSILDDataSetup - Layer Selected is required";
            log.error(errorMessage);
            throw new Exception(errorMessage);
        } else if (setup.getLayerPathSelected() == null || setup.getLayerPathSelected() == "") {
            errorMessage = "ImportNGSILDDataSetup - Layer Path Selected is required";
            log.error(errorMessage);
            throw new Exception(errorMessage);
        } else if (setup.getFieldsAvailable() == null || setup.getFieldsAvailable().size() == 0) {
            errorMessage = "ImportNGSILDDataSetup - Fields Available is required";
            log.error(errorMessage);
            throw new Exception(errorMessage);
        }

        if (setup.isUseContext() && (setup.getContextSources() == null || setup.getContextSources().size() == 0)) {
            errorMessage = "ImportNGSILDDataSetup - At least one Context Source is required";
            log.error(errorMessage);
            throw new Exception(errorMessage);
        }

        if (setup.getMatchingConverterSetup() == null) {
            errorMessage = "ImportNGSILDDataSetup - MatchingConverterSetup is required";
            log.error(errorMessage);
            throw new Exception(errorMessage);
        }

        for (Map.Entry<String, MatchingConverterSetup> entry : setup.getMatchingConverterSetup().entrySet()) {
            String key = entry.getKey();
            MatchingConverterSetup converterSetup = entry.getValue();
            if (converterSetup.getFinalProperty() == null) {
                errorMessage = "ImportNGSILDDataSetup - MatchingConverterSetup " + key + " - Final property is required";
                log.error(errorMessage);
                throw new Exception(errorMessage);
            } else if (converterSetup.getForeignProperty() == null) {
                errorMessage = "ImportNGSILDDataSetup - MatchingConverterSetup " + key + " - Foreign property is required";
                log.error(errorMessage);
                throw new Exception(errorMessage);
            } else if (converterSetup.isLocation()) {
                if (converterSetup.getGeoLocationConfig() == null || converterSetup.getGeoLocationConfig().size() == 0) {
                    errorMessage = "ImportNGSILDDataSetup - MatchingConverterSetup " + key + " - GeoLocation Config is required";
                    log.error(errorMessage);
                    throw new Exception(errorMessage);
                }
                if (!GEO_PROPERTY_TYPE.contains(key) || !GEO_PROPERTY_TYPE.contains(converterSetup.getFinalProperty())) {
                    errorMessage = "ImportNGSILDDataSetup - MatchingConverterSetup " + key +
                            " - GeoLocation Config must be: \"location\", \"observationSpace\", \"operationSpace\"";
                    log.error(errorMessage);
                    throw new Exception(errorMessage);
                }
            }
        }
    }


}
