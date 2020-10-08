import * as React                                                             from 'react';
import {Avatar, Breadcrumb, Button, notification, Space, Tooltip, Typography} from 'antd';
import {LogoutOutlined, UserOutlined}                                         from '@ant-design/icons';
import {withTranslation, WithTranslation}                                     from 'react-i18next';
import {AppState}                                                             from '../../store/reducers/root.reducers';
import {connect}                                                              from 'react-redux';
import {UserReducerProps}                                                     from '../../store/reducers/user.reducers';
import {HashRouter as Router, Link, Redirect, Route, Switch}                  from 'react-router-dom';
import {bindActionCreators, Dispatch}                                         from 'redux';
import * as api                                                               from '../../store/api/user.api';
import BasicLayout, {BasicLayoutProps, DefaultFooter, PageContainer}          from '@ant-design/pro-layout';
import menuData                                                               from './menu';
import {DeleteTokenRequest}                                                   from '../../.openapi/apis';
import Dashboard                                                              from './dashboard/Dashboard';
import Profile                                                                from './profile/Profile';
import Settings                                                               from './settings/Settings';
import VariableTransactionList
                                                                              from './transactions/variable/VariableTransactionList';
import '@ant-design/pro-layout/dist/layout.css';
import '@ant-design/pro-table/dist/table.css';
import '@ant-design/pro-card/dist/card.css';
import CreateVariableTransaction
                                                                              from './transactions/variable/create/CreateVariableTransaction';


const {Text} = Typography;

interface HomeProps extends WithTranslation, UserReducerProps, BasicLayoutProps {
    dispatchLogout: (logoutData: DeleteTokenRequest) => void
}

interface HomeState {
}

class Home extends React.Component<HomeProps, HomeState> {

    constructor(props: HomeProps) {
        super(props);
    }

    onChange = (e: any) => {
        this.setState({[e.target.name]: e.target.value} as HomeState);
    };

    componentDidUpdate(prevProps: Readonly<HomeProps>, prevState: Readonly<HomeState>, snapshot?: any) {
        if (prevProps.userState.error != this.props.userState.error) {
            notification.error({
                message: 'An error occurred',
                description: this.props.userState.error?.message
            });
        }
    }

    logoutUser() {
        if (this.props.userState.user?.id && this.props.userState.user?.activeToken.id) {
            this.props.dispatchLogout({
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
                loading={this.props.userState.isLoading}
                logo={null}
                menuDataRender={() => menuData()}
                menuItemRender={(menuItemProps, defaultDom) => {
                    if (menuItemProps.path) {
                        return (<Link to={menuItemProps.path}>{defaultDom}</Link>);
                    }
                    return defaultDom;
                }}
                // headerContentRender={(props) => (
                //     <PageHeaderWrapper
                //         title={false}
                //         pageHeaderRender={({ breadcrumb }: any) => (
                //             <Breadcrumb itemRender={breadcrumb.itemRender} routes={breadcrumb.routes}/>
                //         )}>
                // )}
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
                        copyright={'Financer Project 2020'}/>
                )}
                {...this.props}>
                <PageContainer>
                    <Router>
                        <Switch>
                            <Route path='/dashboard' component={Dashboard}/>
                            <Route path='/profile' component={Profile}/>
                            <Route path={'/transactions/variable/create'} component={CreateVariableTransaction}/>
                            <Route path={'/transactions/variable/'} component={VariableTransactionList}/>
                            <Route path='/settings' component={Settings}/>
                        </Switch>
                    </Router>
                </PageContainer>
            </BasicLayout>
        );
    }
}

const mapStateToProps = (state: AppState) => {
    return {
        userState: state.user
    };
};

const mapDispatchToProps = (dispatch: Dispatch) => bindActionCreators({
    dispatchLogout: api.logoutUser
}, dispatch);

export default connect(mapStateToProps, mapDispatchToProps)(withTranslation()(Home));
