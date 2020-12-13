import {ProListMetas}        from '@ant-design/pro-list';
import {FixedTransaction}    from '../../../../.openapi';
import {Divider, Space, Tag} from 'antd';
import AmountLabel           from '../../../shared/transaction/amount/amountLabel/AmountLabel';
import React                 from 'react';
import i18next               from 'i18next';

export const metas = (): ProListMetas<FixedTransaction> => {
    return {
        title: {
            dataIndex: 'product',
        },
        subTitle: {
            // eslint-disable-next-line react/display-name
            render: (dom, entity) => {
                return (
                    <Space>
                        <Tag color={entity.active ? 'success' : 'default'}>
                            {entity.active ? i18next.t('Transaction.FixedTransaction.Active')
                                : i18next.t('Transaction.FixedTransaction.Inactive')}
                        </Tag>
                        <Tag color={'processing'}>
                            {entity.hasVariableAmounts ? i18next.t('Transaction.FixedTransaction.HasVariableAmounts')
                                : i18next.t('Transaction.FixedTransaction.HasFixedAmounts')}
                        </Tag>
                    </Space>);
            },
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
                <Space>
                    {entity.timeRange.startDate.toLocaleDateString()}
                    -
                    {entity.timeRange.endDate ? entity.timeRange.endDate.toLocaleDateString() : i18next.t('Transaction.FixedTransaction.Now')}
                </Space>
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
