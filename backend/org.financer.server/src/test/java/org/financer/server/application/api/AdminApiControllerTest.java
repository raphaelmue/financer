package org.financer.server.application.api;

import com.fasterxml.jackson.core.type.TypeReference;
import org.financer.server.application.FinancerServer;
import org.financer.server.application.configuration.security.WebSecurityConfiguration;
import org.financer.server.application.service.AdminConfigurationService;
import org.financer.server.domain.model.user.User;
import org.financer.shared.domain.model.api.DataTransferObject;
import org.financer.shared.domain.model.api.admin.AdminConfigurationDTO;
import org.financer.shared.domain.model.api.admin.InitAdminConfigurationDTO;
import org.financer.shared.domain.model.api.admin.UpdateAdminConfigurationDTO;
import org.financer.shared.domain.model.api.user.RegisterUserDTO;
import org.financer.shared.domain.model.api.user.UserDTO;
import org.financer.shared.path.PathBuilder;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureWebMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.hateoas.PagedModel;
import org.springframework.test.web.servlet.MvcResult;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@Tag("unit")
@SpringBootTest(classes = {FinancerServer.class, AdminConfigurationService.class, WebSecurityConfiguration.class, RestExceptionHandler.class, AdminApiController.class},
        webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureWebMvc
@AutoConfigureMockMvc
public class AdminApiControllerTest extends ApiTest {

    @BeforeEach
    public void setUp() {
        super.setUp();
        adminConfigurationService.resetProperties();
    }

    @Test
    public void testGetAdminConfiguration() throws Exception {
        MvcResult result = mockMvc.perform(buildRequest(PathBuilder.Get().admin().configuration().build()))
                .andExpect(status().isOk()).andReturn();

        AdminConfigurationDTO configuration = objectMapper.readValue(result.getResponse().getContentAsString(), AdminConfigurationDTO.class);
        assertThat(configuration.getDefaultLanguage()).isEqualTo("en");
        assertThat(configuration.getDefaultCurrency()).isEqualTo("USD");
    }

    @Test
    public void testUpdateAdminConfiguration() throws Exception {
        DataTransferObject dto = new UpdateAdminConfigurationDTO()
                .setDefaultLanguage("de")
                .setDefaultCurrency("EUR");

        MvcResult result = mockMvc.perform(buildRequest(PathBuilder.Post().admin().configuration().build(), dto))
                .andExpect(status().isOk()).andReturn();

        AdminConfigurationDTO configuration = objectMapper.readValue(result.getResponse().getContentAsString(), AdminConfigurationDTO.class);
        assertThat(configuration.getDefaultLanguage()).isEqualTo("de");
        assertThat(configuration.getDefaultCurrency()).isEqualTo("EUR");
    }

    @Test
    public void testInitializeServerConfiguration() throws Exception {
        when(userDomainService.registerUser(any(User.class), any(), any())).thenReturn(user().setRoles(roles()));

        DataTransferObject dto = new InitAdminConfigurationDTO()
                .setAdminUser(new RegisterUserDTO()
                        .setName(user().getName())
                        .setEmail(user().getEmail())
                        .setBirthDate(user().getBirthDate())
                        .setGender(user().getGender())
                        .setPassword(user().getPassword()))
                .setDefaultLanguage("de")
                .setDefaultCurrency("EUR");

        MvcResult result = mockMvc.perform(buildRequest(PathBuilder.Put().admin().configuration().build(), dto))
                .andExpect(status().isOk()).andReturn();

        AdminConfigurationDTO configuration = objectMapper.readValue(result.getResponse().getContentAsString(), AdminConfigurationDTO.class);
        assertThat(configuration.getDefaultLanguage()).isEqualTo("de");
        assertThat(configuration.getDefaultCurrency()).isEqualTo("EUR");

        verify(userDomainService, times(1)).registerUser(any(), any(), any());
    }

    @Test
    public void testGetUsers() throws Exception {
        when(userDomainService.fetchUsers(any(Pageable.class))).thenReturn(new PageImpl<>(List.of(user())));

        MvcResult result = mockMvc.perform(buildRequest(PathBuilder.Get().admin().users().build()))
                .andExpect(status().isOk()).andReturn();

        PagedModel<UserDTO> users = objectMapper.readValue(result.getResponse().getContentAsString(),
                new TypeReference<>() {
                });
        assertThat(users.getMetadata()).isNotNull();
        assertThat(users.getMetadata().getTotalElements()).isEqualTo(1);
    }
}
