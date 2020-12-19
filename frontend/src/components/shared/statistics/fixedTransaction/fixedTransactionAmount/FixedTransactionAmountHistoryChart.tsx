import React                              from 'react';
import {ChartProps, StatisticsComponent}  from '../../types';
import {FixedTransactionAmount}           from '../../../../../.openapi';
import {withTranslation, WithTranslation} from 'react-i18next';
import {Line}                             from '@ant-design/charts';
import {bindActionCreators, Dispatch}     from 'redux';
import {connect}                          from 'react-redux';
import {getCurrencySymbol}                from '../../../user/settings/settingsUtil';

interface FixedTransactionAmountHistoryChartComponentProps extends ChartProps<FixedTransactionAmount>, WithTranslation<'default'> {
}

interface FixedTransactionAmountHistoryChartComponentState {
}

class FixedTransactionAmountHistoryChart extends StatisticsComponent<FixedTransactionAmount, FixedTransactionAmountHistoryChartComponentProps, FixedTransactionAmountHistoryChartComponentState> {

    transformData(): Record<string, any>[] {
        return this.props.data?.map((value) => {
            return {
                date: value.valueDate.date.toLocaleDateString(),
                amount: value.amount.amount
            };
        }) || [];
    }

    render() {
        return (
            <Line
                tooltip={{
                    title: this.props.t('Transaction.FixedTransactionAmount')?.toString(),
                    formatter: datum => {
                        return {
                            name: this.props.t('Transaction.Amount')?.toString() || '',
                            value: getCurrencySymbol() + ' ' + datum.amount
                        };
                    }
                }}
                smooth={true}
                data={this.transformData()}
                yAxis={{
                    title: {
                        text: this.props.t('Transaction.Amount')?.toString() || ''
                    },
                    nice: true,
                    label: {
                        formatter: datum => {
                            console.log({datum: getCurrencySymbol() + ' ' + datum});
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
                height={400}
                style={{height: '400px'}}
            />
        );
    }

}


const mapStateToProps = () => {
    return {};
};

const mapDispatchToProps = (dispatch: Dispatch) => bindActionCreators({}, dispatch);

export default connect(mapStateToProps, mapDispatchToProps)(withTranslation<'default'>()(FixedTransactionAmountHistoryChart));
