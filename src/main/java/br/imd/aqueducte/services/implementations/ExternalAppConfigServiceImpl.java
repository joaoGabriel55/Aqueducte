package br.imd.aqueducte.services.implementations;

import br.imd.aqueducte.models.mongodocuments.external_app_config.ExternalAppConfig;
import br.imd.aqueducte.models.mongodocuments.external_app_config.ServiceConfig;
import br.imd.aqueducte.repositories.ExternalAppConfigRepository;
import br.imd.aqueducte.services.ExternalAppConfigService;
import br.imd.aqueducte.services.validators.ExternalAppConfigValidator;
import com.google.common.hash.Hashing;
import lombok.extern.log4j.Log4j2;
import org.apache.http.client.methods.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.nio.charset.StandardCharsets;
import java.util.*;

@Service
@Log4j2
public class ExternalAppConfigServiceImpl implements ExternalAppConfigService {

    @Autowired
    private ExternalAppConfigRepository repository;

    @Autowired
    private ExternalAppConfigValidator validator;

    @Override
    public ExternalAppConfig getConfigByHash(String hash) throws Exception {
        try {
            ExternalAppConfig config = this.repository.findByHashConfig(hash);
            log.info("findByHashConfig ExternalAppConfig");
            return config;
        } catch (Exception e) {
            log.error(e.getMessage(), e.getStackTrace());
            throw new Exception(e.getMessage());
        }
    }

    @Override
    public ExternalAppConfig createOrUpdate(ExternalAppConfig config) throws Exception {
        try {
            this.validator.validExternalAppConfig(config);

            Date now = new Date();
            if (config.getId() == null) {
                String charSequence = config.getApplicationName() + now.getTime();
                String hashConfig = Hashing.sha256()
                        .hashString(charSequence, StandardCharsets.UTF_8)
                        .toString();
                config.setHashConfig(hashConfig);
                config.setCreatedDate(now);
            } else {
                config.setCreatedDate(config.getCreatedDate());
            }
            config.setUpdatedDate(now);
            log.info("createOrUpdate ExternalAppConfig");
            return this.repository.save(config);
        } catch (Exception e) {
            log.error(e.getMessage(), e.getStackTrace());
            throw new Exception(e.getMessage());
        }
    }


    private String getFinalUrl(Map<String, String> queryParams, String url) {
        String finalUrl = url;
        if (queryParams != null && queryParams.size() > 0) {
            queryParams.entrySet().forEach((query) -> {
                finalUrl.replace("{" + query + "}", queryParams.get(query));
            });
        }
        return finalUrl;
    }

    @Override
    public HttpRequestBase mountExternalAppConfigService(
            ServiceConfig serviceConfig, Map<String, String> queryParams, Map<String, String> requestHeaders
    ) {
        HttpRequestBase request = null;
        String url = getFinalUrl(queryParams, serviceConfig.getUrl());
        if (serviceConfig.getHttpVerb().toUpperCase().equals("GET")) {
            request = new HttpGet(url);
        } else if (serviceConfig.getHttpVerb().toUpperCase().equals("POST")) {
            request = new HttpPost(url);
        } else if (serviceConfig.getHttpVerb().toUpperCase().equals("PATCH")) {
            request = new HttpPatch(url);
        } else if (serviceConfig.getHttpVerb().toUpperCase().equals("PUT")) {
            request = new HttpPut(url);
        }

        request.addHeader("Content-Type", serviceConfig.getContentType());
        if (serviceConfig.getHeaders() != null) {
            Set<String> headers = serviceConfig.getHeaders();
            for (Map.Entry<String, String> requestHeader : requestHeaders.entrySet()) {
                if (headers.contains(requestHeader.getKey()))
                    request.addHeader(requestHeader.getKey(), requestHeader.getValue());
            }
        }
        return request;
    }

    @Override
    public void deleteByHash(String hash) throws Exception {
        try {
            ExternalAppConfig config = getConfigByHash(hash);
            if (config == null) {
                log.error("ExternalAppConfig not found - {}", hash);
                throw new Exception();
            }
            String idForDelete = config.getId();
            this.repository.deleteById(idForDelete);
            log.info("deleteByHash ExternalAppConfig - {}", hash);
        } catch (Exception e) {
            log.error(e.getMessage(), e.getStackTrace());
            throw new Exception();
        }
    }

    @Override
    public List<ExternalAppConfig> findAll() throws Exception {
        return null;
    }

    @Override
    public Optional<ExternalAppConfig> findById(String id) throws Exception {
        return Optional.empty();
    }

    @Override
    public String delete(String id) throws Exception {
        return null;
    }
}
