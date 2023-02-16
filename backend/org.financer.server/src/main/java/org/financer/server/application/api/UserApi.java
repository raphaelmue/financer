package org.financer.server.application.api;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.enums.SecuritySchemeIn;
import io.swagger.v3.oas.annotations.enums.SecuritySchemeType;
import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.security.SecurityScheme;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.financer.server.application.api.util.PageableParameters;
import org.financer.shared.domain.model.api.category.CategoryDTO;
import org.financer.shared.domain.model.api.transaction.fixed.FixedTransactionDTO;
import org.financer.shared.domain.model.api.transaction.variable.VariableTransactionDTO;
import org.financer.shared.domain.model.api.user.*;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.hateoas.PagedModel;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import jakarta.validation.Valid;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import java.net.URISyntaxException;
import java.util.List;

@SecurityScheme(
        name = "TokenAuth",
        scheme = "bearer",
        bearerFormat = "Token",
        type = SecuritySchemeType.HTTP,
        in = SecuritySchemeIn.HEADER,
        description = "Authentication requires a token that is generated when login or register.")
@Tag(name = "user", description = "Operations with users")
public interface UserApi {

    /**
     * Logs the user into the system.
     *
     * @param email    email of the user
     * @param password password of the user
     * @return User object if credentials are correct
     */
    @Operation(
            summary = "Logs the user into the system",
            tags = {"user"})
    @ApiResponse(
            responseCode = "200",
            description = "Users credentials are correct.",
            content = @Content(schema = @Schema(implementation = UserDTO.class)))
    @ApiResponse(
            responseCode = "403",
            description = "Users credentials are invalid.")
    @GetMapping(
            value = "/users",
            produces = {"application/json"},
            headers = "Accept=application/json")
    ResponseEntity<UserDTO> loginUser(
            @Parameter(description = "Email of the user to log in", required = true)
            @RequestParam(value = "email") @NotNull @Valid String email,
            @Parameter(description = "Plain text password of the user to log in", required = true)
            @RequestParam(value = "password") @NotNull @Valid String password);

    /**
     * Deletes a token. This is called, when the user logs out of a client.
     *
     * @param userId  id of the user
     * @param tokenId id of the token to delete
     * @return void
     */
    @Operation(
            summary = "Deletes a token",
            tags = {"user"},
            security = @SecurityRequirement(name = "TokenAuth"))
    @ApiResponse(
            responseCode = "200",
            description = "Token was successfully deleted")
    @DeleteMapping(
            value = "/users/{userId}/tokens/{tokenId}",
            produces = {"application/json"},
            headers = "Accept=application/json")
    ResponseEntity<Void> deleteToken(
            @Parameter(description = "ID of the user to which the token is assigned", required = true)
            @PathVariable("userId") @NotBlank @Min(1) Long userId,
            @Parameter(description = "ID of the token that will be deleted", required = true)
            @PathVariable("tokenId") @NotBlank @Min(1) Long tokenId);

    /**
     * Registers a new user.
     *
     * @return User object
     */
    @Operation(
            summary = "Registers a new user",
            tags = {"user"})
    @ApiResponse(
            responseCode = "200",
            description = "User was successfully registered",
            content = @Content(schema = @Schema(implementation = UserDTO.class)))
    @PutMapping(
            value = "/users",
            produces = {"application/json"},
            headers = "Accept=application/json")
    ResponseEntity<UserDTO> registerUser(
            @io.swagger.v3.oas.annotations.parameters.RequestBody(
                    description = "User that will be registered",
                    required = true,
                    content = @Content(schema = @Schema(implementation = RegisterUserDTO.class)))
            @RequestBody @NotNull @Valid RegisterUserDTO registerUserDTO);

    /**
     * Returns a user by its id
     *
     * @param userId id of the user to return
     * @return user
     */
    @Operation(
            summary = "Returns a user",
            tags = {"user"},
            security = @SecurityRequirement(name = "TokenAuth"))
    @ApiResponse(
            responseCode = "200",
            description = "User was successfully returned",
            content = @Content(schema = @Schema(implementation = UserDTO.class)))
    @GetMapping(
            value = "/users/{userId}",
            produces = {"application/json"},
            headers = "Accept=application/json")
    ResponseEntity<UserDTO> getUser(
            @Parameter(description = "ID of the user whose password will be changed")
            @PathVariable("userId") @NotBlank @Min(1) Long userId);

    /**
     * Updates the password of the given user.
     *
     * @param userId         id of the user
     * @param updatePassword updated encrypted password
     * @return updated user object
     */
    @Operation(
            summary = "Updates the users password",
            tags = {"user"},
            security = @SecurityRequirement(name = "TokenAuth"))
    @ApiResponse(
            responseCode = "200",
            description = "Users password was successfully updated",
            content = @Content(schema = @Schema(implementation = UserDTO.class)))
    @PostMapping(
            value = "/users/{userId}/password",
            produces = {"application/json"},
            headers = "Accept=application/json")
    ResponseEntity<UserDTO> updateUsersPassword(
            @Parameter(description = "ID of the user whose password will be changed", required = true)
            @PathVariable("userId") @NotBlank @Min(1) Long userId,
            @io.swagger.v3.oas.annotations.parameters.RequestBody(
                    description = "Users password that will be updated",
                    required = true,
                    content = @Content(schema = @Schema(implementation = UpdatePasswordDTO.class)))
            @RequestBody @NotNull @Valid UpdatePasswordDTO updatePassword);

    /**
     * Updates the users personal information
     *
     * @param userId              id of the user
     * @param personalInformation updated personal information
     * @return updated user object
     */
    @Operation(
            summary = "Updates the users personal information",
            tags = {"user"},
            security = @SecurityRequirement(name = "TokenAuth"))
    @ApiResponse(
            responseCode = "200",
            description = "Users personal information were successfully updated",
            content = @Content(schema = @Schema(implementation = UserDTO.class)))
    @PostMapping(
            value = "/users/{userId}/personalInformation",
            produces = {"application/json"},
            headers = "Accept=application/json")
    ResponseEntity<UserDTO> updateUsersPersonalInformation(
            @Parameter(description = "ID of the user whose personal information will be changed", required = true)
            @PathVariable("userId") @NotBlank @Min(1) Long userId,
            @io.swagger.v3.oas.annotations.parameters.RequestBody(
                    description = "Users personal information that will be updated",
                    required = true,
                    content = @Content(schema = @Schema(implementation = UpdatePersonalInformationDTO.class)))
            @RequestBody @NotNull @Valid UpdatePersonalInformationDTO personalInformation);

    /**
     * Updates users settings.
     *
     * @param userId  id of the user
     * @param setting updated settings
     * @return update user object
     */
    @Operation(
            summary = "Updates the users settings",
            tags = {"user"},
            security = @SecurityRequirement(name = "TokenAuth"))
    @ApiResponse(
            responseCode = "200",
            description = "Users settings were successfully updated",
            content = @Content(schema = @Schema(implementation = UserDTO.class)))
    @PostMapping(
            value = "/users/{userId}/settings",
            produces = {"application/json"},
            headers = "Accept=application/json")
    ResponseEntity<UserDTO> updateUsersSettings(
            @Parameter(description = "ID of the user whose settings will be updated", required = true)
            @PathVariable("userId") @NotBlank @Min(1) Long userId,
            @io.swagger.v3.oas.annotations.parameters.RequestBody(
                    description = "Users personal information that will be updated",
                    required = true,
                    content = @Content(schema = @Schema(implementation = UpdateSettingsDTO.class)))
            @RequestBody @NotNull @Valid UpdateSettingsDTO setting);

    /**
     * Verifies a users email address by checking the given verification token.
     *
     * @param userId            id of the user to check
     * @param verificationToken verification token to check
     */
    @Operation(
            summary = "Verifies a users email address",
            tags = {"user"},
            security = @SecurityRequirement(name = "TokenAuth"))
    @ApiResponse(
            responseCode = "200",
            description = "User is successfully verified")
    @GetMapping(
            value = "/users/{userId}/verificationToken",
            produces = {"application/json"},
            headers = "Accept=application/json")
    ResponseEntity<Object> verifyUser(
            @Parameter(description = "ID of the user that will be verified", required = true)
            @PathVariable("userId") @NotBlank @Min(1) Long userId,
            @Parameter(description = "Verification token to verify user", required = true)
            @RequestParam("verificationToken") @NotNull @Valid String verificationToken) throws URISyntaxException;

    /**
     * Fetches the users fixed transactions.
     *
     * @param userId     user id
     * @param pageable
     * @param onlyActive
     * @param categoryId
     * @return list of transactions
     */
    @Operation(
            summary = "Fetches all fixed transactions of the user",
            tags = {"user"},
            security = @SecurityRequirement(name = "TokenAuth"))
    @ApiResponse(
            responseCode = "200",
            description = "Users fixed transactions were successfully fetched")
    @PageableParameters
    @GetMapping(
            value = "/users/{userId}/fixedTransactions",
            produces = {"application/json"},
            headers = "Accept=application/json")
    ResponseEntity<PagedModel<FixedTransactionDTO>> getUsersFixedTransactions(
            @Parameter(description = "ID of the user whose fixed transactions will be fetched", required = true)
            @PathVariable("userId") @NotBlank @Min(1) Long userId,
            @Parameter(hidden = true) @Valid
            @PageableDefault(size = 20, sort = "timeRange.startDate", direction = Sort.Direction.DESC) Pageable pageable,
            @Parameter(description = "Indicates whether to return only active fixed transactions")
            @RequestParam(value = "onlyActive", required = false, defaultValue = "true") Boolean onlyActive,
            @Parameter(description = "ID of the category")
            @RequestParam(value = "categoryId", required = false) @Min(1) Long categoryId);

    /**
     * Fetches the users categories.
     *
     * @param userId user id
     * @return tree of categories
     */
    @Operation(
            summary = "Fetches all categories of the user",
            tags = {"user"},
            security = @SecurityRequirement(name = "TokenAuth"))
    @ApiResponse(
            responseCode = "200",
            description = "Users categories were successfully fetched",
            content = @Content(array = @ArraySchema(schema = @Schema(implementation = CategoryDTO.class))))
    @GetMapping(
            value = "/users/{userId}/categories",
            produces = {"application/json"},
            headers = "Accept=application/json")
    ResponseEntity<List<CategoryDTO>> getUsersCategories(
            @Parameter(description = "ID of the user whose categories will be fetched", required = true)
            @PathVariable("userId") @NotBlank @Min(1) Long userId);


    /**
     * Fetches the users variable transactions.
     *
     * @param userId   user id
     * @param pageable
     * @return list of transactions
     */
    @Operation(
            summary = "Fetches all variable transactions of the user",
            tags = {"user"},
            security = @SecurityRequirement(name = "TokenAuth"))
    @ApiResponse(
            responseCode = "200",
            description = "Users variable transactions were successfully fetched")
    @PageableParameters
    @GetMapping(
            value = "/users/{userId}/variableTransactions",
            produces = {"application/json"},
            headers = "Accept=application/json")
    ResponseEntity<PagedModel<VariableTransactionDTO>> getUsersVariableTransactions(
            @Parameter(description = "ID of the user whose variable transactions will be fetched", required = true)
            @PathVariable("userId") @NotBlank @Min(1) Long userId,
            @Parameter(hidden = true) @Valid
            @PageableDefault(size = 20, sort = "valueDate.date", direction = Sort.Direction.DESC) Pageable pageable);
}
