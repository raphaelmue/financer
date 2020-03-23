package org.financer.server.api;

import org.financer.server.service.FinancerService;
import org.financer.shared.model.api.CategoryDTO;
import org.financer.shared.model.api.UserDTO;
import org.financer.shared.model.user.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;

import javax.validation.Valid;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.time.LocalDate;
import java.util.List;

@Controller
public class UserApiController implements UserApi {

    @Autowired
    private FinancerService financerService;

    @Override
    public ResponseEntity<UserDTO> loginUser(@NotNull @Valid String email, @NotNull @Valid String password) {
        User user = financerService.checkCredentials(email, password, "", "", false);
        if (user != null) {
            return new ResponseEntity<>(new UserDTO().setId(user.getId()).setName(user.getName()).setEmail(user.getEmail()), HttpStatus.OK);
        }
        return new ResponseEntity<>(HttpStatus.FORBIDDEN);
    }

    @Override
    public ResponseEntity<UserDTO> registerUser(@NotNull @Valid String email, @NotNull @Valid String name, @NotNull @Valid String surname, @NotNull @Valid String password, @NotNull @Valid LocalDate birthDate, @NotNull @Valid String gender) {
        return null;
    }

    @Override
    public ResponseEntity<Void> updateUser(@NotNull @Valid UserDTO user) {
        return null;
    }

    @Override
    public ResponseEntity<List<CategoryDTO>> getUsersCategories(@NotBlank @Min(1) Long userId) {
        return null;
    }
}
