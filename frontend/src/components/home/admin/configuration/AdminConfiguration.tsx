import React                                 from 'react';
import {AppState}                            from '../../../../store/reducers/root.reducers';
import {bindActionCreators, Dispatch}        from 'redux';
import {connect}                             from 'react-redux';
import {WithTranslation, withTranslation}    from 'react-i18next';
import {PageContainer}                       from '@ant-design/pro-layout';
import {AdminReducerProps}                   from '../../../../store/reducers/admin.reducer';
import * as api                              from '../../../../store/api/admin.api';
import ProCard                               from '@ant-design/pro-card';
import {Divider, Form, notification, Select} from 'antd';
import {fieldIsRequiredRule}                 from '../../../shared/user/form/rules';
import {
    AdminConfiguration as AdminConfigurationData,
    AdminConfigurationDefaultCurrencyEnum,
    AdminConfigurationDefaultLanguageEnum,
    UpdateAdminConfiguration
}                                            from '../../../../.openapi/models';

const {Option} = Select;


interface AdminConfigurationComponentProps extends WithTranslation<"default">, AdminReducerProps {
}

interface AdminConfigurationComponentState extends AdminConfigurationData {
}

class AdminConfiguration extends React.Component<AdminConfigurationComponentProps, AdminConfigurationComponentState> {

    constructor(props: AdminConfigurationComponentProps) {
        super(props);

        this.state = {
            defaultLanguage: this.props.adminState.configuration.defaultLanguage as AdminConfigurationDefaultLanguageEnum || AdminConfigurationDefaultLanguageEnum.En,
            defaultCurrency: this.props.adminState.configuration.defaultCurrency as AdminConfigurationDefaultCurrencyEnum || AdminConfigurationDefaultCurrencyEnum.USD,
        };

        this.props.dispatchLoadAdminConfiguration({}, (adminConfiguration) => {
            this.setState({
                defaultLanguage: adminConfiguration.defaultLanguage,
                defaultCurrency: adminConfiguration.defaultCurrency,
            });
        });
    }

    onChangeLanguage(defaultLanguage: string) {
        this.setState({defaultLanguage: defaultLanguage as AdminConfigurationDefaultLanguageEnum}, () => this.updateAdminConfiguration());
    }

    onChangeCurrency(defaultCurrency: string) {
        this.setState({defaultCurrency: defaultCurrency as AdminConfigurationDefaultCurrencyEnum}, () => this.updateAdminConfiguration());
    }

    updateAdminConfiguration() {
        this.props.dispatchUpdateAdminConfiguration({
            updateAdminConfiguration: this.state as UpdateAdminConfiguration
        }, () => {
            notification.success({
                message: this.props.t('Admin.ServerConfiguration'),
                description: this.props.t('Message.Admin.Configuration.UpdatedAdminConfiguration')
            });
        });
    }


    render() {
        return (
            <PageContainer
                loading={this.props.adminState.isLoading}>
                <ProCard bordered>
                    <Divider orientation={'left'}>{this.props.t('Menu.Admin.Configuration')}</Divider>
                    <Form
                        labelCol={{span: 6}}
                        labelAlign={'left'}
                        wrapperCol={{span: 18}}>

                        <Form.Item
                            label={this.props.t('Admin.Configuration.DefaultLanguage')}
                            name="defaultLanguage"
                            rules={[fieldIsRequiredRule()]}
                            initialValue={this.state.defaultLanguage}>
                            <Select
                                showSearch
                                filterOption={(input, option) =>
                                    option!.children.toLowerCase().indexOf(input.toLowerCase()) >= 0
                                }
                                onChange={this.onChangeLanguage.bind(this)}>
                                <Option value={'en'}>English</Option>
                                <Option value={'de'}>Deutsch</Option>
                            </Select>
                        </Form.Item>

                        <Form.Item
                            label={this.props.t('Admin.Configuration.DefaultCurrency')}
                            name="defaultCurrency"
                            rules={[fieldIsRequiredRule()]}
                            initialValue={this.state.defaultCurrency}>
                            <Select
                                showSearch
                                filterOption={(input, option) =>
                                    option!.children.toLowerCase().indexOf(input.toLowerCase()) >= 0
                                }
                                onChange={this.onChangeCurrency.bind(this)}>
                                <Option value={'USD'}>USD</Option>
                                <Option value={'EUR'}>EUR</Option>
                            </Select>
                        </Form.Item>
                    </Form>
                </ProCard>
            </PageContainer>
        );
    }
}

const mapStateToProps = (state: AppState) => {
    return {
        adminState: state.admin
    };
};

const mapDispatchToProps = (dispatch: Dispatch) => bindActionCreators({
    dispatchLoadAdminConfiguration: api.loadAdminConfiguration,
    dispatchUpdateAdminConfiguration: api.updateAdminConfiguration
}, dispatch);

export default connect(mapStateToProps, mapDispatchToProps)(withTranslation<"default">()(AdminConfiguration));
