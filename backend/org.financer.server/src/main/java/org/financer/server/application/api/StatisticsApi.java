package org.financer.server.application.api;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.enums.SecuritySchemeIn;
import io.swagger.v3.oas.annotations.enums.SecuritySchemeType;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.security.SecurityScheme;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.financer.shared.domain.model.api.statistics.DataSetDTO;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import javax.validation.Valid;
import javax.validation.constraints.Max;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotBlank;
import java.util.List;

@SecurityScheme(
        name = "TokenAuth",
        scheme = "bearer",
        bearerFormat = "Token",
        type = SecuritySchemeType.HTTP,
        in = SecuritySchemeIn.HEADER,
        description = "Authentication requires a token that is generated when login or register.")
@Tag(name = "statistics", description = "Information about statistics")
@RequestMapping("/statistics")
public interface StatisticsApi {


    /**
     * Returns the history of users balance.
     *
     * @param userId         id of the users
     * @param numberOfMonths number of months
     * @return User object if credentials are correct
     */
    @Operation(
            summary = "Returns the history of the users balance",
            tags = {"user"},
            security = @SecurityRequirement(name = "TokenAuth"))
    @ApiResponse(
            responseCode = "200",
            description = "Users categories were successfully fetched",
            content = @Content(schema = @Schema(name = "BalanceHistory", implementation = DataSetDTO.class)))
    @GetMapping(
            value = "/users/{userId}/history",
            produces = {"application/json"},
            headers = "Accept=application/json")
    ResponseEntity<DataSetDTO> getUsersBalanceHistory(
            @Parameter(description = "ID of the user whose password will be changed")
            @PathVariable("userId") @NotBlank @Min(1) Long userId,
            @Parameter(description = "Number of months that are displayed")
            @RequestParam(value = "numberOfMonths", defaultValue = "6") @Min(1) @Max(36) @Valid int numberOfMonths);


    /**
     * Returns the history of users balance.
     *
     * @param userId         id of the users
     * @param numberOfMonths number of months
     * @return User object if credentials are correct
     */
    @Operation(
            summary = "Returns the history of the users category balance",
            tags = {"user"},
            security = @SecurityRequirement(name = "TokenAuth"))
    @ApiResponse(
            responseCode = "200",
            description = "Users categories were successfully fetched",
            content = @Content(schema = @Schema(name = "CategoryHistory", implementation = DataSetDTO.class)))
    @GetMapping(
            value = "/users/{userId}/categories/history",
            produces = {"application/json"},
            headers = "Accept=application/json")
    ResponseEntity<DataSetDTO> getCategoryHistory(
            @Parameter(description = "ID of the user whose password will be changed")
            @PathVariable("userId") @NotBlank @Min(1) Long userId,
            @Parameter(description = "Category IDs to be calculated", required = true)
            @RequestParam(value = "categoryIds") @Valid List<Long> categoryIds,
            @Parameter(description = "Number of months that are displayed")
            @RequestParam(value = "numberOfMonths", defaultValue = "6") @Min(1) @Max(36) @Valid int numberOfMonths);


    /**
     * Returns the history of users balance.
     *
     * @param userId         id of the users
     * @param balanceType    either "expenses" or "revenue"
     * @param numberOfMonths number of months
     * @return User object if credentials are correct
     */
    @Operation(
            summary = "Returns the history of the users category balance",
            tags = {"user"},
            security = @SecurityRequirement(name = "TokenAuth"))
    @ApiResponse(
            responseCode = "200",
            description = "Users categories were successfully fetched",
            content = @Content(schema = @Schema(name = "CategoryDistribution", implementation = DataSetDTO.class)))
    @GetMapping(
            value = "/users/{userId}/categories/distribution",
            produces = {"application/json"},
            headers = "Accept=application/json")
    ResponseEntity<DataSetDTO> getCategoryDistribution(
            @Parameter(description = "ID of the user whose password will be changed")
            @PathVariable("userId") @NotBlank @Min(1) Long userId,
            @Parameter(description = "Balance type", schema = @Schema(name = "BalanceType", type = "string", allowableValues = {"expenses", "revenue"}))
            @RequestParam(value = "balanceType", defaultValue = "expenses") String balanceType,
            @Parameter(description = "Number of months that are displayed")
            @RequestParam(value = "numberOfMonths", defaultValue = "6") @Min(1) @Max(36) @Valid int numberOfMonths);

}
