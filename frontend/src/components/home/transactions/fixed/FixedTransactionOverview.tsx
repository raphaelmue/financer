import React                              from 'react';
import {AppState}                         from '../../../../store/reducers/root.reducers';
import {bindActionCreators, Dispatch}     from 'redux';
import {connect}                          from 'react-redux';
import {WithTranslation, withTranslation} from 'react-i18next';
import {TransactionReducerProps}          from '../../../../store/reducers/transaction.reducer';
import {PageContainer}                    from '@ant-design/pro-layout';
import CategoryTree                       from '../../../shared/category/tree/CategoryTree';
import ProCard                            from '@ant-design/pro-card';
import { Row } from 'antd';

interface FixedTransactionOverviewComponentProps extends WithTranslation, TransactionReducerProps {
}

interface FixedTransactionOverviewComponentState {
    selectedCategoryId: number | undefined
}

class FixedTransactionOverview extends React.Component<FixedTransactionOverviewComponentProps, FixedTransactionOverviewComponentState> {

    constructor(props: FixedTransactionOverviewComponentProps) {
        super(props);
        this.state = {
            selectedCategoryId: undefined
        };
    }

    render() {
        return (
            <PageContainer>
                <Row>
                    <ProCard colSpan={8} bordered>
                        <CategoryTree
                            onSelect={categoryId => this.setState({selectedCategoryId: categoryId})}/>
                    </ProCard>
                    <ProCard colSpan={16} bordered>

                    </ProCard>
                </Row>
            </PageContainer>
        );
    }


}

const mapStateToProps = (state: AppState) => {
    return {
        transactionState: state.transaction
    };
};

const mapDispatchToProps = (dispatch: Dispatch) => bindActionCreators({}, dispatch);

export default connect(mapStateToProps, mapDispatchToProps)(withTranslation()(FixedTransactionOverview));
