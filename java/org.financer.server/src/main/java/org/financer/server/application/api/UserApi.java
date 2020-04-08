package org.financer.server.application.api;

import org.financer.shared.domain.model.api.category.CategoryDTO;
import org.financer.shared.domain.model.api.transaction.VariableTransactionDTO;
import org.financer.shared.domain.model.api.user.RegisterUserDTO;
import org.financer.shared.domain.model.api.user.UserDTO;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.net.URISyntaxException;
import java.time.LocalDate;
import java.util.List;

public interface UserApi {

    /**
     * Logs the user into the system.
     *
     * @param email    email of the user
     * @param password password of the user
     * @return User object if credentials are correct
     */
    @GetMapping(
            value = "/users",
            produces = {"application/json"},
            headers = "Accept=application/json")
    ResponseEntity<UserDTO> loginUser(@NotNull @Valid @RequestParam(value = "email") String email,
                                      @NotNull @Valid @RequestParam(value = "password") String password);

    /**
     * Deletes a token. This is called, when the user logs out of a client.
     *
     * @param userId  id of the user
     * @param tokenId id of the token to delete
     * @return void
     */
    @DeleteMapping(
            value = "/users/{userId}/tokens/{tokenId}",
            produces = {"application/json"},
            headers = "Accept=application/json")
    ResponseEntity<Void> deleteToken(@NotBlank @PathVariable("userId") @Min(1) Long userId,
                                     @NotBlank @PathVariable("tokenId") @Min(1) Long tokenId);

    /**
     * Registers a new user.
     *
     * @return User object
     */
    @PutMapping(
            value = "/users",
            produces = {"application/json"},
            headers = "Accept=application/json")
    ResponseEntity<UserDTO> registerUser(@NotNull @Valid @RequestBody RegisterUserDTO registerUserDTO);

    /**
     * Updates the users information
     *
     * @param user user object with updated information
     * @return null
     */
    @PostMapping(
            value = "/users",
            produces = {"application/json"},
            headers = "Accept=application/json")
    ResponseEntity<Void> updateUser(@NotNull @Valid @RequestParam(value = "user") UserDTO user);

    /**
     * Updates the password of the given user.
     *
     * @param userId      id of the user
     * @param oldPassword old password of the user to verify
     * @param newPassword updated unencrypted password
     * @return updated user object
     */
    @PostMapping(
            value = "/users/{userId}/password",
            produces = {"application/json"},
            headers = "Accept=application/json")
    ResponseEntity<UserDTO> updateUsersPassword(@NotBlank @PathVariable("userId") @Min(1) Long userId,
                                                @NotNull @Valid @RequestParam("oldPassword") String oldPassword,
                                                @NotNull @Valid @RequestParam("newPassword") String newPassword);

    /**
     * Updates the users personal information
     *
     * @param userId    id of the user
     * @param firstName updated first name of the user
     * @param surname   updated surname of the user
     * @param birthDate updated birth date of the user
     * @param gender    updated gender of the user
     * @return updated user object
     */
    @PostMapping(
            value = "/users/{userId}/personalInformation",
            produces = {"application/json"},
            headers = "Accept=application/json")
    ResponseEntity<UserDTO> updateUsersPersonalInformation(@NotBlank @PathVariable("userId") @Min(1) Long userId,
                                                           @NotNull @Valid @RequestParam("firstName") String firstName,
                                                           @NotNull @Valid @RequestParam("surname") String surname,
                                                           @NotNull @Valid @RequestParam(value = "birthDate") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate birthDate,
                                                           @NotNull @Valid @RequestParam(value = "gender") String gender);

    /**
     * Verifies a users email address by checking the given verification token.
     *
     * @param userId            id of the user to check
     * @param verificationToken verification token to check
     */
    @GetMapping(
            value = "/users/{userId}/verificationToken",
            produces = {"application/json"},
            headers = "Accept=application/json")
    ResponseEntity<Object> verifyUser(@NotBlank @PathVariable("userId") @Min(1) Long userId,
                                      @NotNull @Valid @RequestParam("verificationToken") String verificationToken) throws URISyntaxException;

    /**
     * Fetches the users categories.
     *
     * @param userId user id
     * @return tree of categories
     */
    @GetMapping(
            value = "/users/{userId}/categories",
            produces = {"application/json"},
            headers = "Accept=application/json")
    ResponseEntity<List<CategoryDTO>> getUsersCategories(@NotBlank @PathVariable("userId") @Min(1) Long userId);


    /**
     * Fetches the users variable transactions.
     *
     * @param userId user id
     * @return list of transactions
     */
    @GetMapping(
            value = "/users/{userId}/variableTransactions",
            produces = {"application/json"},
            headers = "Accept=application/json")
    ResponseEntity<List<VariableTransactionDTO>> getUsersVariableTransactions(@NotBlank @PathVariable("userId") @Min(1) Long userId,
                                                                              @Valid @RequestParam(value = "page", defaultValue = "0") int page);

    /**
     * Fetches the users fixed transactions.
     *
     * @param userId user id
     * @return list of transactions
     */
    @GetMapping(
            value = "/users/{userId}/fixedTransactions",
            produces = {"application/json"},
            headers = "Accept=application/json")
    ResponseEntity<List<VariableTransactionDTO>> getUsersFixedTransactions(@NotBlank @PathVariable("userId") @Min(1) Long userId);
}
