import * as React                         from 'react';
import 'antd/dist/antd.css';
import {Card, Col, Row, Tabs, Typography} from 'antd';
import {Redirect}                         from 'react-router-dom';
import {connect}                          from 'react-redux';
import {AppState}                         from '../../store/reducers/root.reducers';
import {UserReducerProps}                 from '../../store/reducers/user.reducers';
import {bindActionCreators, Dispatch}     from 'redux';
import {WithTranslation, withTranslation} from 'react-i18next';
import Login                              from './login/Login';
import Register                           from './register/Register';

const {Title} = Typography;
const {TabPane} = Tabs;

interface AuthenticationComponentProps extends WithTranslation<'default'>, UserReducerProps {
}

interface AuthenticationComponentState {
}

class Authentication extends React.Component<AuthenticationComponentProps, AuthenticationComponentState> {

    render() {
        if (this.props.userState.user) {
            // redirect to main page when users is successfully logged in
            return <Redirect to={'/dashboard'}/>;
        }

        return (
            <Row justify="center" align="middle" style={{minHeight: '100vh'}}>
                <Col span={24} md={12}>
                    <Card>
                        <Typography>
                            <Title>Financer</Title>
                        </Typography>
                        <Tabs defaultActiveKey="loginTab">
                            <TabPane tab={this.props.t('login')} key="loginTab">
                                <Login/>
                            </TabPane>
                            <TabPane tab={this.props.t('register')} key="registerTab">
                                <Register/>
                            </TabPane>
                        </Tabs>
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

const mapDispatchToProps = (dispatch: Dispatch) => bindActionCreators({}, dispatch);

export default connect(mapStateToProps, mapDispatchToProps)(withTranslation<"default">()(Authentication));
