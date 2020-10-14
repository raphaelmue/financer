import {bindActionCreators, Dispatch}     from 'redux';
import {connect}                          from 'react-redux';
import {WithTranslation, withTranslation} from 'react-i18next';
import React                              from 'react';
import {UserReducerState}                 from '../../../../../store/reducers/user.reducers';
import {AppState}                         from '../../../../../store/reducers/root.reducers';
import {TransactionReducerProps}          from '../../../../../store/reducers/transaction.reducer';
import ProCard                            from '@ant-design/pro-card';
import {Button, DatePicker, Form, Input}  from 'antd';
import {fieldIsRequiredRule}              from '../../../../shared/user/form/rules';
import TextArea                           from 'antd/es/input/TextArea';
import {Product}                          from '../../../../../.openapi/models';
import ProductList                        from '../../../../shared/transaction/product/ProductList';
import CategoryTreeSelect                 from '../../../../shared/category/CategoyTreeSelect';
import CreateProductDrawer                from '../../../../shared/transaction/product/create/CreateProductDrawer';
import {FooterToolbar}                    from '@ant-design/pro-layout';

interface CreateVariableTransactionComponentProps extends WithTranslation, UserReducerState, TransactionReducerProps {
}

interface CreateVariableTransactionComponentState {
    categoryId: number | undefined
    products: Product[],
    showProductDialog: boolean
}

class CreateVariableTransaction extends React.Component<CreateVariableTransactionComponentProps, CreateVariableTransactionComponentState> {

    constructor(props: CreateVariableTransactionComponentProps) {
        super(props);
        this.state = {
            categoryId: undefined,
            showProductDialog: false,
            products: []
        };
    }

    onSubmit() {
    }

    render() {
        return (
            <div>
                <ProCard bordered>
                    <Form
                        labelCol={{span: 6}}
                        wrapperCol={{span: 16}}
                        name={'createVariableTransaction'}>
                        <Form.Item
                            label={this.props.t('Transaction.ValueDate')}
                            name="valueDate"
                            rules={[fieldIsRequiredRule(this.props.i18n)]}>
                            <DatePicker/>
                        </Form.Item>

                        <Form.Item
                            label={this.props.t('Transaction.Category.Name')}
                            name="password"
                            rules={[fieldIsRequiredRule(this.props.i18n)]}>
                            <CategoryTreeSelect onChange={categoryId => this.setState({categoryId: categoryId})}/>
                        </Form.Item>

                        <Form.Item
                            label={this.props.t('Transaction.Vendor')}
                            name="vendor">
                            <Input/>
                        </Form.Item>
                        <Form.Item
                            label={this.props.t('Transaction.Description')}
                            name="description">
                            <TextArea/>
                        </Form.Item>
                    </Form>
                </ProCard>
                <ProCard bordered style={{marginTop: 8}}>
                    <ProductList
                        products={this.state.products}
                        openProductDialog={() => this.setState({showProductDialog: true})}/>
                    <CreateProductDrawer
                        visible={this.state?.showProductDialog || false}
                        onCancel={() => this.setState({showProductDialog: false})}
                        onSubmit={(product: Product) => this.setState({
                            products: this.state.products.concat(product),
                            showProductDialog: false
                        })}/>
                </ProCard>

                <FooterToolbar>
                    <Button type={'primary'} onClick={this.onSubmit}>{this.props.t('Form.Button.Submit')}</Button>
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

const mapDispatchToProps = (dispatch: Dispatch) => bindActionCreators({}, dispatch);

export default connect(mapStateToProps, mapDispatchToProps)(withTranslation()(CreateVariableTransaction));
