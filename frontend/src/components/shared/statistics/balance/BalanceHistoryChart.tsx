import React                              from 'react';
import {connect}                          from 'react-redux';
import {WithTranslation, withTranslation} from 'react-i18next';
import {statisticDispatchMap}             from '../../../../store/api/statistic.api';
import {AppState}                         from '../../../../store/reducers/root.reducers';
import {StatisticReducerProps}            from '../../../../store/reducers/statistic.reducer';
import {Line}                             from '@ant-design/charts';
import {getCurrencySymbol}                from '../../user/settings/settingsUtil';
import {UserReducerState}                 from '../../../../store/reducers/user.reducers';
import ProCard                            from '@ant-design/pro-card';
import {BalanceHistory}                   from '../../../../.openapi';
import {Select}                           from 'antd';

const {Option} = Select;

interface BalanceHistoryChartComponentProps extends WithTranslation<'default'>, StatisticReducerProps {
    userState: UserReducerState
}

interface BalanceHistoryChartComponentState {
    data: Record<string, number>[],
    numberOfMonths: number,
    loading: boolean
}

class BalanceHistoryChart extends React.Component<BalanceHistoryChartComponentProps, BalanceHistoryChartComponentState> {

    constructor(props: BalanceHistoryChartComponentProps) {
        super(props);

        this.state = {
            data: [],
            numberOfMonths: 6,
            loading: true
        };
    }

    componentDidMount() {
        this.loadBalanceHistory();
    }

    loadBalanceHistory() {
        this.setState({data: [], loading: true});
        if (this.props.userState.user?.id) {
            this.props.dispatchLoadBalanceHistory({
                userId: this.props.userState.user?.id,
                numberOfMonths: this.state.numberOfMonths
            }, (balanceHistory) => {
                this.setState({
                    data: this.transformData(balanceHistory),
                    loading: false
                });
            });
        }
    }

    transformData(balanceHistory: BalanceHistory): Record<string, any>[] {
        const records: Record<string, any>[] = [];
        if (balanceHistory.records !== undefined) {
            for (const key of Object.keys(balanceHistory.records)) {
                records.push({
                    date: key,
                    amount: balanceHistory.records[key].balance
                });
            }
        }
        return records;
    }

    onChangeNumberOfMonths(value: number) {
        this.setState({numberOfMonths: value}, () => this.loadBalanceHistory());
    }

    render() {
        return (
            <ProCard
                title={this.props.t('Statistics.History.BalanceHistory')}
                loading={this.state.loading}
                extra={
                    <Select<number>
                        onChange={this.onChangeNumberOfMonths.bind(this)}
                        defaultValue={this.state.numberOfMonths}>
                        <Option value={6}>6 Months</Option>
                        <Option value={12}>1 Year</Option>
                        <Option value={24}>2 Years</Option>
                        <Option value={48}>4 Years</Option>
                    </Select>
                }>
                <Line
                    autoFit
                    smooth
                    tooltip={{
                        title: this.props.t('Statistics.Balance')?.toString(),
                        formatter: datum => {
                            return {
                                name: this.props.t('Transaction.Amount')?.toString() || '',
                                value: getCurrencySymbol() + ' ' + datum.amount
                            };
                        }
                    }}
                    data={this.state.data}
                    yAxis={{
                        title: {
                            text: this.props.t('Transaction.Amount')?.toString() || ''
                        },
                        nice: true,
                        label: {
                            formatter: datum => {
                                return getCurrencySymbol() + ' ' + datum;
                            }
                        }
                    }}
                    xAxis={{
                        title: {
                            text: this.props.t('Transaction.ValueDate')?.toString() || ''
                        }
                    }}
                    xField={'date'}
                    yField={'amount'}
                    padding={'auto'}
                    annotations={[
                        {
                            type: 'regionFilter',
                            start: ['min', Number.MIN_SAFE_INTEGER.toString()],
                            end: ['max', '0'],
                            color: '#F4664A',
                        }, {
                            type: 'line',
                            start: ['min', '0'],
                            end: ['max', '0'],
                            style: {
                                stroke: '#F4664A',
                                lineDash: [2, 2],
                            },
                        }
                    ]}
                />
            </ProCard>
        );
    }
}

const mapStateToProps = (state: AppState) => {
    return {
        userState: state.user,
        statisticState: state.statistic
    };
};

export default connect(mapStateToProps, statisticDispatchMap)(withTranslation<'default'>()(BalanceHistoryChart));
