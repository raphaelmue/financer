import {AppState}                     from '../../../store/reducers/root.reducers';
import {bindActionCreators, Dispatch} from 'redux';
import {connect}                          from 'react-redux';
import {WithTranslation, withTranslation} from 'react-i18next';
import React                              from 'react';
import {UserReducerState}             from '../../../store/reducers/user.reducers';

interface SettingsComponentProps extends WithTranslation<'default'>, UserReducerState {
}

interface SettingsComponentState {
}

class Settings extends React.Component<SettingsComponentProps, SettingsComponentState> {
    render() {
        return (
            <div></div>
        );
    }
}

const mapStateToProps = (state: AppState) => {
    return {
        userState: state.user
    };
};

const mapDispatchToProps = (dispatch: Dispatch) => bindActionCreators({}, dispatch);

export default connect(mapStateToProps, mapDispatchToProps)(withTranslation<"default">()(Settings));
