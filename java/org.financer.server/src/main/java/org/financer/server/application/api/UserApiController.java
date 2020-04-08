package org.financer.server.application.api;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.financer.server.application.service.AuthenticationService;
import org.financer.server.domain.model.category.Category;
import org.financer.server.domain.model.transaction.VariableTransaction;
import org.financer.server.domain.model.user.User;
import org.financer.server.domain.service.UserDomainService;
import org.financer.shared.domain.model.api.category.CategoryDTO;
import org.financer.shared.domain.model.api.transaction.VariableTransactionDTO;
import org.financer.shared.domain.model.api.user.RegisterUserDTO;
import org.financer.shared.domain.model.api.user.UserDTO;
import org.financer.shared.domain.model.value.objects.*;
import org.financer.util.mapping.ModelMapperUtils;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.net.URI;
import java.net.URISyntaxException;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Controller
public class UserApiController implements UserApi {

    @Autowired
    private UserDomainService userDomainService;

    @Autowired
    private ModelMapper modelMapper;

    @Autowired
    private AuthenticationService authenticationService;

    private final ObjectMapper objectMapper;
    private final HttpServletRequest request;


    @Autowired
    public UserApiController(ObjectMapper objectMapper, HttpServletRequest request) {
        this.objectMapper = objectMapper;
        this.request = request;
    }

    @Override
    public ResponseEntity<UserDTO> loginUser(@NotNull @Valid String email, @NotNull @Valid String password) {
        Optional<User> userOptional = userDomainService.checkCredentials(email, password, new IPAddress(request.getRemoteAddr()), null);
        return userOptional.map(userEntity -> new ResponseEntity<>(modelMapper.map(userEntity, UserDTO.class), HttpStatus.OK))
                .orElseGet(() -> new ResponseEntity<>(HttpStatus.FORBIDDEN));
    }

    @Override
    public ResponseEntity<Void> deleteToken(@NotBlank @Min(1) Long userId, @NotBlank @Min(1) Long tokenId) {
        userDomainService.deleteToken(userId, tokenId);
        return new ResponseEntity<>(HttpStatus.OK);
    }

    @Override
    public ResponseEntity<UserDTO> registerUser(@NotNull @Valid RegisterUserDTO registerUserDTO) {
        User user = userDomainService.registerUser(
                new User()
                        .setId(-1)
                        .setEmail(new Email(registerUserDTO.getEmail()))
                        .setName(new Name(registerUserDTO.getName(), registerUserDTO.getSurname()))
                        .setPassword(new HashedPassword(registerUserDTO.getPassword()))
                        .setBirthDate(new BirthDate(registerUserDTO.getBirthDate()))
                        .setGender(new Gender(registerUserDTO.getGender())),
                new IPAddress(request.getRemoteAddr()), null);
        return new ResponseEntity<>(modelMapper.map(user, UserDTO.class), HttpStatus.OK);
    }

    @Override
    public ResponseEntity<Void> updateUser(@NotNull @Valid UserDTO user) {
        return new ResponseEntity<>(HttpStatus.NOT_IMPLEMENTED);
    }

    @Override
    public ResponseEntity<UserDTO> updateUsersPassword(@NotBlank @Min(1) Long userId, @NotNull @Valid String oldPassword,
                                                       @NotNull @Valid String newPassword) {
        authenticationService.getAuthenticatedUser().throwIfNotUsersProperty(userId);
        User updateUser = userDomainService.updatePassword(userId, oldPassword, newPassword);
        return new ResponseEntity<>(modelMapper.map(updateUser, UserDTO.class), HttpStatus.OK);
    }

    @Override
    public ResponseEntity<UserDTO> updateUsersPersonalInformation(@NotBlank @Min(1) Long userId, @NotNull @Valid String firstName, @NotNull @Valid String surname, @NotNull @Valid LocalDate birthDate, @NotNull @Valid String gender) {
        return new ResponseEntity<>(HttpStatus.NOT_IMPLEMENTED);
    }

    @Override
    public ResponseEntity<Object> verifyUser(@NotBlank @Min(1) Long userId, @NotNull @Valid String verificationToken) throws URISyntaxException {
        Optional<User> userOptional = userDomainService.verifyUser(new TokenString(verificationToken));

        HttpHeaders httpHeaders = new HttpHeaders();
        httpHeaders.setLocation(new URI(""));
        return userOptional.map(userEntity -> new ResponseEntity<>(httpHeaders, HttpStatus.SEE_OTHER))
                .orElseGet(() -> new ResponseEntity<>(HttpStatus.FORBIDDEN));
    }

    @Override
    public ResponseEntity<List<CategoryDTO>> getUsersCategories(@NotBlank @PathVariable("userId") @Min(1) Long userId) {
        authenticationService.getAuthenticatedUser().throwIfNotUsersProperty(userId);
        List<Category> categories = userDomainService.fetchCategories(userId);
        return new ResponseEntity<>(ModelMapperUtils.mapAll(categories, CategoryDTO.class), HttpStatus.OK);
    }

    @Override
    public ResponseEntity<List<VariableTransactionDTO>> getUsersVariableTransactions(@NotBlank @Min(1) Long userId, int page) {
        authenticationService.getAuthenticatedUser().throwIfNotUsersProperty(userId);
        List<VariableTransaction> categories = userDomainService.fetchTransactions(userId, page);
        return new ResponseEntity<>(ModelMapperUtils.mapAll(categories, VariableTransactionDTO.class), HttpStatus.OK);
    }

    @Override
    public ResponseEntity<List<VariableTransactionDTO>> getUsersFixedTransactions(@NotBlank @Min(1) Long userId) {
        return new ResponseEntity<>(HttpStatus.NOT_IMPLEMENTED);
    }
}
