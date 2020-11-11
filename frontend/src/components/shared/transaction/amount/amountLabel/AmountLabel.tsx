import {UserReducerProps}             from '../../../../../store/reducers/user.reducers';
import {AppState}                     from '../../../../../store/reducers/root.reducers';
import {bindActionCreators, Dispatch} from 'redux';
import {connect}                      from 'react-redux';
import {Amount}                       from '../../../../../.openapi/models';
import React                          from 'react';
import {Space, Typography}            from 'antd';
import AmountUtil                     from '../util';
import {getCurrencySymbol}            from '../../../user/settings/settingsUtil';

const {Text} = Typography;

interface AmountLabelComponentProps extends UserReducerProps {
    amount: Amount,
    disableColor?: boolean
}

interface AmountLabelComponentState {
}

class AmountLabel extends React.Component<AmountLabelComponentProps, AmountLabelComponentState> {
    render() {
        return (
            <Space size={'small'}>
                <Text type={AmountUtil.getTextType(this.props.amount)}>
                    {getCurrencySymbol()}
                </Text>
                <Text type={AmountUtil.getTextType(this.props.amount)}>
                    {this.props.amount.amount.toLocaleString(
                        this.props.userState.user?.settings?.LANGUAGE?.value || 'en',
                        {minimumFractionDigits: 2})}
                </Text>
            </Space>
        );
    }
}

const mapStateToProps = (state: AppState) => {
    return {
        userState: state.user
    };
};

const mapDispatchToProps = (dispatch: Dispatch) => bindActionCreators({}, dispatch);

export default connect(mapStateToProps, mapDispatchToProps)(AmountLabel);
