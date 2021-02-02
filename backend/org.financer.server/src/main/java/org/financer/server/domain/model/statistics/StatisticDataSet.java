package org.financer.server.domain.model.statistics;

import java.util.Map;
import java.util.Optional;

public interface StatisticDataSet<I extends Comparable<I>, N extends Comparable<I>, D> {

    Map<I, StatisticRecord<N, D>> getRecords();

    void addRecord(I index, Map<N, D> data);

    void addRecord(I index, N name, D data);

    Optional<StatisticRecord<N, D>> getRecordByIndex(I index);

}
