import React                                             from 'react';
import {withTranslation, WithTranslation}                from 'react-i18next';
import {StatisticReducerProps}                           from '../../../../store/reducers/statistic.reducer';
import {Pie}                                             from '@ant-design/charts';
import {getCurrencySymbol}                               from '../../user/settings/settingsUtil';
import {UserState}                                       from '../../../../store/reducers/user.reducers';
import {DataSet, GetCategoryDistributionBalanceTypeEnum} from '../../../../.openapi';
import {Card, Radio, Select, Space}                      from 'antd';
import {AppState}                                        from '../../../../store/reducers/root.reducers';
import {connect}                                         from 'react-redux';
import {statisticDispatchMap}                            from '../../../../store/api/statistic.api';
import {ChartCard}                                       from 'ant-design-pro/lib/Charts';

const {Option} = Select;

interface CategoryDistributionChartComponentProps extends WithTranslation<'default'>, StatisticReducerProps {
    userState: UserState
}

interface CategoryDistributionChartComponentState {
    data: Record<string, number>[],
    balanceType: GetCategoryDistributionBalanceTypeEnum
    numberOfMonths: number,
    loading: boolean
}

class CategoryDistributionChart extends React.Component<CategoryDistributionChartComponentProps, CategoryDistributionChartComponentState> {

    constructor(props: CategoryDistributionChartComponentProps) {
        super(props);

        this.state = {
            data: [],
            balanceType: GetCategoryDistributionBalanceTypeEnum.Expenses,
            numberOfMonths: 1,
            loading: true
        };
    }

    componentDidMount() {
        this.loadCategoryDistribution();
    }

    loadCategoryDistribution() {
        this.setState({data: [], loading: true});
        if (this.props.userState.user?.id) {
            this.props.dispatchLoadCategoryDistribution({
                userId: this.props.userState.user?.id,
                balanceType: this.state.balanceType,
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
                    category: key,
                    amount: balanceHistory.records[key].amount
                });
            }
        }
        return records;
    }

    onChangeNumberOfMonths(value: number) {
        this.setState({numberOfMonths: value}, () => this.loadCategoryDistribution());
    }

    render() {
        return (
            <ChartCard
                title={this.props.t('Statistics.History.CategoryDistribution')}
                total={this.state.data.reduce((sum, value) => sum + value.amount, 0)}
                loading={this.state.loading}
                action={
                    <Space>
                        <Radio.Group
                            onChange={e => this.setState({balanceType: e.target.value}, () => this.loadCategoryDistribution())}
                            defaultValue={this.state.balanceType}>
                            <Radio.Button
                                value="expenses">{this.props.t('Transaction.Category.CategoryClass.Expenses')}</Radio.Button>
                            <Radio.Button
                                value="revenue">{this.props.t('Transaction.Category.CategoryClass.Revenue')}</Radio.Button>
                        </Radio.Group>
                        <Select<number>
                            onChange={this.onChangeNumberOfMonths.bind(this)}
                            defaultValue={this.state.numberOfMonths}>
                            <Option value={1}>1 Month</Option>
                            <Option value={3}>3 Months</Option>
                            <Option value={6}>6 Months</Option>
                            <Option value={12}>1 Year</Option>
                            <Option value={24}>2 Years</Option>
                            <Option value={48}>4 Years</Option>
                        </Select>
                    </Space>
                }>
                <Pie
                    data={this.state.data}
                    angleField={'amount'}
                    colorField={'category'}
                    radius={1}
                    innerRadius={0.7}
                    appendPadding={10}
                    statistic={{
                        title: {
                            customHtml: this.props.t('Statistics.Total')
                        },
                        content: {
                            formatter: (datum, data) => {
                                return `${getCurrencySymbol()} ${datum?.amount || data?.reduce((sum, value) => sum + value.amount, 0)}`;
                            }
                        }
                    }}
                    tooltip={{
                        title: this.props.t('Statistics.Balance')?.toString(),
                        formatter: datum => {
                            return {
                                name: this.props.t('Transaction.Amount')?.toString() || '',
                                value: `${getCurrencySymbol()} ${datum.amount}`
                            };
                        }
                    }}
                    label={{
                        type: 'inner',
                        offset: '-50%',
                        style: {textAlign: 'center'},
                        autoRotate: false,
                        content: '{value} ' + getCurrencySymbol(),
                    }}
                    interactions={[
                        {type: 'element-selected'},
                        {type: 'element-active'},
                        {type: 'pie-statistic-active'},
                    ]}
                />
            </ChartCard>
        );
    }
}

const mapStateToProps = (state: AppState) => {
    return {
        userState: state.user,
        statisticState: state.statistic
    };
};

export default connect(mapStateToProps, statisticDispatchMap)(withTranslation<'default'>()(CategoryDistributionChart));
