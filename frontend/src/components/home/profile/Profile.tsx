import * as React                         from 'react';
import {Button}                           from 'antd';
import {withTranslation, WithTranslation} from 'react-i18next';
import {AppState}                         from '../../../store/reducers/root.reducers';
import {bindActionCreators, Dispatch}     from 'redux';
import * as action                        from '../../../store/api/user.api';
import {connect}                          from 'react-redux';
import {UserReducerState}                 from '../../../store/reducers/user.reducers';

interface ProfileProps extends WithTranslation, UserReducerState {
}

interface ProfileState {
}

class Profile extends React.Component<ProfileProps, ProfileState> {

    render() {
        return (
            <Button type="primary">
                {this.props.t("logout")}
            </Button>
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

export default connect(mapStateToProps, mapDispatchToProps)(withTranslation()(Profile))
