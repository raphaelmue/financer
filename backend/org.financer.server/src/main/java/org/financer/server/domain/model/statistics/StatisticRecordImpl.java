package org.financer.server.domain.model.statistics;

import lombok.Data;

import java.util.Map;

@Data
public class StatisticRecordImpl<N, D> implements StatisticRecord<N, D> {

    private final Map<N, D> data;

    @Override
    public void addRecord(N name, D data) {
        this.data.put(name, data);
    }
}
