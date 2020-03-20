package org.financer.server.api;

import org.financer.shared.model.api.UserDTO;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestParam;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import java.time.LocalDate;

public interface UserApi {

    /**
     * Logs the user into the system.
     *
     * @param email    email of the user
     * @param password password of the user
     * @return User object if credentials are correct
     */
    @GetMapping(
            value = "/user",
            produces = {"application/json"})
    ResponseEntity<UserDTO> loginUser(@NotNull @Valid @RequestParam(value = "email") String email,
                                      @NotNull @Valid @RequestParam(value = "password") String password);

    /**
     * Registers a new user.
     *
     * @param email     email of the user
     * @param name      first name of the user
     * @param surname   surname of the user
     * @param password  password of the user
     * @param birthDate birth date of the user
     * @param gender    gender of the user as string
     * @return User object
     */
    @PutMapping(
            value = "/user",
            produces = {"application/json"})
    ResponseEntity<UserDTO> registerUser(@NotNull @Valid @RequestParam(value = "email") String email,
                                         @NotNull @Valid @RequestParam(value = "name") String name,
                                         @NotNull @Valid @RequestParam(value = "surname") String surname,
                                         @NotNull @Valid @RequestParam(value = "password") String password,
                                         @NotNull @Valid @RequestParam(value = "birthDate") LocalDate birthDate,
                                         @NotNull @Valid @RequestParam(value = "gender") String gender);

    /**
     * Updates the users information
     *
     * @param user user object with updated information
     * @return null
     */
    @PostMapping(
            value = "/user",
            produces = {"application/json"})
    ResponseEntity<Void> updateUser(@NotNull @Valid @RequestParam(value = "user") UserDTO user);
}
