import * as React                         from 'react';
import 'antd/dist/antd.css';
import {Card, Col, Row, Tabs}             from 'antd';
import {Redirect}                         from 'react-router-dom';
import {connect}                          from 'react-redux';
import {AppState}                         from '../../store/reducers/root.reducers';
import {UserReducerProps}                 from '../../store/reducers/user.reducers';
import {WithTranslation, withTranslation} from 'react-i18next';
import Login                              from './login/Login';
import Register                           from './register/Register';
import {userDispatchMap}                  from '../../store/api/user.api';

interface AuthenticationComponentProps extends WithTranslation<'default'>, UserReducerProps {
}

interface AuthenticationComponentState {
    activeTab: string
}

class Authentication extends React.Component<AuthenticationComponentProps, AuthenticationComponentState> {

    constructor(props: AuthenticationComponentProps) {
        super(props);

        this.state = {
            activeTab: 'login'
        };
    }

    tabContent = (key: string): React.ReactNode => {
        switch (key) {
            case 'login':
                return <Login/>;
            case 'register':
                return <Register/>;
        }
        return <div/>;
    };

    render() {
        if (this.props.userState.user) {
            // redirect to main page when users is successfully logged in
            return <Redirect to={'/dashboard'}/>;
        }

        return (
            <Row
                style={{
                    height: '100vh',
                    background: 'url(\'images/banner.jpg\') no-repeat',
                    backgroundSize: 'cover',
                    backgroundPosition: 'center'
                }}
                justify="space-around"
                align="middle">
                <Col md={{span: 12}} span={22} data-aos={'fade-up'} data-aos-delay={'0'}>
                    <Card
                        tabList={[{
                            tab: this.props.t('login'),
                            key: 'login'
                        }, {
                            tab: this.props.t('register'),
                            key: 'register'
                        }]}
                        onTabChange={key => this.setState({activeTab: key})}
                        activeTabKey={this.state.activeTab}>
                        {this.tabContent(this.state.activeTab)}
                    </Card>
                </Col>
            </Row>
        );
    }
}

const mapStateToProps = (state: AppState) => {
    return {
        userState: state.user
    };
};

export default connect(mapStateToProps, userDispatchMap)(withTranslation<'default'>()(Authentication));
