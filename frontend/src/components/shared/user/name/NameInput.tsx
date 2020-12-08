import {Form, Input}         from 'antd';
import {UserOutlined}        from '@ant-design/icons';
import * as React                         from 'react';
import {WithTranslation, withTranslation} from 'react-i18next';
import {Name}                             from '../../../../.openapi';
import {connect}                          from 'react-redux';
import {fieldIsRequiredRule} from '../form/rules';

interface NameInputComponentProps extends WithTranslation<'default'> {
    onChange: (name: Name) => void
}

interface NameInputComponentState extends Name {
}

class NameInput extends React.Component<NameInputComponentProps, NameInputComponentState> {


    onChange = (e: any) => {
        this.setState({[e.target.name]: e.target.value} as NameInputComponentState, () => {
            if (this.state.firstName && this.state.surname) {
                this.props.onChange(this.state);
            }
        });
    };

    render() {
        return (
            <div
                style={{marginBottom: 0}}>
                <Form.Item
                    name="name"
                    rules={[fieldIsRequiredRule()]}
                    style={{display: 'inline-block', width: 'calc(50% - 6px)'}}>
                    <Input prefix={<UserOutlined className="site-form-item-icon"/>}
                           type={'text'}
                           name="firstName"
                           placeholder={this.props.t('firstName')}
                           onChange={this.onChange}/>
                </Form.Item>
                <span style={{display: 'inline-block', width: '12px', lineHeight: '32px', textAlign: 'center'}}>
                    </span>
                <Form.Item
                    name="surname"
                    rules={[fieldIsRequiredRule()]}
                    style={{display: 'inline-block', width: 'calc(50% - 6px)'}}>
                    <Input prefix={<UserOutlined className="site-form-item-icon"/>}
                           type={'text'}
                           name="surname"
                           placeholder={this.props.t('surname')}
                           onChange={this.onChange}/>
                </Form.Item>
            </div>
        );
    }
}

export default connect()(withTranslation<"default">()(NameInput));
