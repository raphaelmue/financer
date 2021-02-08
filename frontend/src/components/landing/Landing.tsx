import React                                                         from 'react';
import {connect}                                                     from 'react-redux';
import {userDispatchMap}                                             from '../../store/api/user.api';
import {WithTranslation, withTranslation}                            from 'react-i18next';
import {AppState}                                                    from '../../store/reducers/root.reducers';
import {Button, Col, Divider, Image, Layout, Row, Space, Typography} from 'antd';
import RcBannerAnim                                                  from 'rc-banner-anim';
import {LoginOutlined, UserAddOutlined}                              from '@ant-design/icons';

import 'rc-banner-anim/assets/index.css';
import TextyAnim                                                     from 'rc-texty';
import {Link}                                                        from 'react-router-dom';


const {Header, Content, Footer} = Layout;
const {Text, Title} = Typography;
const {Element} = RcBannerAnim;

interface LandingComponentProps extends WithTranslation<'default'> {
}

interface LandingComponentState {
}

class Landing extends React.Component<LandingComponentProps, LandingComponentState> {
    render() {
        return (
            <Layout>
                <Header
                    title={'F I N A N C E R'}>

                </Header>
                <Content>
                    <RcBannerAnim
                        style={{height: 'calc(100vh - 64px)'}}
                        autoPlay>
                        <Element>
                            <div style={{
                                background: 'url(\'images/banner.jpg\') no-repeat',
                                backgroundPosition: 'center',
                                backgroundSize: 'cover',
                                height: '100%'
                            }}>
                                <div style={{
                                    padding: '20px',
                                    width: '100%',
                                    position: 'absolute',
                                    top: '50%',
                                    transform: 'translateY(-50%)'
                                }}>
                                    <Row>
                                        <Col md={{
                                            offset: 14,
                                            span: 6
                                        }} offset={0} span={24}>
                                            <Space direction={'vertical'} size={'large'}>
                                                <Title
                                                    style={{
                                                        fontFamily: 'Lexend Deca',
                                                        fontSize: '64px',
                                                        margin: '0',
                                                    }}>
                                                    <TextyAnim> Financer </TextyAnim>
                                                </Title>
                                                <Divider type={'horizontal'} dashed/>
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
                                    </Row>
                                </div>
                            </div>
                        </Element>
                        {/*<Element>*/}
                        {/*    <div style={{*/}
                        {/*        padding: '150px',*/}
                        {/*        backgroundPosition: 'center',*/}
                        {/*        backgroundSize: 'cover',*/}
                        {/*        height: '100%'*/}
                        {/*    }}>*/}
                        {/*        <Title>F I N A N C E R 2</Title>*/}
                        {/*    </div>*/}
                        {/*</Element>*/}
                    </RcBannerAnim>
                    <div>

                    </div>
                </Content>
                <Footer style={{backgroundColor: '#222222'}}>
                    <Row>
                        <Col md={{span: 8}} span={24}>
                            <Space direction={'horizontal'} size={'middle'}>
                                <Image src={'images/financer-icon-full.png'} width={64}/>
                                <Title level={3} style={{fontFamily: 'Lexend Deca', color: 'white', margin: 0}}>Financer</Title>
                            </Space>
                        </Col>
                        <Col md={{span: 8}} span={24}>

                        </Col>
                        <Col md={{span: 8}} span={24}>

                        </Col>
                    </Row>
                </Footer>
            </Layout>
        );
    }
}

const mapStateToProps = (state: AppState) => {
    return {
        userState: state.user
    };
};

export default connect(mapStateToProps, userDispatchMap)(withTranslation<'default'>()(Landing));
