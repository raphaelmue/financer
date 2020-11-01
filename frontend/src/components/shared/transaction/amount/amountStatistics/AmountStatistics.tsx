import React                              from 'react';
import {Amount, VariableTransaction}      from '../../../../../.openapi/models';
import {Statistic}                        from 'antd';
import {withTranslation, WithTranslation} from 'react-i18next';
import {UserReducerProps}                 from '../../../../../store/reducers/user.reducers';
import {AppState}                         from '../../../../../store/reducers/root.reducers';
import {bindActionCreators, Dispatch}     from 'redux';
import {connect}                          from 'react-redux';

interface AmountStatisticsComponentProps extends WithTranslation, UserReducerProps {
    data: () => number
}

interface AmountStatisticsComponentState {
    amount: number
}

const negativeColor: string = '#cf1322';
const positiveColor: string = '#3f8600';

class AmountStatistics extends React.Component<AmountStatisticsComponentProps, AmountStatisticsComponentState> {
    constructor(props: AmountStatisticsComponentProps) {
        super(props);
    }

    getAmountOfData(data: Amount): number {
        return data.amount;
    }

    getColor(): string {
        return this.props.data() < 0 ? negativeColor : positiveColor;
    }

    render() {
        return (
            <Statistic title={this.props.t('Transaction.TotalAmount')}
                       value={this.props.data()}
                       prefix={this.props.userState.user?.settings?.CURRENCY?.value || '$'}
                       valueStyle={{color: this.getColor()}}
                       precision={2}/>
        );
    }

}

const mapStateToProps = (state: AppState) => {
    return {
        userState: state.user
    };
};

const mapDispatchToProps = (dispatch: Dispatch) => bindActionCreators({}, dispatch);

export default connect(mapStateToProps, mapDispatchToProps)(withTranslation()(AmountStatistics));

export const getAmountOfVariableTransaction = (variableTransaction: VariableTransaction): number => {
    let amount = 0;
    if (variableTransaction.products) {
        variableTransaction.products.forEach(value => {
            amount = amount + (value.amount.amount * value.quantity.numberOfItems);
        });
    }
    return amount;
};
