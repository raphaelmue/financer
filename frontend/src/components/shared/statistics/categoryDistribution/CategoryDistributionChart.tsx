import React                                    from 'react';
import {connect}                                from 'react-redux';
import {WithTranslation, withTranslation}       from 'react-i18next';
import {statisticDispatchMap}                   from '../../../../store/api/statistic.api';
import {AppState}                               from '../../../../store/reducers/root.reducers';
import {StatisticReducerProps}                  from '../../../../store/reducers/statistic.reducer';
import {Pie}                                    from '@ant-design/charts';
import {getCurrencySymbol}                      from '../../user/settings/settingsUtil';
import {UserReducerState}                       from '../../../../store/reducers/user.reducers';
import ProCard                                  from '@ant-design/pro-card';
import {GetCategoryDistributionBalanceTypeEnum} from '../../../../.openapi';
import {Select, Space, Radio}                   from 'antd';
import {CategoryDistribution}                   from '../../../../.openapi/models/CategoryDistribution';

const {Option} = Select;

interface CategoryDistributionChartComponentProps extends WithTranslation<'default'>, StatisticReducerProps {
    userState: UserReducerState
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
            numberOfMonths: 6,
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

    transformData(balanceHistory: CategoryDistribution): Record<string, any>[] {
        const records: Record<string, any>[] = [];
        if (balanceHistory.records !== undefined) {
            for (const key of Object.keys(balanceHistory.records)) {
                records.push({
                    category: key,
                    amount: balanceHistory.records[key].amount
                });
            }
        }
        console.log(records);
        return records;
    }

    onChangeNumberOfMonths(value: number) {
        this.setState({numberOfMonths: value}, () => this.loadCategoryDistribution());
    }

    render() {
        return (
            <ProCard
                title={this.props.t('Statistics.History.BalanceHistory')}
                loading={this.state.loading}
                extra={
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
                    padding={'auto'}
                    radius={1}
                    innerRadius={0.6}
                    appendPadding={10}
                    tooltip={{
                        title: this.props.t('Statistics.Balance')?.toString(),
                        formatter: datum => {
                            return {
                                name: this.props.t('Transaction.Amount')?.toString() || '',
                                value: getCurrencySymbol() + ' ' + datum.amount
                            };
                        }
                    }}
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

export default connect(mapStateToProps, statisticDispatchMap)(withTranslation<'default'>()(CategoryDistributionChart));
