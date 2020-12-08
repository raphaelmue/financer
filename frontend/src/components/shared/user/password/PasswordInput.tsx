import * as React                         from 'react';
import {Form, Input}                      from 'antd';
import {WithTranslation, withTranslation} from 'react-i18next';
import {connect}                          from 'react-redux';
import {LockOutlined}                     from '@ant-design/icons';
import hash                               from 'js-sha256';
import {HashedPassword}                   from '../../../../.openapi';

interface PasswordInputComponentProps extends WithTranslation<'default'> {
    onChange: (password: HashedPassword) => void;
}

interface PasswordInputComponentState {
    salt: string;
    password: string;
    repeatedPassword: string;
}

const characters = 'ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789';

class PasswordInput extends React.Component<PasswordInputComponentProps, PasswordInputComponentState> {

    constructor(props: PasswordInputComponentProps) {
        super(props);

        this.state = {
            salt: this.generateSalt(),
            password: '',
            repeatedPassword: ''
        };
    }

    onChange = (e: any) => {
        this.setState({[e.target.name]: e.target.value} as PasswordInputComponentState, () => {
            if (this.state.password && this.state.repeatedPassword && this.state.password === this.state.repeatedPassword) {
                this.props.onChange({
                    salt: this.state.salt,
                    hashedPassword: hash.sha256(this.state.password)
                });
            }
        });
    };

    render() {
        return (
            <div>
                <Form.Item
                    name="password"
                    rules={[{required: true, message: 'Please input your Password!'}]}>
                    <Input
                        prefix={<LockOutlined className="site-form-item-icon"/>}
                        type="password"
                        name="password"
                        placeholder={this.props.t('password')}
                        onChange={this.onChange}/>
                </Form.Item>
                <Form.Item
                    name="repeatPassword"
                    rules={[{required: true, message: 'Please input your Password!'}]}>
                    <Input
                        prefix={<LockOutlined className="site-form-item-icon"/>}
                        type="password"
                        name="repeatedPassword"
                        placeholder={this.props.t('Register.repeatPassword')}
                        onChange={this.onChange}/>
                </Form.Item>
            </div>);
    }

    private generateSalt(length = 32) {
        let result = '';
        const charactersLength = characters.length;
        for (let i = 0; i < length; i++) {
            result += characters.charAt(Math.floor(Math.random() * charactersLength));
        }
        return result;
    }
}

export default connect()(withTranslation<'default'>()(PasswordInput));
