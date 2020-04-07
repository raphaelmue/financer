package org.financer.server.application.configuration;

import org.financer.server.domain.model.user.UserEntity;
import org.financer.server.domain.service.UserDomainService;
import org.financer.shared.domain.model.value.objects.TokenString;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Component
public class AuthenticationTokenFilter extends OncePerRequestFilter {

    private static final String TOKEN_PREFIX = "Bearer ";
    public static final String HEADER_STRING = "Authorization";

    public static final String REGISTERED_USER_ROLE = "REGISTERED_USER";

    @Autowired
    private UserDomainService userDomainService;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain chain) throws IOException, ServletException {
        if (request.getRequestURI().endsWith("/users") && (HttpMethod.GET.matches(request.getMethod()) ||
                HttpMethod.PUT.matches(request.getMethod()))) {
            chain.doFilter(request, response);
            return;
        }

        final String header = request.getHeader(HEADER_STRING);

        if (header != null && header.startsWith(TOKEN_PREFIX)) {
            String tokenString = header.substring(7);

            try {
                Optional<UserEntity> userOptional = userDomainService.checkUsersToken(new TokenString(tokenString));
                if (userOptional.isPresent() && SecurityContextHolder.getContext().getAuthentication() == null) {
                    List<GrantedAuthority> authList = new ArrayList<>();
                    authList.add(new SimpleGrantedAuthority(REGISTERED_USER_ROLE));

                    UsernamePasswordAuthenticationToken usernamePasswordAuthenticationToken =
                            new UsernamePasswordAuthenticationToken(userOptional.get(), null, authList);
                    usernamePasswordAuthenticationToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));

                    SecurityContextHolder.getContext().setAuthentication(usernamePasswordAuthenticationToken);
                    chain.doFilter(request, response);
                    return;
                }
            } catch (Exception e) {
                logger.error("Unable to get JWT Token, possibly expired", e);
            }
        } else {
            logger.info("Failed to authenticate: Token is invalid.");
        }
        response.sendError(HttpServletResponse.SC_FORBIDDEN, "Failed to authenticate: Token is invalid.");
    }
}