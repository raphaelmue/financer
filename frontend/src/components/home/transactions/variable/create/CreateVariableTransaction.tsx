import {bindActionCreators, Dispatch}                         from 'redux';
import {connect}                                              from 'react-redux';
import {WithTranslation, withTranslation}                     from 'react-i18next';
import React                                                  from 'react';
import {UserReducerState}                                     from '../../../../../store/reducers/user.reducers';
import {AppState}                                             from '../../../../../store/reducers/root.reducers';
import {TransactionReducerProps}                              from '../../../../../store/reducers/transaction.reducer';
import ProCard                                                from '@ant-design/pro-card';
import {Button, DatePicker, Form, Input, notification, Steps} from 'antd';
import {fieldIsRequiredRule}                                  from '../../../../shared/user/form/rules';
import TextArea                                               from 'antd/es/input/TextArea';
import {Attachment, Product}                                  from '../../../../../.openapi/models';
import ProductList                                            from '../../../../shared/transaction/product/ProductList';
import CategoryTreeSelect                                     from '../../../../shared/category/CategoyTreeSelect';
import CreateProductDrawer
                                                              from '../../../../shared/transaction/product/create/CreateProductDialog';
import CreateProductDialog
                                                              from '../../../../shared/transaction/product/create/CreateProductDialog';
import {FooterToolbar}                                        from '@ant-design/pro-layout';
import * as api                                               from '../../../../../store/api/transaction.api';
import {Redirect}                                             from 'react-router-dom';
import AttachmentList
                                                              from '../../../../shared/transaction/attachment/AttachmentList';
import CreateAttachmentDialog
                                                              from '../../../../shared/transaction/attachment/create/CreateAttachmentDialog';

const {Step} = Steps;

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

    onPrevious() {
        this.setState({currentStep: this.state.currentStep - 1});
    }

    onNext() {
        this.setState({currentStep: this.state.currentStep + 1});
    }

    render() {
        if (this.state.redirectToTransactionList) {
            return <Redirect to={'/transactions/variable'}/>;
        }

        return (
            <div>
                <Steps current={this.state.currentStep}
                       style={{marginBottom: '24px'}}>
                    <Step key={'transactionData'}
                          title={this.props.t('Form.Step.CreateVariableTransaction.TransactionData')}/>
                    <Step key={'products'} title={this.props.t('Form.Step.CreateVariableTransaction.ProductData')}/>
                    <Step key={'attachments'} title={this.props.t('Form.Step.CreateVariableTransaction.Attachments')}/>
                </Steps>
                <ProCard collapsed={this.state.currentStep !== 0} bordered>
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
                    </Form>
                </ProCard>
                <ProCard collapsed={this.state.currentStep !== 1} bordered>
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
                </ProCard>
                <ProCard collapsed={this.state.currentStep !== 2} bordered>
                    <AttachmentList
                        attachments={this.state.attachments}
                        openAttachmentDialog={() => this.setState({showAttachmentDialog: true})}/>
                    <CreateAttachmentDialog
                        visible={this.state?.showAttachmentDialog || false}
                        onCancel={() => this.setState({showAttachmentDialog: false})}
                        onSubmit={(attachment: Attachment) => this.setState({
                            attachments: this.state.attachments.concat(attachment),
                            showAttachmentDialog: false
                        })}/>
                </ProCard>

                <FooterToolbar>
                    <Button
                        disabled={this.state.currentStep === 0}
                        onClick={this.onPrevious.bind(this)}>
                        {this.props.t('Form.Button.Previous')}
                    </Button>
                    <Button
                        disabled={this.state.currentStep === 2}
                        onClick={this.onNext.bind(this)}>
                        {this.props.t('Form.Button.Next')}
                    </Button>
                    <Button
                        type={'primary'}
                        disabled={!(this.state.valueDate !== undefined && this.state.categoryId !== undefined && this.state.products.length > 0)}
                        onClick={this.onSubmit.bind(this)}>
                        {this.props.t('Form.Button.Submit')}
                    </Button>
                </FooterToolbar>
            </div>
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
