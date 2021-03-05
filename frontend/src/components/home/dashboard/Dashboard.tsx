import {AppState}                         from '../../../store/reducers/root.reducers';
import {bindActionCreators, Dispatch}     from 'redux';
import {connect}                          from 'react-redux';
import {WithTranslation, withTranslation} from 'react-i18next';
import React                              from 'react';
import {UserState}                        from '../../../store/reducers/user.reducers';
import {PageContainer}                    from '@ant-design/pro-layout';
import BalanceHistoryChart                from '../../shared/statistics/balance/BalanceHistoryChart';
import CategoryDistributionChart
                                          from '../../shared/statistics/categoryDistribution/CategoryDistributionChart';
import {Col, Row}                         from 'antd';
import VariableTransactionCountHistoryChart
                                          from '../../shared/statistics/variableTransaction/variableTransactionCount/VariableTransactionCountChart';

interface DashboardProps extends WithTranslation<'default'>, UserState {
}

interface DashboardState {
}

class Dashboard extends React.Component<DashboardProps, DashboardState> {

    render() {
        return (
            <PageContainer>
                <Row gutter={[16, 16]}>
                    <Col span={24}>
                        <BalanceHistoryChart/>
                    </Col>
                    <Col span={24} md={{span: 12}}>
                        <CategoryDistributionChart/>
                    </Col>
                    <Col span={24} md={{span: 12}}>
                        <VariableTransactionCountHistoryChart/>
                    </Col>
                </Row>
            </PageContainer>
        );
    }

}

const mapStateToProps = (state: AppState) => {
    return {
        userState: state.user
    };
};

const mapDispatchToProps = (dispatch: Dispatch) => bindActionCreators({}, dispatch);

export default connect(mapStateToProps, mapDispatchToProps)(withTranslation<'default'>()(Dashboard));
