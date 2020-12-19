import React                              from 'react';
import {bindActionCreators, Dispatch}     from 'redux';
import {connect}                          from 'react-redux';
import {AppState}                         from '../../../../store/reducers/root.reducers';
import {withTranslation, WithTranslation} from 'react-i18next';
import {TimeRange}                        from '../../../../.openapi';
import i18next                            from 'i18next';
import {Space}                            from 'antd';

interface TimeRangeLabelComponentProps extends WithTranslation<'default'> {
    timeRange?: TimeRange
}

interface TimeRangeLabelComponentState {
}

class TimeRangeLabel extends React.Component<TimeRangeLabelComponentProps, TimeRangeLabelComponentState> {
    render() {
        if (this.props.timeRange) {
            return (
                <Space>
                    {this.props.timeRange.startDate.toLocaleDateString()}
                    -
                    {this.props.timeRange.endDate ? this.props.timeRange.endDate.toLocaleDateString() : i18next.t('Transaction.FixedTransaction.Now')}
                </Space>
            );
        }
        return <Space/>;
    }
}

const mapStateToProps = (state: AppState) => {
    return {
        userState: state.user
    };
};
const mapDispatchToProps = (dispatch: Dispatch) => bindActionCreators({}, dispatch);

export default connect(mapStateToProps, mapDispatchToProps)(withTranslation<'default'>()(TimeRangeLabel));
