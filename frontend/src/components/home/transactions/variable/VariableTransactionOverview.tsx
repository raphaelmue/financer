import {PageContainer}                from '@ant-design/pro-layout';
import VariableTransactionList
                                      from '../../../shared/transaction/variable/transactionList/VariableTransactionList';
import React                          from 'react';
import {AppState}                     from '../../../../store/reducers/root.reducers';
import {bindActionCreators, Dispatch} from 'redux';
import * as categoryApi               from '../../../../store/api/category.api';
import * as userApi                   from '../../../../store/api/user.api';
import {connect}                      from 'react-redux';
import {withTranslation}              from 'react-i18next';

interface VariableTransactionOverviewComponentProps {
}

interface VariableTransactionOverviewComponentState {
}

class VariableTransactionOverview extends React.Component<VariableTransactionOverviewComponentProps, VariableTransactionOverviewComponentState> {

    render() {
        return (
            <PageContainer>
                <VariableTransactionList/>
            </PageContainer>
        );
    }


}

const mapStateToProps = () => {
    return {};
};

const mapDispatchToProps = (dispatch: Dispatch) => bindActionCreators({}, dispatch);

export default connect(mapStateToProps, mapDispatchToProps)(withTranslation<'default'>()(VariableTransactionOverview));
