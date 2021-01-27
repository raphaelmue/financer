package org.financer.server.application.api;

import org.financer.server.domain.service.StatisticsDomainService;
import org.financer.shared.domain.model.api.statistics.user.BalanceHistoryDataSetDTO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;
import javax.validation.constraints.Max;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotBlank;
import java.util.List;

@RestController
public class StatisticsApiController implements StatisticsApi {

    @Autowired
    private StatisticsDomainService statisticsDomainService;

    @Override
    public ResponseEntity<BalanceHistoryDataSetDTO> getUsersBalanceHistory(@NotBlank @Min(1) Long userId, @Min(1) @Max(36) @Valid int numberOfMonths) {
        return new ResponseEntity<>(statisticsDomainService.getBalanceHistoryOfUser(userId, numberOfMonths).map(), HttpStatus.OK);
    }

    @Override
    public ResponseEntity<BalanceHistoryDataSetDTO> getCategoriesHistory(@NotBlank @Min(1) Long userId, List<Long> categoryIds, @Min(1) @Max(36) @Valid int numberOfMonths) {
        return new ResponseEntity<>(statisticsDomainService.getCategoriesHistory(userId, categoryIds, numberOfMonths).map(), HttpStatus.OK);
    }

    @Override
    public ResponseEntity<BalanceHistoryDataSetDTO> getCategoriesDistribution(@NotBlank @Min(1) Long userId, String balanceType, @Min(1) @Max(36) @Valid int numberOfMonths) {
        return new ResponseEntity<>(statisticsDomainService.getCategoriesDistribution(userId, balanceType, numberOfMonths).map(), HttpStatus.OK);
    }
}
