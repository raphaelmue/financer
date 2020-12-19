package org.financer.server.application.configuration.security;

import org.financer.server.application.service.AdminConfigurationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

import javax.servlet.*;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

@Component
@Order(Ordered.HIGHEST_PRECEDENCE)
public class SimpleCORSFilter implements Filter {

    private static final String ALLOW_ALL_HOSTS = "ALLOW_ALL";

    @Autowired
    private AdminConfigurationService adminConfigurationService;

    @Override
    public void doFilter(ServletRequest req, ServletResponse res, FilterChain chain) throws IOException, ServletException {

        HttpServletRequest request = (HttpServletRequest) req;
        HttpServletResponse response = (HttpServletResponse) res;

        String host = adminConfigurationService.getClientHost().equals(ALLOW_ALL_HOSTS) ?
                request.getHeader("Origin")
                : adminConfigurationService.getClientHost();

        response.setHeader("Access-Control-Allow-Origin", host);
        response.setHeader("Access-Control-Allow-Credentials", "true");
        response.setHeader("Access-Control-Allow-Methods", "POST, GET, DELETE, PUT");
        response.setHeader("Access-Control-Max-Age", "3600");
        response.setHeader("Access-Control-Allow-Headers", "Authorization, Accept-Language, Content-Type, Accept, Access-Control-Allow-Headers, X-Requested-With");

        chain.doFilter(req, res);
    }

    @Override
    public void init(FilterConfig filterConfig) {
    }

    @Override
    public void destroy() {
    }

}