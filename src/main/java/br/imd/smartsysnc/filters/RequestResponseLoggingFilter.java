package br.imd.smartsysnc.filters;

import static br.imd.smartsysnc.utils.RequestsUtils.APP_TOKEN;
import static br.imd.smartsysnc.utils.RequestsUtils.USER_TOKEN;

import java.io.IOException;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.annotation.Order;

import com.fasterxml.jackson.databind.ObjectMapper;

import br.imd.smartsysnc.models.response.Response;
import br.imd.smartsysnc.security.PermissionChecker;

@Order(1)
public class RequestResponseLoggingFilter implements Filter {

	private final static Logger LOG = LoggerFactory.getLogger(RequestResponseLoggingFilter.class);

	@Override
	public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
			throws IOException, ServletException {

		PermissionChecker permissionChecker = new PermissionChecker();

		HttpServletRequest req = (HttpServletRequest) request;
		HttpServletResponse res = (HttpServletResponse) response;
		LOG.info("Logging Request  {} : {}", req.getHeader(APP_TOKEN), req.getHeader(USER_TOKEN));
		try {
			if (!permissionChecker.checkSmartSyncPerssisionAccess(req.getHeader(USER_TOKEN))) {
				buildResponseError(res, "You don't have permission to access Smart Sync API");
				return;
			}
			chain.doFilter(request, response);
//			LOG.info("Logging Response :{}", res.getContentType());
		} catch (Exception e) {
			buildResponseError(response, "Internal error.");
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
