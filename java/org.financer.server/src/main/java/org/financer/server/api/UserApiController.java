package org.financer.server.api;

import org.financer.shared.model.api.UserDTO;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import java.time.LocalDate;

@Controller
public class UserApiController implements UserApi {

    @Override
    public ResponseEntity<UserDTO> loginUser(@NotNull @Valid String email, @NotNull @Valid String password) {
        return null;
    }

    @Override
    public ResponseEntity<UserDTO> registerUser(@NotNull @Valid String email, @NotNull @Valid String name, @NotNull @Valid String surname, @NotNull @Valid String password, @NotNull @Valid LocalDate birthDate, @NotNull @Valid String gender) {
        return null;
    }

    @Override
    public ResponseEntity<Void> updateUser(@NotNull @Valid UserDTO user) {
        return null;
    }
}