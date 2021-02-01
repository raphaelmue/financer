package org.financer.server.domain.model.statistics;

import lombok.Data;

import java.util.Map;
import java.util.Optional;
import java.util.TreeMap;

@Data
public class StatisticDataSetImpl<I extends Comparable<I>, N extends Comparable<I>, D> implements StatisticDataSet<I, N, D> {

    private final Map<I, StatisticRecord<N, D>> records = new TreeMap<>();

    @Override
    public void addRecord(I index, N name, D data) {
        Map<N, D> dataMap = new TreeMap<>();
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
