import {WithTranslation, withTranslation} from 'react-i18next';
import {VariableTransaction}              from '../../../../../.openapi/models';
import React                          from 'react';
import {DatePicker, Form, Input}      from 'antd';
import {bindActionCreators, Dispatch} from 'redux';
import {connect}                      from 'react-redux';
import {fieldIsRequiredRule}          from '../../../user/form/rules';
import CategoryTreeSelect             from '../../../category/CategoyTreeSelect';
import TextArea                       from 'antd/es/input/TextArea';
import moment                         from 'moment';

export interface VariableTransactionMetaData {
    categoryId?: number,
    valueDate?: Date,
    vendor?: string,
    description?: string,
}

interface VariableTransactionDataFormComponentProps extends WithTranslation<'default'> {
    variableTransaction?: VariableTransaction
    onChange?: (variableTransactionData: VariableTransactionMetaData) => void
}

interface VariableTransactionDataFormComponentState {
    variableTransactionMetaData: VariableTransactionMetaData
}

class VariableTransactionDataForm extends React.Component<VariableTransactionDataFormComponentProps, VariableTransactionDataFormComponentState> {

    constructor(props: VariableTransactionDataFormComponentProps) {

        super(props);
        this.state = {
            variableTransactionMetaData: {
                categoryId: this.props.variableTransaction?.category.id,
                valueDate: this.props.variableTransaction?.valueDate.date || new Date(),
                vendor: this.props.variableTransaction?.vendor,
                description: this.props.variableTransaction?.description
            }
        };
    }

    onChange(updatedMetaData: VariableTransactionMetaData) {
        if (this.props.onChange) {
            this.props.onChange(updatedMetaData);
        }
        this.setState({variableTransactionMetaData: updatedMetaData});
    }

    render() {
        return (
            <Form
                labelCol={{span: 6}}
                wrapperCol={{span: 16}}
                name={'variableTransactionDataForm'}>
                <Form.Item
                    label={this.props.t('Transaction.ValueDate')}
                    name="valueDate"
                    rules={[fieldIsRequiredRule()]}
                    initialValue={moment(this.props.variableTransaction?.valueDate.date) || undefined}>
                    <DatePicker
                        onChange={(value, dateString: string) => this.onChange({
                            ...this.state.variableTransactionMetaData,
                            valueDate: new Date(dateString)
                        })}/>
                </Form.Item>
                <Form.Item
                    label={this.props.t('Transaction.Category.Name')}
                    name="categoryId"
                    rules={[fieldIsRequiredRule()]}>
                    <CategoryTreeSelect
                        categoryId={this.props.variableTransaction?.category.id}
                        onChange={(value) => this.onChange({
                            ...this.state.variableTransactionMetaData,
                            categoryId: value
                        })}/>
                </Form.Item>
                <Form.Item
                    label={this.props.t('Transaction.Vendor')}
                    name="vendor"
                    initialValue={this.props.variableTransaction?.vendor}>
                    <Input
                        name={'vendor'}
                        onChange={(event) => this.onChange({
                            ...this.state.variableTransactionMetaData,
                            vendor: event.target.value
                        })}/>
                </Form.Item>
                <Form.Item
                    label={this.props.t('Transaction.Description')}
                    name="description"
                    initialValue={this.props.variableTransaction?.description}>
                    <TextArea
                        name={'description'}
                        onChange={(event) => this.onChange({
                            ...this.state.variableTransactionMetaData,
                            description: event.target.value
                        })}/>
                </Form.Item>
            </Form>
        );
    }

}

const mapStateToProps = () => {
    return {};
};

const mapDispatchToProps = (dispatch: Dispatch) => bindActionCreators({}, dispatch);

export default connect(mapStateToProps, mapDispatchToProps)(withTranslation<"default">()(VariableTransactionDataForm));
