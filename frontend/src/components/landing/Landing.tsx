import React                                                                         from 'react';
import {connect}                                                                     from 'react-redux';
import {userDispatchMap}                                                             from '../../store/api/user.api';
import {WithTranslation, withTranslation}                                            from 'react-i18next';
import {AppState}                                                                    from '../../store/reducers/root.reducers';
import {Button, Card, Carousel, Col, Divider, Image, Layout, Row, Space, Typography} from 'antd';
import RcBannerAnim                                                                  from 'rc-banner-anim';
import {LoginOutlined, UserAddOutlined}                                              from '@ant-design/icons';
import TextyAnim                                                                     from 'rc-texty';
import {Link}                                                                        from 'react-router-dom';

import BasicLayout from '@ant-design/pro-layout';

import 'rc-banner-anim/assets/index.css';


const {Footer} = Layout;
const {Text, Title} = Typography;

interface LandingComponentProps extends WithTranslation<'default'> {
}

interface LandingComponentState {
}

class Landing extends React.Component<LandingComponentProps, LandingComponentState> {
    render() {
        return (
            <BasicLayout
                title={'F I N A N C E R'}
                logo={'images/financer-icon-64.png'}
                headerTheme={'light'}
                navTheme={'light'}
                headerHeight={64}
                layout={'top'}
                contentStyle={{margin: 0}}>
                <Row
                    style={{
                        height: 'calc(100vh - 64px)',
                        background: 'url(\'images/banner.jpg\') no-repeat',
                        backgroundSize: 'cover',
                        backgroundPosition: 'center'
                    }}
                    justify="space-around"
                    align="middle">
                    <Col md={{span: 6, order: 2}} span={22}>
                        <Space direction={'vertical'} size={'large'}>
                            <Title
                                style={{
                                    fontFamily: 'Lexend Deca',
                                    fontSize: '64px',
                                    margin: '0',
                                }}>
                                <TextyAnim> Financer </TextyAnim>
                            </Title>
                            <Text>{this.props.t('Landing.Banner.FinancerDescription')}</Text>
                            <Link to={'/authentication'}>
                                <Space direction={'horizontal'}>
                                    <Button
                                        type="primary"
                                        shape={'round'}
                                        size={'large'}
                                        icon={<LoginOutlined/>}>
                                        {this.props.t('login')}
                                    </Button>
                                    <Button
                                        shape={'round'}
                                        size={'large'}
                                        icon={<UserAddOutlined/>}>
                                        {this.props.t('register')}
                                    </Button>
                                </Space>
                            </Link>
                        </Space>
                    </Col>
                    <Col md={{span: 14, order: 1}} span={22}>
                        <Card>
                            <Carousel autoplay lazyLoad={'ondemand'}>
                                <img src={'images/screenshots/screenshot-dashboard.png'}
                                     style={{width: '100%'}}/>
                                <img src={'images/screenshots/screenshot-variable-transactions.png'}
                                     style={{width: '100%'}}/>
                                <img src={'images/screenshots/screenshot-fixed-transactions.png'}
                                     style={{width: '100%'}}/>
                            </Carousel>
                        </Card>
                    </Col>
                </Row>
                <Footer style={{backgroundColor: '#222222'}}>
                    <Row gutter={[16, 16]}>
                        <Col md={{span: 6, offset: 3}} span={24}>
                            <Space direction={'horizontal'} size={'middle'}>
                                <Image src={'images/financer-icon-full.png'} width={64}/>
                                <Title level={3}
                                       style={{fontFamily: 'Lexend Deca', color: 'white', margin: 0}}>Financer</Title>
                            </Space>
                        </Col>
                        <Col md={{span: 6}} span={24}>
                            <Space direction={'vertical'}>
                                <Title
                                    style={{color: 'white'}}
                                    level={3}>
                                    {this.props.t('Landing.Footer.Resources')}
                                </Title>
                                <Link to={'https://github.com/raphaelmue/financer'}>
                                    <Text style={{color: 'lightgray'}}>GitHub</Text>
                                </Link>
                                <Link to={'https://jenkins.raphael-muesseler.de/job/financer/'}>
                                    <Text style={{color: 'lightgray'}}>Jenkins</Text>
                                </Link>
                            </Space>
                        </Col>
                        <Col md={{span: 6}} span={24}>
                            <Space direction={'vertical'}>
                                <Title
                                    style={{color: 'white'}}
                                    level={3}>
                                    {this.props.t('Landing.Footer.Contact')}
                                </Title>
                                <Text style={{color: 'lightgray'}}>Raphael Müßeler</Text>
                                <Link to={'mailto:info@financer-project.org'}>
                                    <Text style={{color: 'lightgray'}}>info@financer-project.org</Text>
                                </Link>
                            </Space>
                        </Col>
                    </Row>
                </Footer>
            </BasicLayout>
        );
    }
}

const mapStateToProps = (state: AppState) => {
    return {
        userState: state.user
    };
};

export default connect(mapStateToProps, userDispatchMap)(withTranslation<'default'>()(Landing));
