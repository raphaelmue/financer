import React                              from 'react';
import {AppState}                         from '../../../../../store/reducers/root.reducers';
import {bindActionCreators, Dispatch}     from 'redux';
import {connect}                          from 'react-redux';
import {WithTranslation, withTranslation} from 'react-i18next';
import {Space, Tag}                       from 'antd';
import i18next                            from 'i18next';

interface FixedTransactionStatusTagComponentProps extends WithTranslation<'default'> {
    isActive?: boolean,
    hasVariableAmounts?: boolean
}

interface FixedTransactionStatusTagComponentState {
}

class FixedTransactionStatusTag extends React.Component<FixedTransactionStatusTagComponentProps, FixedTransactionStatusTagComponentState> {
    render() {
        if (this.props.isActive !== undefined && this.props.hasVariableAmounts !== undefined) {
            return (
                <Space>
                    <Tag color={this.props.isActive ? 'success' : 'default'}>
                        {this.props.isActive ? i18next.t('Transaction.FixedTransaction.Active')
                            : i18next.t('Transaction.FixedTransaction.Inactive')}
                    </Tag>
                    <Tag color={'processing'}>
                        {this.props.hasVariableAmounts ? i18next.t('Transaction.FixedTransaction.HasVariableAmounts')
                            : i18next.t('Transaction.FixedTransaction.HasFixedAmounts')}
                    </Tag>
                </Space>
            );
        }
        return (
            <Tag color={this.props.isActive ? 'success' : 'default'}>
                {this.props.isActive ? i18next.t('Transaction.FixedTransaction.Active')
                    : i18next.t('Transaction.FixedTransaction.Inactive')}
            </Tag>);
    }
}

const mapStateToProps = (state: AppState) => {
    return {
        userState: state.user
    };
};
const mapDispatchToProps = (dispatch: Dispatch) => bindActionCreators({}, dispatch);

export default connect(mapStateToProps, mapDispatchToProps)(withTranslation<'default'>()(FixedTransactionStatusTag));
