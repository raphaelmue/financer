import React                              from 'react';
import {AppState}                         from '../../../../store/reducers/root.reducers';
import {bindActionCreators, Dispatch}     from 'redux';
import {connect}                          from 'react-redux';
import {WithTranslation, withTranslation} from 'react-i18next';
import {PageContainer}                    from '@ant-design/pro-layout';
import {AdminReducerProps}                from '../../../../store/reducers/admin.reducer';
import * as api                           from '../../../../store/api/admin.api';
import ProCard                            from '@ant-design/pro-card';
import {Divider, Form, Select}            from 'antd';
import {fieldIsRequiredRule}              from '../../../shared/user/form/rules';

const {Option} = Select;


interface AdminConfigurationComponentProps extends WithTranslation, AdminReducerProps {
}

interface AdminConfigurationComponentState {
}

class AdminConfiguration extends React.Component<AdminConfigurationComponentProps, AdminConfigurationComponentState> {

    constructor(props: AdminConfigurationComponentProps) {
        super(props);

        this.props.dispatchLoadAdminConfiguration({});
    }


    render() {
        return (
            <PageContainer>
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
                            initialValue={this.props.adminState.configuration.defaultLanguage}>
                            <Select
                                showSearch
                                filterOption={(input, option) =>
                                    option!.children.toLowerCase().indexOf(input.toLowerCase()) >= 0
                                }>
                                <Option value={'en'}>English</Option>
                                <Option value={'de'}>Deutsch</Option>
                            </Select>
                        </Form.Item>

                        <Form.Item
                            label={this.props.t('Admin.Configuration.DefaultCurrency')}
                            name="defaultCurrency"
                            rules={[fieldIsRequiredRule()]}
                            initialValue={this.props.adminState.configuration.defaultCurrency}>
                            <Select
                                showSearch
                                filterOption={(input, option) =>
                                    option!.children.toLowerCase().indexOf(input.toLowerCase()) >= 0
                                }>
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
    dispatchLoadAdminConfiguration: api.loadAdminConfiguration
}, dispatch);

export default connect(mapStateToProps, mapDispatchToProps)(withTranslation()(AdminConfiguration));
