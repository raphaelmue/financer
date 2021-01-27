package org.financer.server.domain.model.statistics;

import org.financer.server.domain.model.Convertable;
import org.financer.shared.domain.model.api.statistics.user.BalanceHistoryDataSetDTO;

import java.util.Map;
import java.util.TreeMap;

public class DataSet extends StatisticDataSetImpl<String, String, Double> implements Convertable<BalanceHistoryDataSetDTO> {

    @Override
    public BalanceHistoryDataSetDTO map() {
        Map<String, Map<String, Double>> records = new TreeMap<>();

        for (Map.Entry<String, StatisticRecord<String, Double>> recordsEntry : this.getRecords().entrySet()) {
            records.put(recordsEntry.getKey(), recordsEntry.getValue().getData());
        }

        return new BalanceHistoryDataSetDTO().setRecords(records);
    }
}
