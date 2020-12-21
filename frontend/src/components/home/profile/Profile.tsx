import * as React                         from 'react';
import {Button}                           from 'antd';
import {WithTranslation, withTranslation} from 'react-i18next';
import {AppState}                         from '../../../store/reducers/root.reducers';
import {userDispatchMap}                  from '../../../store/api/user.api';
import {connect}                          from 'react-redux';
import {UserReducerState}                 from '../../../store/reducers/user.reducers';
import {PageContainer}                    from '@ant-design/pro-layout';

interface ProfileProps extends WithTranslation<'default'>, UserReducerState {
}

interface ProfileState {
}

class Profile extends React.Component<ProfileProps, ProfileState> {

    render() {
        return (
            <PageContainer>
                <Button type="primary">
                    {this.props.t('logout')}
                </Button>
            </PageContainer>
        );
    }

}

const mapStateToProps = (state: AppState) => {
    return {
        userState: state.user
    };
};

export default connect(mapStateToProps, userDispatchMap)(withTranslation<'default'>()(Profile));
