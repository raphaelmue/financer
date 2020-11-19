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
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration
@EnableWebSecurity
@EnableGlobalMethodSecurity(prePostEnabled = true)
public class WebSecurityConfiguration extends WebSecurityConfigurerAdapter {

    @Override
    protected void configure(HttpSecurity http) throws Exception {
        http.csrf().disable().authorizeRequests()
                .antMatchers(PathBuilder.Get().apiDocumentation().any().build().getPath()).permitAll()      // OpenAPI Documentation
                .antMatchers(PathBuilder.Get().apiDocumentationUI().any().build().getPath()).permitAll()    // SpringDoc UI
                .antMatchers(HttpMethod.GET, PathBuilder.Get().users().build().getPath()).permitAll()       // login
                .antMatchers(HttpMethod.PUT, PathBuilder.Get().users().build().getPath()).permitAll()      // register
                .anyRequest().hasAuthority(Role.ROLE_USER).and()
                .addFilterBefore(authenticationTokenFilter(), UsernamePasswordAuthenticationFilter.class);

        http.sessionManagement().sessionCreationPolicy(SessionCreationPolicy.STATELESS);
    }

    @Bean
    public AuthenticationTokenFilter authenticationTokenFilter() {
        return new AuthenticationTokenFilter();
    }

    @Bean
    @Override
    public UserDetailsService userDetailsService() {
        return new UserDetailsServiceImpl();
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
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