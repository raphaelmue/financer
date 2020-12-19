import {AppState}                                           from '../../../../../store/reducers/root.reducers';
import {connect}                                            from 'react-redux';
import {transactionDispatchMap}                             from '../../../../../store/api/transaction.api';
import {WithTranslation, withTranslation}                   from 'react-i18next';
import {Modal, notification}                                from 'antd';
import React                                                from 'react';
import {DataDialog}                                         from '../../../form/modal/data/types';
import {FixedTransaction}                                   from '../../../../../.openapi';
import {TransactionReducerProps}                            from '../../../../../store/reducers/transaction.reducer';
import FixedTransactionDataForm, {FixedTransactionMetaData} from '../transactionData/FixedTransactionDataForm';

interface UpdateFixedTransactionDialogComponentProps extends WithTranslation<'default'>, DataDialog<FixedTransaction>, TransactionReducerProps {
}

interface UpdateFixedTransactionDialogComponentState {
    confirmLoading: boolean,
    fixedTransactionData: FixedTransactionMetaData
}

class UpdateFixedTransactionDialog extends React.Component<UpdateFixedTransactionDialogComponentProps, UpdateFixedTransactionDialogComponentState> {

    constructor(props: UpdateFixedTransactionDialogComponentProps) {
        super(props);

        this.state = {
            confirmLoading: false,
            fixedTransactionData: {}
        };
    }


    onSubmit() {
        this.setState({confirmLoading: true});
        if (this.props.data) {
            this.props.dispatchUpdateFixedTransaction({
                transactionId: this.props.data.id,
                updateFixedTransaction: {
                    ...this.state.fixedTransactionData
                }
            }, fixedTransaction => {
                notification.success({
                    message: this.props.t('Transaction.FixedTransactions'),
                    description: this.props.t('Message.Transaction.FixedTransaction.UpdatedFixedTransaction')
                });
                if (this.props.onSubmit) {
                    this.props.onSubmit(fixedTransaction);
                }
                this.setState({confirmLoading: false});
            });
        }
    }

    onCancel() {
        if (this.props.onCancel) {
            this.props.onCancel();
        }
    }


    render() {
        return (
            <Modal
                visible={this.props.visible}
                okText={this.props.t('Form.Button.Submit')}
                cancelText={this.props.t('Form.Button.Cancel')}
                confirmLoading={this.state.confirmLoading}
                onOk={this.onSubmit.bind(this)}
                onCancel={this.onCancel.bind(this)}>
                <FixedTransactionDataForm
                    fixedTransaction={this.props.data}
                    onChange={fixedTransactionData => this.setState({fixedTransactionData: fixedTransactionData})}/>
            </Modal>
        );
    }

}

const mapStateToProps = (state: AppState) => {
    return {
        transactionState: state.transaction
    };
};

export default connect(mapStateToProps, transactionDispatchMap)(withTranslation<'default'>()(UpdateFixedTransactionDialog));
