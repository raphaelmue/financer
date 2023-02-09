package org.financer.server.application.configuration.security;

import org.financer.server.domain.model.user.Role;
import org.financer.shared.domain.model.value.objects.HashedPassword;
import org.financer.shared.path.PathBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration
@EnableWebSecurity
@EnableGlobalMethodSecurity(prePostEnabled = true)
public class WebSecurityConfiguration {

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http.cors().and().csrf().disable().authorizeRequests()
                .requestMatchers(PathBuilder.Get().apiDocumentation().any().build().getPath()).permitAll()                                  // OpenAPI Documentation
                .requestMatchers(PathBuilder.Get().apiDocumentationUI().any().build().getPath()).permitAll()                                // SpringDoc UI
                .requestMatchers(HttpMethod.GET, PathBuilder.Get().users().build().getPath()).permitAll()                                   // login
                .requestMatchers(HttpMethod.PUT, PathBuilder.Get().users().build().getPath()).permitAll()                                   // register
                .requestMatchers(HttpMethod.GET, PathBuilder.Get().users().userId().verificationToken().build().getPath()).permitAll()      // verify users email
                .requestMatchers(PathBuilder.Get().admin().any().build().getPath()).hasAuthority(Role.ROLE_ADMIN)
                .requestMatchers(HttpMethod.GET, PathBuilder.Get().admin().configuration().build().getPath()).hasAuthority(Role.ROLE_USER)
                .anyRequest().hasAuthority(Role.ROLE_USER).and()
                .addFilterBefore(authenticationTokenFilter(), UsernamePasswordAuthenticationFilter.class);

        http.sessionManagement().sessionCreationPolicy(SessionCreationPolicy.STATELESS);

        return http.build();
    }

    @Bean
    public AuthenticationTokenFilter authenticationTokenFilter() {
        return new AuthenticationTokenFilter();
    }

    @Bean
    public UserDetailsService userDetailsService() {
        return new UserDetailsServiceImpl();
    }

    @Bean
    public static PasswordEncoder passwordEncoder() {
        return new PasswordEncoder() {
            @Override
            public String encode(CharSequence rawPassword) {
                return new HashedPassword(rawPassword.toString()).getHashedPassword();
            }

            @Override
            public boolean matches(CharSequence rawPassword, String encodedPassword) {
                return new HashedPassword(encodedPassword).isEqualTo(rawPassword.toString());
            }
        };
    }

    @Bean
    public DaoAuthenticationProvider authenticationProvider() {
        DaoAuthenticationProvider authProvider = new DaoAuthenticationProvider();
        authProvider.setUserDetailsService(userDetailsService());
        authProvider.setPasswordEncoder(passwordEncoder());

        return authProvider;
    }
}