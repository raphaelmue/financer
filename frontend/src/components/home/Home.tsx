import * as React                                                 from 'react';
import {Avatar, Button, notification, Space, Tooltip, Typography} from 'antd';
import {LogoutOutlined, UserOutlined}                             from '@ant-design/icons';
import {withTranslation, WithTranslation}                         from 'react-i18next';
import {AppState}                                                 from '../../store/reducers/root.reducers';
import {connect}                                                  from 'react-redux';
import {UserReducerProps}                                         from '../../store/reducers/user.reducers';
import {Link, Redirect}                                           from 'react-router-dom';
import {bindActionCreators, Dispatch}                             from "redux";
import * as action                                                from "../../store/api/user.api";
import BasicLayout, {BasicLayoutProps, PageContainer}             from "@ant-design/pro-layout";
import {menu, routes}                                             from "./routes";
import {DeleteTokenRequest}                                       from "../../.openapi/apis";
import '@ant-design/pro-layout/dist/layout.css'

const {Text} = Typography;

interface HomeProps extends WithTranslation, UserReducerProps, BasicLayoutProps {
    dispatchLogout: (logoutData: DeleteTokenRequest) => void
}

interface HomeState {
    collapsed: boolean;
    activeMenu: string;
}

class Home extends React.Component<HomeProps, HomeState> {

    constructor(props: HomeProps) {
        super(props);
        this.state = {
            collapsed: false,
            activeMenu: 'dashboard'
        };
    }

    onChange = (e: any) => {
        this.setState({[e.target.name]: e.target.value} as HomeState)
    }

    toggleSider = () => {
        this.setState({
            collapsed: !this.state.collapsed,
        });
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
            return <Redirect to={'/login'}/>
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
                menuDataRender={() => menu}
                route={() => routes}
                breadcrumbRender={(routes) => [{
                    path: '#/internal/dashboard',
                    breadcrumbName: 'Home'
                }, ...(routes || [])]}
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
                                onClick={() => this.logoutUser()}
                            />
                        </Tooltip>
                    </Space>
                )}
                {...this.props}>
                <PageContainer></PageContainer>
            </BasicLayout>
        )
    }
}

const mapStateToProps = (state: AppState) => {
    return {
        userState: state.user
    }
}

const mapDispatchToProps = (dispatch: Dispatch) => bindActionCreators({
    dispatchLogout: action.logoutUser
}, dispatch)

export default connect(mapStateToProps, mapDispatchToProps)(withTranslation()(Home))
