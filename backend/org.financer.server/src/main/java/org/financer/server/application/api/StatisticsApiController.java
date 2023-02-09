package org.financer.server.application.api;

import org.financer.server.domain.service.StatisticsDomainService;
import org.financer.shared.domain.model.api.statistics.DataSetDTO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;

import jakarta.validation.Valid;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import java.util.List;

@RestController
public class StatisticsApiController implements StatisticsApi {

    @Autowired
    private StatisticsDomainService statisticsDomainService;

    @Override
    public ResponseEntity<DataSetDTO> getUsersBalanceHistory(@NotBlank @Min(1) Long userId, @Min(1) @Max(36) @Valid int numberOfMonths) {
        return new ResponseEntity<>(statisticsDomainService.getBalanceHistoryOfUser(userId, numberOfMonths).map(), HttpStatus.OK);
    }

    @Override
    public ResponseEntity<DataSetDTO> getCategoryHistory(@NotBlank @Min(1) Long userId, List<Long> categoryIds, @Min(1) @Max(36) @Valid int numberOfMonths) {
        return new ResponseEntity<>(statisticsDomainService.getCategoriesHistory(userId, categoryIds, numberOfMonths).map(), HttpStatus.OK);
    }

    @Override
    public ResponseEntity<DataSetDTO> getCategoryDistribution(@NotBlank @Min(1) Long userId, String balanceType, @Min(1) @Max(36) @Valid int numberOfMonths) {
        return new ResponseEntity<>(statisticsDomainService.getCategoriesDistribution(userId, balanceType, numberOfMonths).map(), HttpStatus.OK);
    }

    @Override
    public ResponseEntity<DataSetDTO> getVariableTransactionCountHistory(@NotBlank @Min(1) Long userId, @Min(1) @Max(48) @Valid int numberOfMonths) {
        return new ResponseEntity<>(statisticsDomainService.getVariableTransactionCountHistory(userId, numberOfMonths).map(), HttpStatus.OK);
    }
}
