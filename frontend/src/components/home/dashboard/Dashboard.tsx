import {AppState}                     from '../../../store/reducers/root.reducers';
import {bindActionCreators, Dispatch} from 'redux';
import * as action                    from '../../../store/api/user.api';
import {connect}                          from 'react-redux';
import {WithTranslation, withTranslation} from 'react-i18next';
import React                              from 'react';
import {UserReducerState}             from '../../../store/reducers/user.reducers';
import {Typography}                   from 'antd';
import {PageContainer}                from '@ant-design/pro-layout';

const {Text} = Typography;

interface DashboardProps extends WithTranslation<'default'>, UserReducerState {
}

interface DashboardState {
}

class Dashboard extends React.Component<DashboardProps, DashboardState> {

    render() {
        return (
            <PageContainer>
                <Text>Dashboard</Text>
            </PageContainer>
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

export default connect(mapStateToProps, mapDispatchToProps)(withTranslation<"default">()(Dashboard))
