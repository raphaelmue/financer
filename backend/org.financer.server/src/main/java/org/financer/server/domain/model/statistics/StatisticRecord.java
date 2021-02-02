package org.financer.server.domain.model.statistics;

import java.util.Map;

public interface StatisticRecord<N, D> {

    Map<N, D> getData();

    void addRecord(N name, D data);

}
