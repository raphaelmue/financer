import {UserReducerProps} from '../../../../../store/reducers/user.reducers';
import {AppState}         from '../../../../../store/reducers/root.reducers';
import {connect}          from 'react-redux';
import {ValueDate}        from '../../../../../.openapi';
import React              from 'react';
import {userDispatchMap}  from '../../../../../store/api/user.api';

interface ValueDateLabelComponentProps extends UserReducerProps {
    valueDate?: ValueDate
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
            return (<span/>);
        }
    }
}

const mapStateToProps = (state: AppState) => {
    return {
        userState: state.user
    };
};

export default connect(mapStateToProps, userDispatchMap)(ValueDateLabel);
