package org.financer.server.application.api;

import com.fasterxml.jackson.core.type.TypeReference;
import org.financer.server.application.FinancerServer;
import org.financer.server.application.configuration.WebSecurityConfiguration;
import org.financer.server.domain.model.user.Setting;
import org.financer.server.domain.model.user.User;
import org.financer.shared.domain.model.api.DataTransferObject;
import org.financer.shared.domain.model.api.category.CategoryDTO;
import org.financer.shared.domain.model.api.transaction.fixed.FixedTransactionDTO;
import org.financer.shared.domain.model.api.transaction.variable.VariableTransactionDTO;
import org.financer.shared.domain.model.api.user.RegisterUserDTO;
import org.financer.shared.domain.model.api.user.UpdatePersonalInformationDTO;
import org.financer.shared.domain.model.api.user.UpdateSettingsDTO;
import org.financer.shared.domain.model.api.user.UserDTO;
import org.financer.shared.domain.model.value.objects.*;
import org.financer.shared.path.PathBuilder;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureWebMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.web.servlet.MvcResult;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@Tag("unit")
@SpringBootTest(classes = {FinancerServer.class, WebSecurityConfiguration.class, UserApiController.class},
        webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureWebMvc
@AutoConfigureMockMvc
public class UserApiControllerTest extends ApiTest {

    private User user;

    @BeforeEach
    public void setUp() {
        super.setUp();
        user = user();
    }

    @Test
    public void testLoginUser() throws Exception {
        when(userDomainService.checkCredentials(eq(user.getEmail().getEmailAddress()), eq(password()),
                any(IPAddress.class), any())).thenReturn(Optional.of(user));

        mockMvc.perform(buildRequestWithoutAuthorization(PathBuilder.Get().users().build())
                .param("email", user.getEmail().getEmailAddress())
                .param("password", password()))
                .andExpect(status().isOk());
    }

    @Test
    public void testRegisterUser() throws Exception {
        when(userDomainService.registerUser(any(User.class), any(IPAddress.class), any()))
                .thenAnswer(i -> ((User) i.getArguments()[0]).setId(1));

        DataTransferObject dto = new RegisterUserDTO()
                .setName(user.getName())
                .setEmail(user.getEmail())
                .setBirthDate(user.getBirthDate())
                .setGender(user.getGender())
                .setPassword(user.getPassword());

        MvcResult result = mockMvc.perform(buildRequestWithoutAuthorization(PathBuilder.Put().users().build(), dto)
                .param("email", user.getEmail().getEmailAddress())
                .param("password", password()))
                .andExpect(status().isOk()).andReturn();

        UserDTO userToAssert = objectMapper.readValue(result.getResponse().getContentAsString(), UserDTO.class);
        assertThat(userToAssert.getId()).isEqualTo(1);
        assertThat(userToAssert.getEmail()).isEqualTo(user.getEmail());
    }

    @Test
    public void testDeleteToken() throws Exception {
        mockMvc.perform(buildRequest(PathBuilder.Delete().users().userId(1).tokens().tokenId(1).build()))
                .andExpect(status().isOk());
    }

    @Test
    public void testUpdateUsersPassword() throws Exception {
        when(userDomainService.updatePassword(any(HashedPassword.class))).thenReturn(user);

        DataTransferObject dto = new HashedPassword("newPassword");
        mockMvc.perform(buildRequest(PathBuilder.Post().users().userId(1).password().build(), dto))
                .andExpect(status().isOk());
    }

    @Test
    @SuppressWarnings("unchecked")
    public void testUpdateUsersSettings() throws Exception {
        when(userDomainService.updateUsersSettings(any())).thenAnswer(i -> user.setSettings((Map<SettingPair.Property, Setting>) i.getArguments()[0]));

        DataTransferObject dto = new UpdateSettingsDTO().setSettings(Map.of(SettingPair.Property.CURRENCY, "EUR"));
        MvcResult result = mockMvc.perform(buildRequest(PathBuilder.Post().users().userId(1).settings().build(), dto))
                .andExpect(status().isOk()).andReturn();

        UserDTO user = objectMapper.readValue(result.getResponse().getContentAsString(), UserDTO.class);
        assertThat(user.getSettings().containsKey(SettingPair.Property.CURRENCY)).isTrue();
//        assertThat(user.getSettings().get(SettingPair.Property.CURRENCY).getPairValue()).isEqualTo("EUR");
    }

    @Test
    public void testUpdateUsersPersonalInformation() throws Exception {
        when(userDomainService.updatePersonalInformation(any(Name.class), any(BirthDate.class), any(Gender.class)))
                .thenAnswer(i -> user.setName((Name) i.getArguments()[0])
                        .setBirthDate((BirthDate) i.getArguments()[1])
                        .setGender((Gender) i.getArguments()[2]));

        UpdatePersonalInformationDTO dto = new UpdatePersonalInformationDTO()
                .setName(new Name("Updated", "Name"))
                .setBirthDate(new BirthDate(LocalDate.now()))
                .setGender(new Gender(Gender.Values.FEMALE));
        MvcResult result = mockMvc.perform(buildRequest(PathBuilder.Post().users().userId(1).personalInformation().build(), dto))
                .andExpect(status().isOk()).andReturn();

        UserDTO user = objectMapper.readValue(result.getResponse().getContentAsString(), UserDTO.class);
        assertThat(user.getName()).isEqualTo(dto.getName());
        assertThat(user.getBirthDate()).isEqualTo(dto.getBirthDate());
        assertThat(user.getGender()).isEqualTo(dto.getGender());
    }

    @Test
    public void testVerifyUser() throws Exception {
        when(userDomainService.verifyUser(eq(tokenString()))).thenReturn(Optional.of(user));

        mockMvc.perform(buildRequestWithoutAuthorization(PathBuilder.Get().users().userId(1).verificationToken().build())
                .param("verificationToken", verificationToken().getToken().getToken()))
                .andExpect(status().is(303));
    }

    @Test
    public void testGetUsersCategories() throws Exception {
        when(userDomainService.fetchCategories()).thenReturn(List.of(variableCategory()));

        MvcResult result = mockMvc.perform(buildRequest(PathBuilder.Get().users().userId(1).categories().build()))
                .andExpect(status().isOk()).andReturn();

        List<CategoryDTO> categories = objectMapper.readValue(result.getResponse().getContentAsString(),
                new TypeReference<>() {
                });
        assertThat(categories).hasSize(1);
    }


    @Test
    public void testGetUsersVariableTransactions() throws Exception {
        when(userDomainService.fetchVariableTransactions(anyInt())).thenReturn(List.of(variableTransaction()));

        MvcResult result = mockMvc.perform(buildRequest(PathBuilder.Get().users().userId(1).variableTransactions().build()))
                .andExpect(status().isOk()).andReturn();

        List<VariableTransactionDTO> transactions = objectMapper.readValue(result.getResponse().getContentAsString(),
                new TypeReference<>() {
                });
        assertThat(transactions).hasSize(1);
    }

    @Test
    public void testGetUsersFixedTransactions() throws Exception {
        when(userDomainService.fetchFixedTransactions()).thenReturn(List.of(fixedTransaction()));

        MvcResult result = mockMvc.perform(buildRequest(PathBuilder.Get().users().userId(1).fixedTransactions().build()))
                .andExpect(status().isOk()).andReturn();

        List<FixedTransactionDTO> transactions = objectMapper.readValue(result.getResponse().getContentAsString(),
                new TypeReference<>() {
                });
        assertThat(transactions).hasSize(1);
    }
}