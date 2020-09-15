package br.imd.aqueducte.security.filters;

import br.imd.aqueducte.models.mongodocuments.external_app_config.ExternalAppConfig;
import br.imd.aqueducte.models.response.Response;
import br.imd.aqueducte.services.ExternalAppConfigService;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.log4j.Log4j2;
import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpRequestBase;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.Map;

import static br.imd.aqueducte.utils.RequestsUtils.HASH_CONFIG;
import static br.imd.aqueducte.utils.RequestsUtils.getHttpClientInstance;

@Order(1)
@Component
@Log4j2
public class RequestResponseLoggingFilter implements Filter {

    @Autowired
    private ExternalAppConfigService externalAppConfigService;

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
            throws IOException {

        HttpServletRequest req = (HttpServletRequest) request;
        HttpServletResponse res = (HttpServletResponse) response;
        try {
            String path = req.getRequestURI();
            log.info(path);
            if (path.contains("/sync/external-app-config") || path.contains("aqueducte-socket")) {
                chain.doFilter(request, response);
                return;
            }

            String hashConfig = req.getHeader(HASH_CONFIG);
            if (hashConfig == null || hashConfig == "") {
                buildResponseError(res, "'hash-config' header is required");
                return;
            }

            ExternalAppConfig externalAppConfig = this.externalAppConfigService.getConfigByHash(hashConfig);
            if (externalAppConfig.getAuthServiceConfig() != null) {
                Map<String, String> headers = new LinkedHashMap<>();
                for (Iterator<String> it = req.getHeaderNames().asIterator(); it.hasNext(); ) {
                    String header = it.next();
                    headers.put(header, req.getHeader(header));
                }
                HttpRequestBase authRequest = this.externalAppConfigService.mountExternalAppConfigService(
                        externalAppConfig.getAuthServiceConfig(), null, headers
                );
                HttpResponse authResponse = getHttpClientInstance().execute(authRequest);

                int statusCode = authResponse.getStatusLine().getStatusCode();
                log.info("Logging ProtocolVersion: {}", authResponse.getProtocolVersion());
                log.info("Logging Status Code: {}", statusCode);

                if (statusCode != externalAppConfig.getAuthServiceConfig().getReturnStatusCode()) {
                    buildResponseError(res, "You don't have permission to access Aqüeducte API");
                    return;
                }
            }
            chain.doFilter(request, response);
        } catch (Exception e) {
            buildResponseError(res, "Internal error.");
            log.error(e.getMessage(), e.getStackTrace());
        }
    }

    private void buildResponseError(ServletResponse response, String message) throws IOException {
        Response<String> errorResponse = new Response<String>();
        errorResponse.getErrors().add(message);

        byte[] responseToSend = restResponseBytes(errorResponse);
        ((HttpServletResponse) response).setHeader("Content-Type", "application/json");
        ((HttpServletResponse) response).setStatus(401);
        log.error(message);
        response.getOutputStream().write(responseToSend);
    }

    private byte[] restResponseBytes(Response<String> errorResponse) throws IOException {
        String serialized = new ObjectMapper().writeValueAsString(errorResponse);
        return serialized.getBytes();
    }
}
