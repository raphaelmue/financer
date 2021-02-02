import {ProListMetas}            from '@ant-design/pro-list';
import {FixedTransaction}        from '../../../../.openapi';
import {Divider, Space}          from 'antd';
import AmountLabel               from '../../../shared/transaction/amount/amountLabel/AmountLabel';
import React                     from 'react';
import TimeRangeLabel            from '../../../shared/transaction/timeRange/TimeRangeLabel';
import FixedTransactionStatusTag from '../../../shared/transaction/fixed/status/FixedTransactionStatusTag';
import {Link}                    from 'react-router-dom';

export const metas = (): ProListMetas<FixedTransaction> => {
    return {
        title: {
            dataIndex: 'product',
            // eslint-disable-next-line react/display-name
            render: (dom, entity) => <Link to={'/transactions/fixed/' + entity.id}>{entity.product || entity.category.name}</Link>
        },
        subTitle: {
            // eslint-disable-next-line react/display-name
            render: (dom, entity) =>
                <FixedTransactionStatusTag
                    isActive={entity.active}
                    hasVariableAmounts={entity.hasVariableAmounts}/>,
        },
        description: {
            // eslint-disable-next-line react/display-name
            render: (dom, entity) => (
                <Space>
                    {entity.day}
                    <Divider type={'vertical'}/>
                    {entity.vendor}
                    <Divider type={'vertical'}/>
                    {entity.description}
                </Space>
            )
        },
        content: {
            // eslint-disable-next-line react/display-name
            render: (dom, entity) => (
                <TimeRangeLabel timeRange={entity.timeRange}/>
            )
        },
        extra: {
            // eslint-disable-next-line react/display-name
            render: ((dom, entity) => {
                if (entity.hasVariableAmounts) {
                    if (entity.transactionAmounts.length > 0) {
                        return (
                            <Space direction={'vertical'}>
                                <AmountLabel
                                    amount={entity.transactionAmounts[entity.transactionAmounts.length - 1].amount}/>
                            </Space>
                        );
                    }
                } else {
                    return (
                        <AmountLabel amount={entity.amount}/>
                    );
                }
                return (<div/>);
            })
        }
    };
};
