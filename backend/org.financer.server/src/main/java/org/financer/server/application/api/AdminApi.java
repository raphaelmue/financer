package org.financer.server.application.api;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.financer.server.application.api.util.PageableParameters;
import org.financer.shared.domain.model.api.admin.AdminConfigurationDTO;
import org.financer.shared.domain.model.api.admin.InitAdminConfigurationDTO;
import org.financer.shared.domain.model.api.admin.UpdateAdminConfigurationDTO;
import org.financer.shared.domain.model.api.user.UserDTO;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.hateoas.PagedModel;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;

@Tag(name = "admin", description = "Operations for admins")
@RequestMapping("/admin")
public interface AdminApi {

    @Operation(
            summary = "Fetches the configuration of the Financer Server",
            tags = {"admin"},
            security = @SecurityRequirement(name = "TokenAuth"))
    @ApiResponse(
            responseCode = "200",
            description = "Configuration is successfully fetched",
            content = @Content(schema = @Schema(implementation = AdminConfigurationDTO.class)))
    @GetMapping(
            value = "/configuration",
            produces = {"application/json"},
            headers = "Accept=application/json")
    ResponseEntity<AdminConfigurationDTO> getConfiguration();

    @Operation(
            summary = "Updates the configuration of the Financer Server",
            tags = {"admin"},
            security = @SecurityRequirement(name = "TokenAuth"))
    @ApiResponse(
            responseCode = "200",
            description = "Configuration is successfully updated",
            content = @Content(schema = @Schema(implementation = AdminConfigurationDTO.class)))
    @PostMapping(
            value = "/configuration",
            produces = {"application/json"},
            headers = "Accept=application/json")
    ResponseEntity<AdminConfigurationDTO> updateConfiguration(
            @io.swagger.v3.oas.annotations.parameters.RequestBody(
                    description = "Configuration to be updated",
                    required = true,
                    content = @Content(schema = @Schema(implementation = UpdateAdminConfigurationDTO.class)))
            @RequestBody @NotNull @Valid UpdateAdminConfigurationDTO updateAdminConfigurationDTO);

    @Operation(
            summary = "Initializes the server configuration",
            tags = {"admin"},
            security = @SecurityRequirement(name = "TokenAuth"))
    @ApiResponse(
            responseCode = "200",
            description = "Configuration is successfully initialized",
            content = @Content(schema = @Schema(implementation = AdminConfigurationDTO.class)))
    @PutMapping(
            value = "/configuration",
            produces = {"application/json"},
            headers = "Accept=application/json")
    ResponseEntity<AdminConfigurationDTO> initializeServerConfiguration(
            @io.swagger.v3.oas.annotations.parameters.RequestBody(
                    description = "Configuration to be updated",
                    required = true,
                    content = @Content(schema = @Schema(implementation = InitAdminConfigurationDTO.class)))
            @RequestBody @NotNull @Valid InitAdminConfigurationDTO updateAdminConfigurationDTO);

    @Operation(
            summary = "Fetches all users of the system",
            tags = {"admin"},
            security = @SecurityRequirement(name = "TokenAuth"))
    @ApiResponse(
            responseCode = "200",
            description = "Users are successfully fetched")
    @PageableParameters
    @GetMapping(
            value = "/users",
            produces = {"application/json"},
            headers = "Accept=application/json")
    ResponseEntity<PagedModel<UserDTO>> getUsers(
            @Parameter(hidden = true) @Valid
            @PageableDefault(size = 20, sort = "name.surname", direction = Sort.Direction.ASC) Pageable pageable);

}
