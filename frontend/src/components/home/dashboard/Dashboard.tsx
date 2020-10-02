import {AppState}                         from "../../../store/reducers/root.reducers";
import {bindActionCreators, Dispatch}     from "redux";
import * as action                        from "../../../store/api/user.api";
import {connect}                          from "react-redux";
import {WithTranslation, withTranslation} from "react-i18next";
import React                              from "react";
import {UserReducerState}                 from "../../../store/reducers/user.reducers";
import Text                               from "antd/es/typography/Text";

interface DashboardProps extends WithTranslation, UserReducerState {
}

interface DashboardState {
}

class Dashboard extends React.Component<DashboardProps, DashboardState> {

    render() {
        return (
            <Text>Dashboard</Text>
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

export default connect(mapStateToProps, mapDispatchToProps)(withTranslation()(Dashboard))
