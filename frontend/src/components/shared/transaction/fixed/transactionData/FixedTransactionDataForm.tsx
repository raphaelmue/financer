import React                                          from 'react';
import {AppState}                                     from '../../../../../store/reducers/root.reducers';
import {bindActionCreators, Dispatch}                 from 'redux';
import {connect}                                      from 'react-redux';
import {WithTranslation, withTranslation}             from 'react-i18next';
import {Amount, FixedTransaction, TimeRange}          from '../../../../../.openapi';
import {DatePicker, Form, Input, InputNumber, Switch} from 'antd';
import {fieldIsRequiredRule}                          from '../../../user/form/rules';
import moment                                         from 'moment';
import CategoryTreeSelect                             from '../../../category/select/CategoyTreeSelect';
import TextArea                                       from 'antd/es/input/TextArea';

const {RangePicker} = DatePicker;

export interface FixedTransactionMetaData {
    categoryId?: number
    amount?: Amount;
    timeRange?: TimeRange;
    product?: string;
    description?: string;
    vendor?: string;
    hasVariableAmounts?: boolean;
    day?: number;
}

interface FixedTransactionDataFormComponentProps extends WithTranslation<'default'> {
    categoryId?: number,
    fixedTransaction?: FixedTransaction
    onChange?: (fixedTransactionData: FixedTransactionMetaData) => void
}

interface FixedTransactionDataFormComponentState {
    fixedTransactionMetaData: FixedTransactionMetaData
}

class FixedTransactionDataForm extends React.Component<FixedTransactionDataFormComponentProps, FixedTransactionDataFormComponentState> {

    constructor(props: FixedTransactionDataFormComponentProps) {
        super(props);

        this.state = {
            fixedTransactionMetaData: {
                categoryId: this.props.categoryId,
                amount: this.props.fixedTransaction?.amount,
                timeRange: this.props.fixedTransaction?.timeRange,
                vendor: this.props.fixedTransaction?.vendor,
                description: this.props.fixedTransaction?.description,
                hasVariableAmounts: this.props.fixedTransaction?.hasVariableAmounts || false,
                day: this.props.fixedTransaction?.day
            }
        };
    }

    onChange(updatedMetaData: FixedTransactionMetaData) {
        if (this.props.onChange) {
            this.props.onChange(updatedMetaData);
        }
        this.setState({fixedTransactionMetaData: updatedMetaData});
    }

    render() {
        return (
            <Form
                labelCol={{span: 6}}
                wrapperCol={{span: 16}}
                name={'fixedTransactionDataForm'}>
                <Form.Item
                    label={this.props.t('Transaction.ValueDate')}
                    name="valueDate"
                    rules={[fieldIsRequiredRule()]}
                    initialValue={[moment(this.props.fixedTransaction?.timeRange.startDate),
                        moment(this.props.fixedTransaction?.timeRange.endDate)]}>
                    <RangePicker
                        allowEmpty={[false, true]}
                        onChange={(values, dateStrings) => this.onChange({
                            ...this.state.fixedTransactionMetaData,
                            timeRange: {
                                startDate: new Date(dateStrings[0]),
                                endDate: dateStrings[1] ? new Date(dateStrings[1]) : undefined
                            }
                        })}/>
                </Form.Item>
                <Form.Item
                    label={this.props.t('Transaction.Category.Name')}
                    name="categoryId"
                    rules={[fieldIsRequiredRule()]}>
                    <CategoryTreeSelect
                        filterFixed
                        categoryId={this.props.categoryId}
                        onChange={(value) => this.onChange({
                            ...this.state.fixedTransactionMetaData,
                            categoryId: value
                        })}/>
                </Form.Item>
                <Form.Item
                    label={this.props.t('Transaction.FixedTransaction.Product')}
                    name="product"
                    initialValue={this.state.fixedTransactionMetaData.vendor}>
                    <Input
                        name={'product'}
                        onChange={(event) => this.onChange({
                            ...this.state.fixedTransactionMetaData,
                            product: event.target.value
                        })}/>
                </Form.Item>
                <Form.Item
                    label={this.props.t('Transaction.FixedTransaction.HasVariableAmounts')}
                    name="hasVariableAmounts">
                    <Switch
                        checked={this.state.fixedTransactionMetaData.hasVariableAmounts || false}
                        onChange={(value) => this.onChange({
                            ...this.state.fixedTransactionMetaData,
                            hasVariableAmounts: value
                        })}/>
                </Form.Item>
                <Form.Item
                    name="amount"
                    label={this.props.t('Transaction.Amount')}
                    rules={[fieldIsRequiredRule()]}
                    initialValue={this.state.fixedTransactionMetaData.amount}>
                    <InputNumber
                        disabled={this.state.fixedTransactionMetaData.hasVariableAmounts}
                        name={'amount'}
                        style={{width: '100%'}}
                        onChange={(value) => this.onChange({
                            ...this.state.fixedTransactionMetaData,
                            amount: {amount: Number(value) || 0}
                        })}
                        formatter={value => `$ ${value}`.replace(/\B(?=(\d{3})+(?!\d))/g, ',')}/>
                </Form.Item>
                <Form.Item
                    name="day"
                    label={this.props.t('Transaction.FixedTransaction.Day')}
                    rules={[fieldIsRequiredRule()]}
                    initialValue={this.state.fixedTransactionMetaData.amount}>
                    <InputNumber
                        disabled={this.state.fixedTransactionMetaData.hasVariableAmounts}
                        name={'day'}
                        max={28}
                        min={1}
                        style={{width: '100%'}}
                        onChange={(value) => this.onChange({
                            ...this.state.fixedTransactionMetaData,
                            day: Number(value) || 1
                        })}/>
                </Form.Item>
                <Form.Item
                    label={this.props.t('Transaction.Vendor')}
                    name="vendor"
                    initialValue={this.state.fixedTransactionMetaData.vendor}>
                    <Input
                        name={'vendor'}
                        onChange={(event) => this.onChange({
                            ...this.state.fixedTransactionMetaData,
                            vendor: event.target.value
                        })}/>
                </Form.Item>
                <Form.Item
                    label={this.props.t('Transaction.Description')}
                    name="description"
                    initialValue={this.state.fixedTransactionMetaData.description}>
                    <TextArea
                        name={'description'}
                        onChange={(event) => this.onChange({
                            ...this.state.fixedTransactionMetaData,
                            description: event.target.value
                        })}/>
                </Form.Item>
            </Form>
        );
    }
}

const mapStateToProps = (state: AppState) => {
    return {};
};

const mapDispatchToProps = (dispatch: Dispatch) => bindActionCreators({}, dispatch);

export default connect(mapStateToProps, mapDispatchToProps)(withTranslation<'default'>()(FixedTransactionDataForm));
