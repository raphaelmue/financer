import {UserReducerProps}             from '../../../../../store/reducers/user.reducers';
import {AppState}                     from '../../../../../store/reducers/root.reducers';
import {bindActionCreators, Dispatch} from 'redux';
import {connect}                      from 'react-redux';
import {ValueDate}                    from '../../../../../.openapi/models';
import React                          from 'react';

interface ValueDateLabelComponentProps extends UserReducerProps {
    valueDate: ValueDate
}

interface ValueDateLabelComponentState {
}

class ValueDateLabel extends React.Component<ValueDateLabelComponentProps, ValueDateLabelComponentState> {
    render() {
        if (this.props.valueDate) {
            return (
                <span>
                    {new Date(this.props.valueDate.date).toLocaleDateString(
                        this.props.userState.user?.settings?.LANGUAGE?.value || 'en')}
                </span>
            );
        } else {
            return (<span/>)
        }
    }
}

const mapStateToProps = (state: AppState) => {
    return {
        userState: state.user
    };
};

const mapDispatchToProps = (dispatch: Dispatch) => bindActionCreators({}, dispatch);

export default connect(mapStateToProps, mapDispatchToProps)(ValueDateLabel);
