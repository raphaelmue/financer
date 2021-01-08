import React                              from 'react';
import {Amount}                           from '../../../../../.openapi';
import {Statistic}                        from 'antd';
import {WithTranslation, withTranslation} from 'react-i18next';
import {UserReducerProps}                 from '../../../../../store/reducers/user.reducers';
import {AppState}                         from '../../../../../store/reducers/root.reducers';
import {bindActionCreators, Dispatch}     from 'redux';
import {connect}                          from 'react-redux';
import AmountUtil                         from '../util';
import {userDispatchMap}                  from '../../../../../store/api/user.api';

interface AmountStatisticsComponentProps extends WithTranslation<'default'>, UserReducerProps {
    data: Amount
}

interface AmountStatisticsComponentState {
    amount: number
}


class AmountStatistics extends React.Component<AmountStatisticsComponentProps, AmountStatisticsComponentState> {

    getAmountOfData(data: Amount): number {
        return data.amount;
    }

    render() {
        return (
            <Statistic title={this.props.t('Transaction.TotalAmount')}
                       value={this.props.data.amount}
                       prefix={this.props.userState.user?.settings?.CURRENCY?.value || 'USD'}
                       valueStyle={{color: AmountUtil.getColor(this.props.data)}}
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

export default connect(mapStateToProps, userDispatchMap)(withTranslation<'default'>()(AmountStatistics));
