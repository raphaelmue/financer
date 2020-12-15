import React                              from 'react';
import {bindActionCreators, Dispatch}     from 'redux';
import {connect}                          from 'react-redux';
import {WithTranslation, withTranslation} from 'react-i18next';
import {DatePicker, Form, Modal}          from 'antd';
import {fieldIsRequiredRule}              from '../../../../user/form/rules';
import AmountInput                        from '../../../amount/amountInput/amountInput';
import {Amount, FixedTransactionAmount}   from '../../../../../../.openapi';
import moment                             from 'moment';

interface CreateFixedTransactionAmountDialogComponentProps extends WithTranslation<'default'> {
    onSubmit?: (fixedTransactionAmount: FixedTransactionAmount) => Promise<void>,
    onCancel?: () => void
    visible: boolean,
    fixedTransactionAmount?: FixedTransactionAmount
}

interface CreateFixedTransactionAmountDialogComponentState {
    confirmLoading: boolean,
    fixedTransactionAmount: FixedTransactionAmount
}

class CreateFixedTransactionAmountDialog extends React.Component<CreateFixedTransactionAmountDialogComponentProps, CreateFixedTransactionAmountDialogComponentState> {

    constructor(props: CreateFixedTransactionAmountDialogComponentProps) {
        super(props);

        this.state = {
            confirmLoading: false,
            fixedTransactionAmount: props.fixedTransactionAmount || {
                id: -1,
                valueDate: {
                    date: new Date()
                },
                amount: {
                    amount: 0
                }
            }
        };
    }

    onChange(valueDate?: Date, amount?: Amount) {
        if (valueDate) {
            this.setState({
                fixedTransactionAmount: {
                    ...this.state.fixedTransactionAmount,
                    valueDate: {date: valueDate}
                }
            });
        }
        if (amount) {
            this.setState({
                fixedTransactionAmount: {
                    ...this.state.fixedTransactionAmount,
                    amount: amount
                }
            });
        }
    }

    onSubmit() {
        if (this.props.onSubmit !== undefined) {
            this.setState({confirmLoading: true});
            this.props.onSubmit(this.state.fixedTransactionAmount).then(() => {
                this.setState({confirmLoading: false});
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
                centered
                title={this.props.t('Menu.Transaction.Product.CreateProduct')}
                visible={this.props.visible}
                onOk={this.onSubmit.bind(this)}
                onCancel={this.onCancel.bind(this)}
                cancelText={this.props.t('Form.Button.Cancel')}
                keyboard={true}
                destroyOnClose={true}
                confirmLoading={this.state.confirmLoading}
                okText={this.props.t('Form.Button.Submit')}>
                <Form name={'fixedTransactionDialog'}
                      layout={'vertical'}
                      onFinish={this.onSubmit}>
                    <Form.Item
                        name="valueDate"
                        label={this.props.t('Transaction.ValueDate')}
                        initialValue={moment(this.state.fixedTransactionAmount.valueDate.date)}
                        rules={[fieldIsRequiredRule()]}>
                        <DatePicker
                            name="valueDate"
                            onChange={(date, dateString) => this.onChange(new Date(dateString))}/>
                    </Form.Item>
                    <Form.Item>
                        <Form.Item
                            name="amount"
                            label={this.props.t('Transaction.Amount')}
                            rules={[fieldIsRequiredRule()]}
                            initialValue={this.state.fixedTransactionAmount.amount}>
                            <AmountInput
                                amount={this.state.fixedTransactionAmount.amount}
                                onChange={amount => this.onChange(undefined, amount)}/>
                        </Form.Item>
                    </Form.Item>
                </Form>
            </Modal>
        );
    }
}

export default connect(() => {
    return {};
}, (dispatch: Dispatch) => bindActionCreators({}, dispatch))(withTranslation<'default'>()(CreateFixedTransactionAmountDialog));
