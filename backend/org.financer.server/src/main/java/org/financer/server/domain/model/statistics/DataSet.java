package org.financer.server.domain.model.statistics;

import org.financer.server.domain.model.Convertable;
import org.financer.shared.domain.model.api.statistics.DataSetDTO;

import java.util.Map;
import java.util.TreeMap;

public class DataSet extends StatisticDataSetImpl<String, String, Double> implements Convertable<DataSetDTO> {

    @Override
    public DataSetDTO map() {
        Map<String, Map<String, Double>> records = new TreeMap<>();

        for (Map.Entry<String, StatisticRecord<String, Double>> recordsEntry : this.getRecords().entrySet()) {
            records.put(recordsEntry.getKey(), recordsEntry.getValue().getData());
        }

        return new DataSetDTO().setRecords(records);
    }
}
