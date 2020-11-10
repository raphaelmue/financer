import {bindActionCreators, Dispatch}     from 'redux';
import {connect}                          from 'react-redux';
import {WithTranslation, withTranslation} from 'react-i18next';
import React, {RefObject}                 from 'react';
import {Form, Input, InputNumber, Modal}  from 'antd';
import {fieldIsRequiredRule}              from '../../../user/form/rules';
import {CreateProduct}                    from '../../../../../.openapi/models';
import {FormInstance}                     from 'antd/lib/form';

interface CreateProductDialogComponentProps extends WithTranslation {
    visible: boolean
    onSubmit?: (product: CreateProduct) => void,
    onCancel?: () => void
}

interface CreateProductDialogComponentState {
    visible: boolean,
    productName: string,
    quantity: number,
    amount: number
}

const initialState: CreateProductDialogComponentState = {
    visible: false,
    productName: '',
    quantity: 1,
    amount: 0
};

class CreateProductDialog extends React.Component<CreateProductDialogComponentProps, CreateProductDialogComponentState> {

    formRef: RefObject<FormInstance> = React.createRef();

    constructor(props: CreateProductDialogComponentProps) {
        super(props);
        this.state = initialState;
    };

    onCancel() {
        if (this.props.onCancel) {
            this.props.onCancel();
        }
    }

    onSubmit() {
        if (this.formRef.current) {
            this.formRef.current.resetFields();
        }
        if (this.props.onSubmit) {
            this.props.onSubmit({
                name: this.state.productName,
                quantity: {numberOfItems: this.state.quantity},
                amount: {amount: this.state.amount}
            });
        }
        this.setState(initialState);
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
                okText={this.props.t('Form.Button.Submit')}>
                <Form layout="vertical"
                      onFinish={this.onSubmit}
                      ref={this.formRef}>
                    <Form.Item
                        name="productName"
                        label={this.props.t('Transaction.Product.Name')}
                        initialValue={this.state.productName}
                        rules={[fieldIsRequiredRule()]}>
                        <Input
                            name="productName"
                            onChange={event => this.setState({productName: event.target.value})}/>
                    </Form.Item>
                    <Form.Item>
                        <Form.Item
                            name="quantity"
                            label={this.props.t('Transaction.Product.Quantity')}
                            rules={[fieldIsRequiredRule()]}
                            initialValue={this.state.quantity}
                            style={{display: 'inline-block', width: 'calc(50% - 6px)'}}>
                            <InputNumber
                                name="quantity"
                                min={1}
                                style={{width: '100%'}}
                                onChange={(value) => this.setState({quantity: Number(value) || 0})}/>
                        </Form.Item>
                        <span
                            style={{display: 'inline-block', width: '12px', lineHeight: '32px', textAlign: 'center'}}/>
                        <Form.Item
                            name="amount"
                            label={this.props.t('Transaction.Amount')}
                            rules={[fieldIsRequiredRule()]}
                            initialValue={this.state.amount}
                            style={{display: 'inline-block', width: 'calc(50% - 6px)'}}>
                            <InputNumber
                                name={'amount'}
                                min={0}
                                style={{width: '100%'}}
                                onChange={(value) => this.setState({amount: Number(value) || 0})}
                                formatter={value => `$ ${value}`.replace(/\B(?=(\d{3})+(?!\d))/g, ',')}/>
                        </Form.Item>
                    </Form.Item>
                </Form>
            </Modal>
        );
    }
}

const mapDispatchToProps = (dispatch: Dispatch) => bindActionCreators({}, dispatch);

export default connect(() => {
    return {};
}, mapDispatchToProps)(withTranslation()(CreateProductDialog));
