import {bindActionCreators, Dispatch}        from 'redux';
import {connect}                             from 'react-redux';
import {WithTranslation, withTranslation}    from 'react-i18next';
import React                                 from 'react';
import {UserReducerState}                    from '../../../../../store/reducers/user.reducers';
import {AppState}                            from '../../../../../store/reducers/root.reducers';
import {TransactionReducerProps}             from '../../../../../store/reducers/transaction.reducer';
import ProCard                               from '@ant-design/pro-card';
import {DatePicker, Form, Input, TreeSelect} from 'antd';
import {fieldIsRequiredRule}                 from '../../../../shared/user/form/rules';
import TextArea                              from 'antd/es/input/TextArea';
import {Product}                             from '../../../../../.openapi/models';
import ProductList                           from '../../../../shared/transaction/product/ProductList';

interface CreateVariableTransactionComponentProps extends WithTranslation, UserReducerState, TransactionReducerProps {
}

interface CreateVariableTransactionComponentState {
    products: Product[]
}

class CreateVariableTransaction extends React.Component<CreateVariableTransactionComponentProps, CreateVariableTransactionComponentState> {
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
                            <TreeSelect
                                showSearch
                                dropdownStyle={{maxHeight: 400, overflow: 'auto'}}
                                placeholder="Please select"
                                allowClear
                                treeData={[]}
                                treeDefaultExpandAll>
                            </TreeSelect>
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
                        products={[{id: 1, name: 'test', amount: {amount: 20}, quantity: {numberOfItems: 2}}]}
                    />
                </ProCard>
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