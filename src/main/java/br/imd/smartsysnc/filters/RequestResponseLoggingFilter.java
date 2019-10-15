package br.imd.smartsysnc.filters;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.annotation.Order;

import javax.servlet.*;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

@Order(1)
public class RequestResponseLoggingFilter implements Filter {
    private final static Logger LOG = LoggerFactory.getLogger(RequestResponseLoggingFilter.class);
    private final static String APP_TOKEN = "application-token";
    private final static String USER_TOKEN = "user-token";

    @Override
    public void doFilter(
            ServletRequest request,
            ServletResponse response,
            FilterChain chain) throws IOException, ServletException {

        HttpServletRequest req = (HttpServletRequest) request;
        HttpServletResponse res = (HttpServletResponse) response;
        LOG.info(
                "Logging Request  {} : {}", req.getHeader(APP_TOKEN),
                req.getHeader(USER_TOKEN));
        chain.doFilter(request, response);
        LOG.info(
                "Logging Response :{}",
                res.getContentType());
    }
}
