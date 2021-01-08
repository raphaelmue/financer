import * as React                                                 from 'react';
import {Avatar, Button, notification, Space, Tooltip, Typography} from 'antd';
import {LogoutOutlined, UserOutlined}                             from '@ant-design/icons';
import {withTranslation, WithTranslation}                         from 'react-i18next';
import {AppState}                                                 from '../../store/reducers/root.reducers';
import {connect}                                                  from 'react-redux';
import {UserReducerProps}                                         from '../../store/reducers/user.reducers';
import {HashRouter as Router, Link, Redirect, Route, Switch}      from 'react-router-dom';
import {userDispatchMap}                                          from '../../store/api/user.api';
import BasicLayout, {BasicLayoutProps, DefaultFooter}             from '@ant-design/pro-layout';
import menuData                                                   from './menu';
import {DeleteTokenRequest}                                       from '../../.openapi';
import Dashboard                                                  from './dashboard/Dashboard';
import Profile                                                    from './profile/Profile';
import Settings                                                   from './settings/Settings';
import VariableTransactionList
                                                                  from './transactions/variable/VariableTransactionList';
import CreateVariableTransaction
                                                                  from './transactions/variable/create/CreateVariableTransaction';
import VariableTransactionsDetails
                                                                  from './transactions/variable/details/VariableTransactionsDetails';
import AdminConfiguration                                         from './admin/configuration/AdminConfiguration';
import FixedTransactionOverview                                   from './transactions/fixed/FixedTransactionOverview';
import CreateFixedTransaction
                                                                  from './transactions/fixed/create/CreateFixedTransaction';
import {isDarkTheme}                                              from '../shared/user/settings/settingsUtil';
import FixedTransactionDetails
                                                                  from './transactions/fixed/details/FixedTransactionDetails';


import 'ant-design-pro/dist/ant-design-pro.min.css';
import '@ant-design/pro-layout/dist/layout.css';
import '@ant-design/pro-table/dist/table.css';
import '@ant-design/pro-list/dist/list.css';
import '@ant-design/pro-card/dist/card.css';
import UserManagement                                             from './admin/users/UserManagement';

if (isDarkTheme()) {
    require('antd/dist/antd.dark.css');
    require('../../styles/dark-theme.css');
}

const {Text} = Typography;

interface HomeProps extends WithTranslation<'default'>, UserReducerProps, BasicLayoutProps {
    dispatchLogout: (logoutData: DeleteTokenRequest) => void
}

interface HomeState {
}

class Home extends React.Component<HomeProps, HomeState> {

    componentDidUpdate(prevProps: Readonly<HomeProps>) {
        if (prevProps.userState.error?.message !== this.props.userState.error?.message) {
            notification.error({
                message: 'An error occurred',
                description: this.props.userState.error?.message
            });
        }
    }

    logoutUser() {
        if (this.props.userState.user?.id && this.props.userState.user?.activeToken.id) {
            this.props.dispatchDeleteToken({
                userId: this.props.userState.user?.id,
                tokenId: this.props.userState.user?.activeToken.id
            });
        }
    }

    render() {
        if (!this.props.userState.user) {
            return <Redirect to={'/authentication'}/>;
        }

        return (
            <BasicLayout
                style={{
                    overflow: 'auto',
                    height: '100vh'
                }}
                title="F I N A N C E R"
                logo={null}
                menuDataRender={() => menuData()}
                menuItemRender={(menuItemProps, defaultDom) => {
                    if (menuItemProps.path) {
                        return (<Link to={menuItemProps.path}>{defaultDom}</Link>);
                    }
                    return defaultDom;
                }}
                rightContentRender={() => (
                    <Space>
                        <Avatar shape="square" size="small" icon={<UserOutlined/>}/>
                        <Text>
                            {this.props.userState.user?.name.firstName + ' ' + this.props.userState.user?.name.surname}
                        </Text>
                        <Tooltip placement="left" title={this.props.t('logout')}>
                            <Button
                                type="text"
                                icon={<LogoutOutlined/>}
                                onClick={() => this.logoutUser()}/>
                        </Tooltip>
                    </Space>
                )}
                footerRender={() => (
                    <DefaultFooter
                        links={[
                            {key: 'financer', title: 'Financer Website', href: 'https://financer-project.org/'},
                            {key: 'github   ', title: 'GitHub', href: 'https://github.com/raphaelmue/financer/'},
                        ]}
                        copyright={'Financer Project 2020 - ' + new Date().getFullYear()}/>
                )}
                breadcrumbRender={(routers = []) => [{
                    path: '/',
                    breadcrumbName: this.props.t('Menu.Home')?.toString() || ''
                }, ...routers,]}
                itemRender={(route, params, routes) => {
                    return routes.indexOf(route) === 0 ? (<span>{route.breadcrumbName}</span>) : (
                        <Link to={route.path}>{route.breadcrumbName}</Link>
                    );
                }}
                {...this.props}>
                <Router>
                    <Switch>
                        <Route path={'/dashboard'} component={Dashboard}/>
                        <Route path={'/transactions/variable/create'} component={CreateVariableTransaction}/>
                        <Route path={'/transactions/variable/:variableTransactionId'}
                               component={VariableTransactionsDetails}/>
                        <Route path={'/transactions/variable/'} component={VariableTransactionList}/>
                        <Route path={'/transactions/fixed/create'} component={CreateFixedTransaction}/>
                        <Route path={'/transactions/fixed/:fixedTransactionId'}
                               component={FixedTransactionDetails}/>
                        <Route path={'/transactions/fixed/'} component={FixedTransactionOverview}/>
                        <Route path={'/profile/:userId?'} component={Profile}/>
                        <Route path={'/settings'} component={Settings}/>
                        <Route path={'/admin/configuration'} component={AdminConfiguration}/>
                        <Route path={'/admin/users'} component={UserManagement}/>
                    </Switch>
                </Router>
            </BasicLayout>
        );
    }
}

const mapStateToProps = (state: AppState) => {
    return {
        userState: state.user
    };
};

export default connect(mapStateToProps, userDispatchMap)(withTranslation<'default'>()(Home));
