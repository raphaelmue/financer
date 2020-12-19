import React from 'react';

export interface ChartProps<Data> {
    data?: Data[]
}

export abstract class StatisticsComponent<Data, Props extends ChartProps<Data>, State extends any> extends React.Component<Props, State> {
    abstract transformData(): Record<string, any>[]
}
