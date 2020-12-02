import {Modal, notification}                                      from 'antd';
import React                                                      from 'react';
import {FixedTransaction, Product, VariableTransaction}           from '../../../../../.openapi/models';
import {TransactionReducerProps}                                  from '../../../../../store/reducers/transaction.reducer';
import {AppState}                                                 from '../../../../../store/reducers/root.reducers';
import {bindActionCreators, Dispatch}                             from 'redux';
import {connect}                                                  from 'react-redux';
import VariableTransactionDataForm, {VariableTransactionMetaData} from '../transactionData/VariableTransactionDataForm';
import * as api                                                   from '../../../../../store/api/transaction.api';
import {withTranslation, WithTranslation}                         from 'react-i18next';
import {
    CreateProductRequest,
    CreateTransactionRequest,
    DeleteProductsRequest,
    DeleteVariableTransactionRequest,
    GetUsersFixedTransactionsRequest,
    GetUsersVariableTransactionsRequest,
    GetVariableTransactionByIdRequest
}                                                                 from '../../../../../.openapi/apis';

interface UpdateVariableTransactionDialogComponentProps extends WithTranslation, TransactionReducerProps {
    visible: boolean,
    variableTransaction: VariableTransaction
    onSubmit?: (variableTransaction: VariableTransaction) => void,
    onCancel?: () => void
}

interface UpdateVariableTransactionDialogComponentState {
    variableTransactionData: VariableTransactionMetaData
    confirmLoading: boolean,
}

class UpdateVariableTransactionDialog extends React.Component<UpdateVariableTransactionDialogComponentProps, UpdateVariableTransactionDialogComponentState> {

    constructor(props: UpdateVariableTransactionDialogComponentProps) {
        super(props);

        this.state = {
            variableTransactionData: {
                categoryId: this.props.variableTransaction.category.id,
                valueDate: this.props.variableTransaction.valueDate.date,
                vendor: this.props.variableTransaction.vendor,
                description: this.props.variableTransaction.description
            },
            confirmLoading: false,
        };
    }

    onSubmit() {
        if (this.state.variableTransactionData.categoryId !== undefined
            && this.state.variableTransactionData.valueDate !== undefined) {
            this.setState({confirmLoading: true}, () => {
                this.updateVariableTransaction();
            });
        }
    }

    updateVariableTransaction() {
        this.props.dispatchUpdateVariableTransaction({
            transactionId: this.props.variableTransaction.id,
            updateVariableTransaction: {
                categoryId: this.state.variableTransactionData.categoryId!,
                valueDate: {date: this.state.variableTransactionData.valueDate!},
                vendor: this.state.variableTransactionData.vendor,
                description: this.state.variableTransactionData.description
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
                    variableTransaction={this.props.variableTransaction}
                    onChange={variableTransactionData => this.setState({variableTransactionData: variableTransactionData})}/>
            </Modal>
        );
    }

}

const mapStateToProps = (state: AppState) => {
    return {
        transactionState: state.transaction
    };
};

const mapDispatchToProps = (dispatch: Dispatch) => bindActionCreators({
    dispatchUpdateVariableTransaction: api.updateVariableTransaction,
    dispatchLoadVariableTransactions: (data: GetUsersVariableTransactionsRequest) => {
    },
    dispatchLoadVariableTransaction: (data: GetVariableTransactionByIdRequest, callback?: (variableTransaction: VariableTransaction) => void) => {
    },
    dispatchCreateVariableTransaction: (data: CreateTransactionRequest, callback?: (variableTransaction: VariableTransaction) => void) => {
    },
    dispatchCreateProduct: (data: CreateProductRequest, callback?: (product: Product) => void) => {
    },
    dispatchDeleteVariableTransaction: (data: DeleteVariableTransactionRequest, callback?: () => void) => {
    },
    dispatchDeleteProducts: (data: DeleteProductsRequest, callback?: () => void) => {
    },
    dispatchLoadFixedTransactions: (data: GetUsersFixedTransactionsRequest, callback?: (fixedTransactions: FixedTransaction[]) => void) => {
    }
}, dispatch);

export default connect(mapStateToProps, mapDispatchToProps)(withTranslation()(UpdateVariableTransactionDialog));
