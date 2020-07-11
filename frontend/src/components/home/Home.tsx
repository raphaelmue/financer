import * as React                                                           from 'react';
import {Avatar, Col, Layout, Menu, Row, Space, Typography}                  from 'antd';
import {LaptopOutlined, MenuFoldOutlined, MenuUnfoldOutlined, UserOutlined} from '@ant-design/icons';
import {withTranslation, WithTranslation}                                   from 'react-i18next';
import {AppState}                                                           from '../../reducers/root.reducers';
import {connect}                                                            from 'react-redux';
import {UserReducerProps}                                                   from '../../reducers/user.reducers';
import {Redirect}                                                           from 'react-router-dom';

const {SubMenu} = Menu;
const {Header, Sider, Content} = Layout;
const {Text} = Typography;

interface HomeProps extends WithTranslation, UserReducerProps {
}

interface HomeState {
    collapsed: boolean
}

class Home extends React.Component<HomeProps, HomeState> {

    constructor(props: HomeProps) {
        super(props);
        this.state = {
            collapsed: false
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

    render() {
        if (!this.props.user.user) {
            return <Redirect to={'/login'}/>
        }

        return (
            <Layout style={{
                overflow: 'auto',
                height: '100vh'
            }}>
                <Header className="site-layout-background" style={{padding: 0}}>

                    <Row justify="space-between">
                        <Col flex={200}>
                            <Space align={'baseline'} size={'large'} direction={'horizontal'}>
                                <Text style={{color: 'white'}}> F I N A N C E R</Text>
                            </Space>
                        </Col>
                        <Col>
                            {React.createElement(this.state.collapsed ? MenuUnfoldOutlined : MenuFoldOutlined, {
                                className: 'trigger',
                                onClick: this.toggleSider,
                            })}
                        </Col>
                        <Col span={5}>
                            <Space align={'start'} size={'small'} direction={'horizontal'}>
                                <Avatar shape="square" icon={<UserOutlined/>} size="small"/>
                                <Text
                                    style={{color: 'white'}}>
                                    {this.props.user.user.name.firstName + ' ' + this.props.user.user.name.surname}
                                </Text>
                            </Space>
                        </Col>
                    </Row>
                </Header>
                <Layout>
                    <Sider breakpoint="md"
                           collapsedWidth="0">
                        <Menu mode="inline"
                              defaultSelectedKeys={['1']}
                              defaultOpenKeys={['sub1']}
                              style={{height: '100%', borderRight: 0}}>
                            <Menu.Item key="1">{this.props.t('dashboard')}</Menu.Item>
                            <SubMenu key="transactionSubMenu" icon={<UserOutlined/>}
                                     title={this.props.t('transactions')}>
                                <Menu.Item key="2">{this.props.t('Overview')}</Menu.Item>
                                <Menu.Item key="3">{this.props.t('variableTransactions')}</Menu.Item>
                                <Menu.Item key="4">{this.props.t('fixedTransactions')}</Menu.Item>
                            </SubMenu>
                            <SubMenu key="ProfileSubMenu" icon={<LaptopOutlined/>} title={this.props.t('profile')}>
                                <Menu.Item key="5">{this.props.t('categories')}</Menu.Item>
                                <Menu.Item key="6">{this.props.t('profile')}</Menu.Item>
                                <Menu.Item key="7">{this.props.t('settings')}</Menu.Item>
                            </SubMenu>
                        </Menu>
                    </Sider>

                    <Layout className="site-layout">

                        <Content style={{margin: '24px 16px 0'}}>
                            <div className="site-layout-background" style={{padding: 24, minHeight: 360}}>
                                content
                            </div>
                        </Content>
                    </Layout>
                </Layout>
            </Layout>
        )
    }
}

const mapStateToProps = (state: AppState) => {
    return {
        user: state.user
    }
}

export default connect(mapStateToProps)(withTranslation()(Home))