import {bindActionCreators, Dispatch}          from 'redux';
import {connect}                               from 'react-redux';
import {WithTranslation, withTranslation}      from 'react-i18next';
import React                                   from 'react';
import {UserReducerState}                      from '../../../../../store/reducers/user.reducers';
import {AppState}                              from '../../../../../store/reducers/root.reducers';
import {TransactionReducerProps}               from '../../../../../store/reducers/transaction.reducer';
import {DatePicker, Form, Input, notification} from 'antd';
import {fieldIsRequiredRule}                   from '../../../../shared/user/form/rules';
import TextArea                                from 'antd/es/input/TextArea';
import {Attachment, Product}                   from '../../../../../.openapi/models';
import ProductList                             from '../../../../shared/transaction/product/ProductList';
import CategoryTreeSelect                      from '../../../../shared/category/CategoyTreeSelect';
import CreateProductDialog                     from '../../../../shared/transaction/product/create/CreateProductDialog';
import * as api                                from '../../../../../store/api/transaction.api';
import {Redirect}                              from 'react-router-dom';
import AttachmentList                          from '../../../../shared/transaction/attachment/AttachmentList';
import CreateAttachmentDialog
                                               from '../../../../shared/transaction/attachment/create/CreateAttachmentDialog';
import StepForm, {FormStep}                    from '../../../../shared/form/step/StepForm';
import {UploadFile}                            from 'antd/lib/upload/interface';

interface CreateVariableTransactionComponentProps extends WithTranslation, UserReducerState, TransactionReducerProps {
}

interface CreateVariableTransactionComponentState {
    categoryId: number | undefined
    valueDate: Date | undefined,
    vendor: string | undefined,
    description: string | undefined,
    products: Product[],
    attachments: Attachment[],
    showProductDialog: boolean,
    showAttachmentDialog: boolean,
    redirectToTransactionList: boolean,
    currentStep: number
}

class CreateVariableTransaction extends React.Component<CreateVariableTransactionComponentProps, CreateVariableTransactionComponentState> {

    constructor(props: CreateVariableTransactionComponentProps) {
        super(props);
        this.state = {
            categoryId: undefined,
            valueDate: undefined,
            vendor: undefined,
            description: undefined,
            products: [],
            attachments: [],
            showProductDialog: false,
            showAttachmentDialog: false,
            redirectToTransactionList: false,
            currentStep: 0
        };
    }

    onSubmit() {
        if (this.state.valueDate && this.state.categoryId) {
            this.props.dispatchCreateVariableTransaction({
                createVariableTransaction: {
                    valueDate: {date: this.state.valueDate},
                    categoryId: this.state.categoryId,
                    vendor: this.state.vendor,
                    description: this.state.description,
                    products: this.state.products,
                    attachments: this.state.attachments
                }
            }, variableTransaction => {
                notification.success({
                    message: this.props.t('Transaction.VariableTransaction'),
                    description: this.props.t('Message.Transaction.VariableTransaction.CreatedVariableTransaction', {
                        category: variableTransaction.category.name,
                        categoryClass: this.props.t('Transaction.Category.CategoryClass.' + variableTransaction.category.categoryClass),
                        valueDate: variableTransaction.valueDate.date.toDateString()
                    })
                });
                this.setState({redirectToTransactionList: true});
            });
        }
    }

    onChange = (e: any) => {
        this.setState({[e.target.name]: e.target.value} as CreateVariableTransactionComponentState);
    };

    steps: FormStep[] = [
        {
            title: this.props.t('Form.Step.CreateVariableTransaction.TransactionData'),
            key: 'transactionData',
            content: () =>
                <Form
                    labelCol={{span: 6}}
                    wrapperCol={{span: 16}}
                    name={'createVariableTransaction'}>
                    <Form.Item
                        label={this.props.t('Transaction.ValueDate')}
                        name="valueDate"
                        rules={[fieldIsRequiredRule(this.props.i18n)]}>
                        <DatePicker
                            onChange={(value, dateString: string) => this.setState({valueDate: new Date(dateString)})}/>
                    </Form.Item>

                    <Form.Item
                        label={this.props.t('Transaction.Category.Name')}
                        name="categoryId"
                        rules={[fieldIsRequiredRule(this.props.i18n)]}>
                        <CategoryTreeSelect onChange={categoryId => this.setState({categoryId: categoryId})}/>
                    </Form.Item>

                    <Form.Item
                        label={this.props.t('Transaction.Vendor')}
                        name="vendor">
                        <Input
                            name={'vendor'}
                            onChange={this.onChange}/>
                    </Form.Item>
                    <Form.Item
                        label={this.props.t('Transaction.Description')}
                        name="description">
                        <TextArea
                            name={'description'}
                            onChange={this.onChange}/>
                    </Form.Item>
                </Form>,
            condition: () => (this.state.valueDate !== undefined && this.state.categoryId !== undefined)
        }, {
            title: this.props.t('Form.Step.CreateVariableTransaction.ProductData'),
            key: 'productData',
            content: () =>
                <div>
                    <ProductList
                        products={this.state.products}
                        openProductDialog={() => this.setState({showProductDialog: true})}/>
                    <CreateProductDialog
                        visible={this.state?.showProductDialog || false}
                        onCancel={() => this.setState({showProductDialog: false})}
                        onSubmit={(product: Product) => this.setState({
                            products: this.state.products.concat(product),
                            showProductDialog: false
                        })}/>
                </div>,
            condition: () => (this.state.products.length > 0)
        }, {
            title: this.props.t('Form.Step.CreateVariableTransaction.Attachments'),
            key: 'attachments',
            content: () =>
                <div>
                    <AttachmentList
                        attachments={this.state.attachments}
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
            <StepForm steps={this.steps} onSubmit={this.onSubmit.bind(this)}/>
        );
    }
}

const mapStateToProps = (state: AppState) => {
    return {
        userState: state.user,
        transactionState: state.transaction
    };
};

const mapDispatchToProps = (dispatch: Dispatch) => bindActionCreators({
    dispatchCreateVariableTransaction: api.createVariableTransaction
}, dispatch);

export default connect(mapStateToProps, mapDispatchToProps)(withTranslation()(CreateVariableTransaction));
