import React                                                                         from 'react';
import {connect}                                                                     from 'react-redux';
import {userDispatchMap}                                                             from '../../store/api/user.api';
import {WithTranslation, withTranslation}                                            from 'react-i18next';
import {AppState}                                                                    from '../../store/reducers/root.reducers';
import {Button, Card, Carousel, Col, Divider, Image, Layout, Row, Space, Typography} from 'antd';
import {
    CloudOutlined,
    CodeOutlined,
    DatabaseOutlined,
    LineChartOutlined,
    LoginOutlined,
    PieChartOutlined,
    UserAddOutlined
}                                                                                    from '@ant-design/icons';
import BasicLayout                                                                   from '@ant-design/pro-layout';
import AOS                                                                           from 'aos';

import 'aos/dist/aos.css';
import './landing.css';
import Authentication
                                                                                     from '../authentication/Authentication';

AOS.init({
    duration: 800,
    easing: 'ease-out-back'
});

const {Footer} = Layout;
const {Text, Title} = Typography;

interface LandingComponentProps extends WithTranslation<'default'> {
}

interface LandingComponentState {
}

class Landing extends React.Component<LandingComponentProps, LandingComponentState> {
    loginDividerRef: React.RefObject<any>;

    constructor(props: LandingComponentProps) {
        super(props);

        this.loginDividerRef = React.createRef();
    }


    render() {
        return (
            <BasicLayout
                className={'landing'}
                title={'F I N A N C E R'}
                logo={'images/financer-icon-64.png'}
                headerTheme={'light'}
                fixedHeader
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
                                data-aos="fade-down"
                                style={{
                                    fontFamily: 'Lexend Deca',
                                    fontSize: '64px',
                                    margin: '0',
                                }}>
                                Financer
                            </Title>
                            <Text data-aos="fade-up">{this.props.t('Landing.Banner.FinancerDescription')}</Text>
                            <Space direction={'horizontal'} data-aos="fade">
                                <Button
                                    type="primary"
                                    shape={'round'}
                                    size={'large'}
                                    icon={<LoginOutlined/>}
                                    onClick={() => this.loginDividerRef.current.scrollIntoView()}>
                                    {this.props.t('login')}
                                </Button>
                                <Button
                                    shape={'round'}
                                    size={'large'}
                                    icon={<UserAddOutlined/>}
                                    onClick={() => this.loginDividerRef.current.scrollIntoView()}>
                                    {this.props.t('register')}
                                </Button>
                            </Space>
                        </Space>
                    </Col>
                    <Col md={{span: 14, order: 1}} span={22} data-aos="flip-left">
                        <Carousel
                            autoplay
                            edgeFriction={50}
                            arrows
                            touchMove
                            lazyLoad={'ondemand'}
                            style={{borderRadius: '4px'}}>
                            <img src={'images/screenshots/screenshot-dashboard.png'}
                                 style={{width: '100%'}}/>
                            <img src={'images/screenshots/screenshot-variable-transactions.png'}
                                 style={{width: '100%'}}/>
                            <img src={'images/screenshots/screenshot-fixed-transactions.png'}
                                 style={{width: '100%'}}/>
                        </Carousel>
                    </Col>
                </Row>
                <Divider orientation={'center'}>{this.props.t('Landing.Content.Features')}</Divider>
                <Row
                    style={{
                        height: 'calc(100vh - 64px)',
                        backgroundColor: 'white'
                    }}
                    justify="space-around"
                    align="middle">
                    <Col md={{span: 4}} span={22} data-aos={'fade-up'} data-aos-delay={'0'}>
                        <div className={'icon-box'}>
                            <Space size={'small'} direction={'vertical'}>
                                <CodeOutlined style={{color: '#666'}}/>
                                <Title level={5}>{this.props.t('Landing.Content.OpenSource.Title')}</Title>
                                <Text>{this.props.t('Landing.Content.OpenSource.Description')}</Text>
                            </Space>
                        </div>
                    </Col>
                    <Col md={{span: 4}} span={22} data-aos={'fade-up'} data-aos-delay={'100'}>
                        <div className={'icon-box'}>
                            <Space size={'small'} direction={'vertical'}>
                                <LineChartOutlined style={{color: '#0c7271'}}/>
                                <Title level={5}>{this.props.t('Landing.Content.Productivity.Title')}</Title>
                                <Text>{this.props.t('Landing.Content.Productivity.Description')}</Text>
                            </Space>
                        </div>
                    </Col>
                    <Col md={{span: 4}} span={22} data-aos={'fade-up'} data-aos-delay={'200'}>
                        <div className={'icon-box'}>
                            <Space size={'small'} direction={'vertical'}>
                                <PieChartOutlined style={{color: '#29b7b6'}}/>
                                <Title level={5}>{this.props.t('Landing.Content.Statistics.Title')}</Title>
                                <Text>{this.props.t('Landing.Content.Statistics.Description')}</Text>
                            </Space>
                        </div>
                    </Col>
                </Row>
                <Divider orientation={'center'}>{this.props.t('Landing.Content.GettingStarted')}</Divider>
                <Row
                    style={{
                        height: 'calc(100vh - 64px)'
                    }}
                    justify="space-around"
                    align="middle">
                    <Col md={{span: 6}} span={22} data-aos={'fade-up'} data-aos-delay={'0'}>
                        <Card className={'card-box'}>
                            <Space size={'small'} direction={'vertical'}>
                                <Title level={3}><CloudOutlined/>{this.props.t('Landing.Content.Cloud.Title')}</Title>
                                <Text>{this.props.t('Landing.Content.Cloud.Description')}</Text>
                            </Space>
                        </Card>
                    </Col>
                    <Col md={{span: 6}} span={22} data-aos={'fade-up'} data-aos-delay={'100'}>
                        <Card className={'card-box'}>
                            <Space size={'small'} direction={'vertical'}>
                                <Title level={3}><DatabaseOutlined/>{this.props.t('Landing.Content.OnPremise.Title')}
                                </Title>
                                <Text>{this.props.t('Landing.Content.OnPremise.Description')}</Text>
                            </Space>
                        </Card>
                    </Col>
                </Row>
                <Divider orientation={'center'}>{this.props.t('login')}</Divider>
                <Authentication />
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
                                <a href={'https://github.com/raphaelmue/financer'} target={'_blank'} rel={'noopener noreferrer'}>
                                    <Text style={{color: 'lightgray'}}>GitHub</Text>
                                </a>
                                <a href={'https://jenkins.raphael-muesseler.de/job/financer/'} target={'_blank'} rel={'noopener noreferrer'}>
                                    <Text style={{color: 'lightgray'}}>Jenkins</Text>
                                </a>
                                <a href={'https://sonarqube.raphael-muesseler.de/dashboard?id=financer'} target={'_blank'} rel={'noopener noreferrer'}>
                                    <Text style={{color: 'lightgray'}}>SonarQube</Text>
                                </a>
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
                                <a href={'mailto:info@financer-project.org'} >
                                    <Text style={{color: 'lightgray'}}>info@financer-project.org</Text>
                                </a>
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
