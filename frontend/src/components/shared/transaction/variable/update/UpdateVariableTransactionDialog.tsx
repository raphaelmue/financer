import {Modal, notification}                                      from 'antd';
import React                                                      from 'react';
import {VariableTransaction}                                      from '../../../../../.openapi';
import {TransactionReducerProps}                                  from '../../../../../store/reducers/transaction.reducer';
import {AppState}                                                 from '../../../../../store/reducers/root.reducers';
import {connect}                                                  from 'react-redux';
import VariableTransactionDataForm, {VariableTransactionMetaData} from '../transactionData/VariableTransactionDataForm';
import {transactionDispatchMap}                                   from '../../../../../store/api/transaction.api';
import {withTranslation, WithTranslation}                         from 'react-i18next';
import {DataDialog}                                               from '../../../form/modal/data/types';

interface UpdateVariableTransactionDialogComponentProps extends DataDialog<VariableTransaction>, WithTranslation<'default'>, TransactionReducerProps {
}

interface UpdateVariableTransactionDialogComponentState {
    fixedTransactionData: VariableTransactionMetaData
    confirmLoading: boolean,
}

class UpdateVariableTransactionDialog extends React.Component<UpdateVariableTransactionDialogComponentProps, UpdateVariableTransactionDialogComponentState> {

    constructor(props: UpdateVariableTransactionDialogComponentProps) {
        super(props);

        this.state = {
            fixedTransactionData: {
                categoryId: this.props.data?.category.id || -1,
                valueDate: this.props.data?.valueDate.date || new Date,
                vendor: this.props.data?.vendor,
                description: this.props.data?.description
            },
            confirmLoading: false,
        };
    }

    onSubmit() {
        if (this.state.fixedTransactionData.categoryId !== undefined
            && this.state.fixedTransactionData.valueDate !== undefined) {
            this.setState({confirmLoading: true}, () => {
                this.updateVariableTransaction();
            });
        }
    }

    updateVariableTransaction() {
        if (this.props.data) {
            this.props.dispatchUpdateVariableTransaction({
                transactionId: this.props.data?.id,
                updateVariableTransaction: {
                    categoryId: this.state.fixedTransactionData.categoryId!,
                    valueDate: {date: this.state.fixedTransactionData.valueDate!},
                    vendor: this.state.fixedTransactionData.vendor,
                    description: this.state.fixedTransactionData.description
                }
            }, (variableTransaction) => {
                this.setState({confirmLoading: false});
                if (this.props.onSubmit) {
                    this.props.onSubmit(variableTransaction);
                }
                notification.success({
                    message: this.props.t('Transaction.VariableTransaction'),
                    description: this.props.t('Message.Transaction.VariableTransaction.UpdatedVariableTransaction')
                });
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
                <VariableTransactionDataForm
                    variableTransaction={this.props.data}
                    onChange={variableTransactionData => this.setState({fixedTransactionData: variableTransactionData})}/>
            </Modal>
        );
    }

}

const mapStateToProps = (state: AppState) => {
    return {
        transactionState: state.transaction
    };
};

export default connect(mapStateToProps, transactionDispatchMap)(withTranslation<'default'>()(UpdateVariableTransactionDialog));
