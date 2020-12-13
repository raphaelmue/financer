import {Modal, notification}                                      from 'antd';
import React                                                      from 'react';
import {
    CreateProductRequest,
    CreateTransactionRequest,
    DeleteProductsRequest,
    DeleteVariableTransactionRequest,
    GetUsersVariableTransactionsRequest,
    GetVariableTransactionByIdRequest,
    Product,
    VariableTransaction
}                                                                 from '../../../../../.openapi';
import {TransactionReducerProps}                                  from '../../../../../store/reducers/transaction.reducer';
import {AppState}                                                 from '../../../../../store/reducers/root.reducers';
import {bindActionCreators, Dispatch}                             from 'redux';
import {connect}                                                  from 'react-redux';
import VariableTransactionDataForm, {VariableTransactionMetaData} from '../transactionData/VariableTransactionDataForm';
import * as api                                                   from '../../../../../store/api/transaction.api';
import {WithTranslation, withTranslation}                         from 'react-i18next';

interface UpdateVariableTransactionDialogComponentProps extends WithTranslation<'default'>, TransactionReducerProps {
    visible: boolean,
    variableTransaction: VariableTransaction,
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
        return;
    },
    dispatchLoadVariableTransaction: (data: GetVariableTransactionByIdRequest, callback?: (variableTransaction: VariableTransaction) => void) => {
        return;
    },
    dispatchCreateVariableTransaction: (data: CreateTransactionRequest, callback?: (variableTransaction: VariableTransaction) => void) => {
        return;
    },
    dispatchCreateProduct: (data: CreateProductRequest, callback?: (product: Product) => void) => {
        return;
    },
    dispatchDeleteVariableTransaction: (data: DeleteVariableTransactionRequest, callback?: () => void) => {
        return;
    },
    dispatchDeleteProducts: (data: DeleteProductsRequest, callback?: () => void) => {
        return;
    }
}, dispatch);

export default connect(mapStateToProps, mapDispatchToProps)(withTranslation<"default">()(UpdateVariableTransactionDialog));
