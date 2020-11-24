package org.financer.server.application.api;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.financer.server.application.FinancerServer;
import org.financer.server.application.configuration.MigrationConfiguration;
import org.financer.server.application.configuration.PersistenceConfiguration;
import org.financer.server.application.configuration.security.AuthenticationTokenFilter;
import org.financer.server.application.configuration.security.WebSecurityConfiguration;
import org.financer.server.application.service.AdminConfigurationService;
import org.financer.server.domain.service.CategoryDomainService;
import org.financer.server.domain.service.TransactionDomainService;
import org.financer.server.domain.service.UserDomainService;
import org.financer.server.utils.ServiceTest;
import org.financer.shared.domain.model.api.DataTransferObject;
import org.financer.shared.domain.model.value.objects.TokenString;
import org.financer.shared.path.Path;
import org.junit.jupiter.api.BeforeEach;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureWebMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import java.util.Optional;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@SpringBootTest(
        classes = {FinancerServer.class, PersistenceConfiguration.class, MigrationConfiguration.class,
                WebSecurityConfiguration.class, RestExceptionHandler.class},
        webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureWebMvc
@AutoConfigureMockMvc
public abstract class ApiTest extends ServiceTest {

    @Autowired
    protected ObjectMapper objectMapper;

    @Autowired
    protected AdminConfigurationService adminConfigurationService;

    @Autowired
    protected MockMvc mockMvc;

    @MockBean
    protected UserDomainService userDomainService;

    @MockBean
    protected TransactionDomainService transactionDomainService;

    @MockBean
    protected CategoryDomainService categoryDomainService;

    @BeforeEach
    public void setUp() {
        when(userDomainService.checkUsersToken(any(TokenString.class))).thenReturn(Optional.of(user()));
        when(authenticationService.getAuthenticatedUser()).thenReturn(user());
    }

    protected MockHttpServletRequestBuilder buildRequest(Path path) {
        return buildRequestWithoutAuthorization(path)
                .header(AuthenticationTokenFilter.HEADER_STRING, AuthenticationTokenFilter.TOKEN_PREFIX + tokenString().getToken());
    }

    protected MockHttpServletRequestBuilder buildRequest(Path path, DataTransferObject dto) throws JsonProcessingException {
        return buildRequestWithoutAuthorization(path)
                .header(AuthenticationTokenFilter.HEADER_STRING, AuthenticationTokenFilter.TOKEN_PREFIX + tokenString().getToken())
                .contentType(MediaType.APPLICATION_JSON).characterEncoding("UTF-8").content(objectMapper.writeValueAsString(dto));
    }

    protected MockHttpServletRequestBuilder buildRequestWithoutAuthorization(Path path, DataTransferObject dto) throws JsonProcessingException {
        return buildRequestWithoutAuthorization(path)
                .contentType(MediaType.APPLICATION_JSON).characterEncoding("UTF-8").content(objectMapper.writeValueAsString(dto));
    }

    protected MockHttpServletRequestBuilder buildRequestWithoutAuthorization(Path path) {
        return MockMvcRequestBuilders.request(HttpMethod.valueOf(path.getMethod()), path.getPath())
                .servletPath(path.getPath())
                .accept(MediaType.APPLICATION_JSON)
                .header("Origin", "http://localhost:3000");
    }
}
