import React                              from 'react';
import {connect}                          from 'react-redux';
import {WithTranslation, withTranslation} from 'react-i18next';
import {statisticDispatchMap}             from '../../../../store/api/statistic.api';
import {AppState}                         from '../../../../store/reducers/root.reducers';
import {StatisticReducerProps}            from '../../../../store/reducers/statistic.reducer';
import {Line}                             from '@ant-design/charts';
import {getCurrencySymbol}                from '../../user/settings/settingsUtil';
import {UserState}                        from '../../../../store/reducers/user.reducers';
import {Card, Select}                     from 'antd';
import {DataSet}                          from '../../../../.openapi';

const {Option} = Select;

interface BalanceHistoryChartComponentProps extends WithTranslation<'default'>, StatisticReducerProps {
    userState: UserState
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

    transformData(balanceHistory: DataSet): Record<string, any>[] {
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
            <Card
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
                    height={250}
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
            </Card>
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
