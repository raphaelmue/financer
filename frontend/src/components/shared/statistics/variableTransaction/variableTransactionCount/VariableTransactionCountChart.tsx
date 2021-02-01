import React                              from 'react';
import {connect}                          from 'react-redux';
import {WithTranslation, withTranslation} from 'react-i18next';
import {statisticDispatchMap}             from '../../../../../store/api/statistic.api';
import {AppState}                         from '../../../../../store/reducers/root.reducers';
import {StatisticReducerProps}            from '../../../../../store/reducers/statistic.reducer';
import {Area}                             from '@ant-design/charts';
import {UserState}                        from '../../../../../store/reducers/user.reducers';
import {Button, Card, Select, Space}      from 'antd';
import {DataSet}                          from '../../../../../.openapi';
import {ChartCard}                        from 'ant-design-pro/lib/Charts';
import VariableTransactionList            from '../../../transaction/variable/transactionList/VariableTransactionList';
import {PlusOutlined}                     from '@ant-design/icons';
import {Link}                             from 'react-router-dom';

const {Option} = Select;

interface VariableTransactionCountChartHistoryComponentProps extends WithTranslation<'default'>, StatisticReducerProps {
    userState: UserState
}

interface VariableTransactionCountChartHistoryComponentState {
    data: Record<string, number>[],
    numberOfMonths: number,
    loading: boolean
}

class VariableTransactionCountHistoryChart extends React.Component<VariableTransactionCountChartHistoryComponentProps, VariableTransactionCountChartHistoryComponentState> {

    constructor(props: VariableTransactionCountChartHistoryComponentProps) {
        super(props);

        this.state = {
            data: [],
            numberOfMonths: 6,
            loading: true
        };
    }

    componentDidMount() {
        this.loadVariableTransactionCountHistory();
    }

    loadVariableTransactionCountHistory() {
        this.setState({data: [], loading: true});
        if (this.props.userState.user?.id) {
            this.props.dispatchLoadVariableTransactionCountHistory({
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
                    count: balanceHistory.records[key].count
                });
            }
        }
        return records;
    }

    onChangeNumberOfMonths(value: number) {
        this.setState({numberOfMonths: value}, () => this.loadVariableTransactionCountHistory());
    }

    getTotal(): number {
        if (this.state.data.length > 0) {
            return this.state.data[this.state.data.length - 1].count;
        }
        return 0;
    }

    render() {
        return (
            <Card
                title={this.props.t('Menu.Transaction.Transactions')}
                loading={this.state.loading}
                extra={
                    <Space>
                        <Link key={'linkToCreateVariableTransaction'}
                              to={'/transactions/variable/create'}>
                            <Button
                                id={'createVariableTransactionButton'}
                                key={'createVariableTransactionButton'}
                                type="primary"
                                icon={<PlusOutlined/>}>
                                {this.props.t('Form.Button.New')}
                            </Button>
                        </Link>
                        <Select<number>
                            onChange={this.onChangeNumberOfMonths.bind(this)}
                            defaultValue={this.state.numberOfMonths}>
                            <Option value={6}>6 Months</Option>
                            <Option value={12}>1 Year</Option>
                            <Option value={24}>2 Years</Option>
                            <Option value={48}>4 Years</Option>
                        </Select>
                    </Space>
                }>
                <ChartCard
                    title={this.props.t('Statistics.History.VariableTransactionCountHistory')}
                    total={this.getTotal()}
                    bordered={false}
                    bodyStyle={{padding: 0}}>
                    <Area
                        smooth
                        xAxis={{
                            label: null,
                            grid: null
                        }}
                        yAxis={{
                            label: null,
                            grid: null
                        }}
                        autoFit
                        tooltip={{
                            title: this.props.t('Menu.Transaction.VariableTransactions')?.toString(),
                            formatter: datum => {
                                return {
                                    name: this.props.t('Statistics.Count')?.toString() || '',
                                    value: datum.count
                                };
                            }
                        }}
                        data={this.state.data}
                        xField={'date'}
                        yField={'count'}
                        height={50}
                    />
                </ChartCard>
                <VariableTransactionList
                    simpleView={true}/>
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

export default connect(mapStateToProps, statisticDispatchMap)(withTranslation<'default'>()(VariableTransactionCountHistoryChart));
