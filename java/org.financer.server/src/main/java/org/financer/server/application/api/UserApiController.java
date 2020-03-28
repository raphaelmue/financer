package org.financer.server.application.api;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.financer.server.application.service.AuthenticationService;
import org.financer.server.domain.model.user.UserEntity;
import org.financer.server.domain.service.UserDomainService;
import org.financer.shared.domain.model.api.CategoryDTO;
import org.financer.shared.domain.model.api.UserDTO;
import org.financer.shared.domain.model.value.objects.*;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
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
        Optional<UserEntity> userOptional = userDomainService.checkCredentials(email, password, new IPAddress(request.getRemoteAddr()), null);
        return userOptional.map(userEntity -> new ResponseEntity<>(modelMapper.map(userEntity, UserDTO.class), HttpStatus.OK))
                .orElseGet(() -> new ResponseEntity<>(HttpStatus.FORBIDDEN));
    }

    @Override
    public ResponseEntity<UserDTO> registerUser(@NotNull @Valid String email, @NotNull @Valid String name,
                                                @NotNull @Valid String surname, @NotNull @Valid String password,
                                                @NotNull @Valid LocalDate birthDate, @NotNull @Valid String gender) {
        UserEntity userEntity = userDomainService.registerUser(
                new UserEntity()
                        .setId(-1)
                        .setEmail(new Email(email))
                        .setName(new Name(name, surname))
                        .setPassword(new HashedPassword(password))
                        .setBirthDate(new BirthDate(birthDate))
                        .setGender(new Gender(gender)),
                new IPAddress(request.getRemoteAddr()), null);
        return new ResponseEntity<>(modelMapper.map(userEntity, UserDTO.class), HttpStatus.OK);
    }

    @Override
    public ResponseEntity<Void> updateUser(@NotNull @Valid UserDTO user) {
        return null;
    }

    @Override
    public ResponseEntity<List<CategoryDTO>> getUsersCategories(@NotBlank @PathVariable("userId") @Min(1) Long userId) {
        return new ResponseEntity<>(HttpStatus.NOT_IMPLEMENTED);
    }
}
