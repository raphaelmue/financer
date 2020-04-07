package org.financer.server.application.configuration;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.financer.server.application.api.error.RestErrorMessage;
import org.financer.server.domain.model.user.UserEntity;
import org.financer.server.domain.service.UserDomainService;
import org.financer.shared.domain.model.value.objects.TokenString;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
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
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Component
public class AuthenticationTokenFilter extends OncePerRequestFilter {

    private static final String TOKEN_PREFIX = "Bearer ";
    public static final String HEADER_STRING = "Authorization";

    public static final String REGISTERED_USER_ROLE = "REGISTERED_USER";

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private MessageSource messageSource;

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

        String tokenString = "";
        if (header != null && header.startsWith(TOKEN_PREFIX)) {
            tokenString = header.substring(7);

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
        }
        sendErrorResponse(request, response, tokenString);
    }

    private void sendErrorResponse(HttpServletRequest request, HttpServletResponse response, String tokenString) {
        String errorMessage = messageSource.getMessage("exception.unauthorizedToken", new String[]{tokenString}, request.getLocale());
        logger.error(String.format("Token (%s) is invalid", tokenString));

        response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
        response.setContentType("application/json");
        try (PrintWriter writer = response.getWriter()) {
            writer.write(objectMapper.writeValueAsString(new RestErrorMessage(HttpStatus.FORBIDDEN, request.getRequestURI(), errorMessage)));
        } catch (IOException exception) {
            logger.error(exception.getMessage(), exception);
        }
    }
}
