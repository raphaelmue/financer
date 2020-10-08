package org.financer.server.application.configuration;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.financer.server.application.api.error.RestErrorMessage;
import org.financer.server.domain.model.user.User;
import org.financer.server.domain.service.UserDomainService;
import org.financer.shared.domain.model.value.objects.TokenString;
import org.financer.shared.path.Path;
import org.financer.shared.path.PathBuilder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.util.AntPathMatcher;
import org.springframework.web.filter.OncePerRequestFilter;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

public class AuthenticationTokenFilter extends OncePerRequestFilter {

    public static final String TOKEN_PREFIX = "Bearer ";
    public static final String HEADER_STRING = "Authorization";

    public static final String REGISTERED_USER_ROLE = "REGISTERED_USER";

    private static final List<Path> paths = Arrays.asList(
            PathBuilder.Get().users().build(),
            PathBuilder.Put().users().build(),
            PathBuilder.Get().users().userId().verificationToken().build(),
            PathBuilder.Get().apiDocumentation().any().build(),
            PathBuilder.Get().apiDocumentationUI().any().build());

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private MessageSource messageSource;

    @Autowired
    private UserDomainService userDomainService;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain chain) throws IOException, ServletException {
        final String header = request.getHeader(HEADER_STRING);

        if (!request.getMethod().equals(HttpMethod.OPTIONS.toString())) {
            String tokenString = "";
            if (header != null && header.startsWith(TOKEN_PREFIX)) {
                tokenString = header.substring(7);

                Optional<User> userOptional = userDomainService.checkUsersToken(new TokenString(tokenString));
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
    }

    @Override
    protected boolean shouldNotFilter(HttpServletRequest request) {
        return paths.stream().anyMatch(path ->
                request.getMethod().equals(path.getMethod())
                        && new AntPathMatcher().match(path.getPath(), request.getServletPath()));
    }

    private void sendErrorResponse(HttpServletRequest request, HttpServletResponse response, String tokenString) {
        String errorMessage = messageSource.getMessage("exception.unauthorizedToken", new String[]{tokenString}, request.getLocale());
        logger.error(String.format("Token (%s) is invalid", tokenString));

        response.setStatus(HttpServletResponse.SC_FORBIDDEN);
        response.setContentType("application/json");
        try (PrintWriter writer = response.getWriter()) {
            writer.write(objectMapper.writeValueAsString(new RestErrorMessage(HttpStatus.FORBIDDEN, request.getRequestURI(), errorMessage)));
        } catch (IOException exception) {
            logger.error(exception.getMessage(), exception);
        }
    }
}
