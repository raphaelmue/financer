package org.financer.server.domain.model.statistics;

import lombok.Data;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

@Data
public class StatisticDataSetImpl<I, N, D> implements StatisticDataSet<I, N, D> {

    private final Map<I, StatisticRecord<N, D>> records = new HashMap<>();

    @Override
    public void addRecord(I index, N name, D data) {
        Map<N, D> dataMap = new HashMap<>();
        dataMap.put(name, data);
        this.records.put(index, new StatisticRecordImpl<>(dataMap));
    }

    @Override
    public void addRecord(I index, Map<N, D> data) {
        this.records.put(index, new StatisticRecordImpl<>(data));
    }

    @Override
    public Optional<StatisticRecord<N, D>> getRecordByIndex(I index) {
        return Optional.ofNullable(this.records.get(index));
    }
}
