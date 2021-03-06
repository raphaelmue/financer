import {connect}                                                  from 'react-redux';
import {WithTranslation, withTranslation}                         from 'react-i18next';
import React                                                      from 'react';
import {AppState}                                                 from '../../../../../store/reducers/root.reducers';
import {TransactionReducerProps}                                  from '../../../../../store/reducers/transaction.reducer';
import {notification}                                             from 'antd';
import {CreateAttachment, CreateProduct}                          from '../../../../../.openapi';
import ProductList
                                                                  from '../../../../shared/transaction/product/ProductList';
import CreateProductDialog
                                                                  from '../../../../shared/transaction/product/create/CreateProductDialog';
import {transactionDispatchMap}                                   from '../../../../../store/api/transaction.api';
import {Redirect}                                                 from 'react-router-dom';
import AttachmentList
                                                                  from '../../../../shared/transaction/attachment/AttachmentList';
import CreateAttachmentDialog
                                                                  from '../../../../shared/transaction/attachment/create/CreateAttachmentDialog';
import StepForm, {FormStep}                                       from '../../../../shared/form/step/StepForm';
import {PageContainer}                                            from '@ant-design/pro-layout';
import VariableTransactionDataForm, {VariableTransactionMetaData} from '../../../../shared/transaction/variable/transactionData/VariableTransactionDataForm';

interface CreateVariableTransactionComponentProps extends WithTranslation<'default'>, TransactionReducerProps {
}

interface CreateVariableTransactionComponentState {
    fixedTransactionData: VariableTransactionMetaData,
    products: CreateProduct[],
    attachments: CreateAttachment[],
    showProductDialog: boolean,
    showAttachmentDialog: boolean,
    redirectToTransactionList: boolean,
    currentStep: number
}

class CreateVariableTransaction extends React.Component<CreateVariableTransactionComponentProps, CreateVariableTransactionComponentState> {

    constructor(props: CreateVariableTransactionComponentProps) {
        super(props);
        this.state = {
            fixedTransactionData: {},
            products: [],
            attachments: [],
            showProductDialog: false,
            showAttachmentDialog: false,
            redirectToTransactionList: false,
            currentStep: 0
        };
    }

    onSubmit() {
        if (this.state.fixedTransactionData.valueDate && this.state.fixedTransactionData.categoryId) {
            this.props.dispatchCreateVariableTransaction({
                createVariableTransaction: {
                    valueDate: {date: this.state.fixedTransactionData.valueDate},
                    categoryId: this.state.fixedTransactionData.categoryId,
                    vendor: this.state.fixedTransactionData.vendor,
                    description: this.state.fixedTransactionData.description,
                    products: this.state.products,
                    attachments: this.state.attachments
                }
            }, (variableTransaction) => {
                this.setState({redirectToTransactionList: true}, () => notification.success({
                    message: this.props.t('Transaction.VariableTransaction')?.toString(),
                    description: this.props.t('Message.Transaction.VariableTransaction.CreatedVariableTransaction', {
                        category: variableTransaction.category.name,
                        categoryClass: this.props.t('Transaction.Category.CategoryClass.' + variableTransaction.category.categoryClass.valueOf())?.toString(),
                        valueDate: variableTransaction.valueDate.date.toDateString()
                    })?.toString()
                }));
            });
        }
    }

    onDeleteProducts(productIds: number[]): Promise<void> {
        return new Promise<void>((resolve) => {
            const products: CreateProduct[] = this.state.products;
            productIds.forEach(value => products.splice(value, 1));
            this.setState({products: products});
            resolve();
        });
    }

    onChange = (e: any) => {
        this.setState({[e.target.name]: e.target.value} as CreateVariableTransactionComponentState);
    };

    steps: FormStep[] = [{
        title: this.props.t('Form.Step.CreateVariableTransaction.TransactionData'),
        key: 'transactionData',
        content: () =>
            <VariableTransactionDataForm
                onChange={variableTransactionData => this.setState({fixedTransactionData: variableTransactionData})}/>,
        condition: () => (this.state.fixedTransactionData.valueDate !== undefined && this.state.fixedTransactionData.categoryId !== undefined)
    }, {
        title: this.props.t('Form.Step.CreateVariableTransaction.ProductData'),
        key: 'productData',
        content: () =>
            <div>
                <ProductList
                    products={this.state.products.map((value: CreateProduct, index) => {
                        return {
                            id: index,
                            quantity: value.quantity,
                            amount: value.amount,
                            name: value.name,
                            totalAmount: {amount: value.quantity.numberOfItems * value.amount.amount}
                        };
                    })}
                    openProductDialog={() => this.setState({showProductDialog: true})}
                    onDeleteProducts={(productIds) => this.onDeleteProducts(productIds)}/>
                <CreateProductDialog
                    visible={this.state?.showProductDialog || false}
                    onCancel={() => this.setState({showProductDialog: false})}
                    onSubmit={(product: CreateProduct) => new Promise<void>(
                        (resolve) => this.setState({
                            products: this.state.products.concat(product),
                            showProductDialog: false
                        }, () => resolve())
                    )}/>
            </div>,
        condition: () => (this.state.products.length > 0)
    }, {
        title: this.props.t('Form.Step.CreateVariableTransaction.Attachments'),
        key: 'attachments',
        content: () =>
            <div>
                <AttachmentList
                    openAttachmentDialog={() => this.setState({showAttachmentDialog: true})}/>
                <CreateAttachmentDialog
                    visible={this.state?.showAttachmentDialog || false}
                    onCancel={() => this.setState({showAttachmentDialog: false})}
                    // onSubmit={(attachment: UploadFile[]) => this.setState({
                    //     attachments: this.state.attachments.concat(attachment),
                    //     showAttachmentDialog: false
                    // })}
                />
            </div>
    }];

    render() {
        if (this.state.redirectToTransactionList) {
            return <Redirect to={'/transactions/variable'}/>;
        }

        return (
            <PageContainer><StepForm steps={this.steps} onSubmit={this.onSubmit.bind(this)}/></PageContainer>
        );
    }
}

const mapStateToProps = (state: AppState) => {
    return {
        transactionState: state.transaction
    };
};

export default connect(mapStateToProps, transactionDispatchMap)(withTranslation<'default'>()(CreateVariableTransaction));
