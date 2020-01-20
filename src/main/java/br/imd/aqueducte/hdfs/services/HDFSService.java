package br.imd.aqueducte.hdfs.services;

import com.google.gson.Gson;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.ContentType;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.HttpClientBuilder;

import java.io.IOException;
import java.util.LinkedHashMap;
import java.util.List;

import static br.imd.aqueducte.utils.PropertiesParams.URL_AQUECONNECT;

public class HDFSService {

    public int sendDataHDFS(String userId, String importSetupName, String layer, List<LinkedHashMap<String, Object>> dataLoaded) {
        String urlPath = URL_AQUECONNECT + "hdfs-data-file/" + userId + "/" + importSetupName + "/" + layer + ".json";
        HttpClient httpClient = HttpClientBuilder.create().build();
        HttpPost request = new HttpPost(urlPath);

        String json = new Gson().toJson(dataLoaded);
        StringEntity entity = new StringEntity(json, ContentType.APPLICATION_JSON);

        request.setEntity(entity);

        int statusCode = 0;
        try {
            HttpResponse response = httpClient.execute(request);
            statusCode = response.getStatusLine().getStatusCode();
        } catch (IOException e) {
            e.printStackTrace();
        }

        return statusCode;
    }
}
