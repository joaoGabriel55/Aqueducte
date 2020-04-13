package br.imd.aqueducte.security.filters;

import br.imd.aqueducte.models.response.Response;
import br.imd.aqueducte.security.PermissionChecker;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.core.annotation.Order;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

import static br.imd.aqueducte.logger.LoggerMessage.logError;
import static br.imd.aqueducte.config.PropertiesParams.AUTH;
import static br.imd.aqueducte.utils.RequestsUtils.USER_TOKEN;

@Order(1)
public class RequestResponseLoggingFilter implements Filter {

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
            throws IOException {

        PermissionChecker permissionChecker = new PermissionChecker();

        HttpServletRequest req = (HttpServletRequest) request;
        HttpServletResponse res = (HttpServletResponse) response;
        try {
            if (AUTH) {
                if (!permissionChecker.checkSmartSyncPermissionAccess(req.getHeader(USER_TOKEN), (HttpServletRequest) request)) {
                    buildResponseError(res, "You don't have permission to access Aqüeducte API");
                    return;
                }
            }
            chain.doFilter(request, response);
        } catch (Exception e) {
            buildResponseError(response, "Internal error.");
            logError(e.getMessage(), e.getStackTrace());
            return;
        }
    }

    private void buildResponseError(ServletResponse response, String message) throws IOException {
        Response<String> errorResponse = new Response<String>();
        errorResponse.getErrors().add(message);

        byte[] responseToSend = restResponseBytes(errorResponse);
        ((HttpServletResponse) response).setHeader("Content-Type", "application/json");
        ((HttpServletResponse) response).setStatus(401);
        response.getOutputStream().write(responseToSend);
    }

    private byte[] restResponseBytes(Response<String> errorResponse) throws IOException {
        String serialized = new ObjectMapper().writeValueAsString(errorResponse);
        return serialized.getBytes();
    }
}
