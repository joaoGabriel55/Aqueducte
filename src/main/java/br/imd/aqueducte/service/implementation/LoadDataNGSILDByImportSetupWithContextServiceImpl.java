package br.imd.aqueducte.service.implementation;

import br.imd.aqueducte.models.mongodocuments.ImportationSetupWithContext;
import br.imd.aqueducte.models.dtos.DataSetRelationship;
import br.imd.aqueducte.service.LoadDataNGSILDByImportationSetupService;
import br.imd.aqueducte.treats.JsonFlatConvertTreat;
import br.imd.aqueducte.treats.NGSILDTreat;
import br.imd.aqueducte.treats.impl.NGSILDTreatImpl;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.HttpClientBuilder;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import static br.imd.aqueducte.utils.PropertiesParams.STATUS_OK;
import static br.imd.aqueducte.utils.PropertiesParams.URL_AQUECONNECT;

@SuppressWarnings("ALL")
@Service
public class LoadDataNGSILDByImportSetupWithContextServiceImpl
        extends LoadDataNGSILDByImportSetup
        implements LoadDataNGSILDByImportationSetupService<ImportationSetupWithContext> {

    public LoadDataNGSILDByImportSetupWithContextServiceImpl() {
        super();
    }

    @Override
    public List<LinkedHashMap<String, Object>> loadData(ImportationSetupWithContext importationSetupWithContext) {
        JsonFlatConvertTreat jsonFlatConvertTreat = new JsonFlatConvertTreat();

        // Load data from Webservice
        Map<String, Object> responseWSResult = loadDataWebservice(importationSetupWithContext);
        Map<String, Object> responseWSResultFlat = (Map<String, Object>) jsonFlatConvertTreat.getJsonFlat(responseWSResult);
        // Get data chosen
        Object dataFound = findDataRecursive(responseWSResultFlat, importationSetupWithContext.getDataSelected());
        if (dataFound instanceof List) {
            // Flat Json collection
            List<Object> dataCollectionFlat = (List<Object>) jsonFlatConvertTreat.getJsonFlat(dataFound);
            List<LinkedHashMap<String, Object>> dataForConvert = filterFieldsSelectedIntoArray(
                    dataCollectionFlat,
                    importationSetupWithContext
            );

            // Convert o NGSI-LD
            NGSILDTreat ngsildTreat = new NGSILDTreatImpl();
            try {
                List<LinkedHashMap<String, Object>> listConvertedIntoNGSILD = ngsildTreat.matchingWithContextAndConvertToEntityNGSILD(
                        importationSetupWithContext.getContextFileLink(),
                        importationSetupWithContext.getMatchingConfigList(),
                        dataForConvert,
                        importationSetupWithContext.getLayerSelected()
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
        HttpClient httpClient = HttpClientBuilder.create().build();
        HttpPost request = new HttpPost(URL_AQUECONNECT + "relationships");

        // TODO: Auth - Parsing the "user-token" for Aqueconnect microservice

        request.setEntity(getDataSetRelationshipJson(dataSetRelationship));
        int statusCode = 0;
        try {
            HttpResponse response = httpClient.execute(request);
            statusCode = response.getStatusLine().getStatusCode();
            if (response.getStatusLine().getStatusCode() != STATUS_OK) {
                return statusCode;
            }
        } catch (IOException e) {
            e.printStackTrace();
            return 400;
        }
        return statusCode;
    }
}
