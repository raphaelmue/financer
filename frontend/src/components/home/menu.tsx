import {MenuDataItem}                                                                  from '@ant-design/pro-layout/es/typings';
import i18next                                                                         from 'i18next';
import {AreaChartOutlined, DollarOutlined, SettingOutlined, TagOutlined, UserOutlined} from '@ant-design/icons';
import React                                                                           from 'react';
import AdminUtils                                                                      from '../shared/admin/utils';
import store                                                                           from '../../store/store';

export default (): MenuDataItem[] => [
    {
        path: '/dashboard',
        name: i18next.t('Menu.Dashboard'),
        icon: <AreaChartOutlined/>
    }, {
        path: '/transactions',
        name: i18next.t('Menu.Transaction.Transactions'),
        icon: <DollarOutlined/>,
        children: [
            {
                path: '/transactions/variable',
                name: i18next.t('Menu.Transaction.VariableTransactions'),
                hideChildrenInMenu: true,
                children: [
                    {
                        path: '/transactions/variable/create',
                        name: i18next.t('Menu.Transaction.CreateVariableTransaction'),
                    }, {
                        path: '/transactions/variable/:variableTransactionId',
                        name: i18next.t('Menu.Transaction.TransactionDetails')
                    }
                ]
            }, {
                path: '/transactions/fixed',
                name: i18next.t('Menu.Transaction.FixedTransactions'),
                hideChildrenInMenu: true,
                children: [
                    {
                        path: '/transactions/fixed/create',
                        name: i18next.t('Menu.Transaction.CreateFixedTransaction'),
                    }, {
                        path: '/transactions/fixed/:fixedTransactionId',
                        name: i18next.t('Menu.Transaction.TransactionDetails')
                    }
                ]
            }
        ]
    }, {
        name: i18next.t('Menu.Account'),
        icon: <UserOutlined/>,
        children: [
            {
                path: '/categories',
                name: i18next.t('Menu.Categories'),
                icon: <TagOutlined/>,
            }, {
                path: '/profile',
                name: i18next.t('Menu.Profile'),
                hideChildrenInMenu: true,
                flatMenu: true,
                key: 'profile'
            }, {
                path: '/settings',
                name: i18next.t('Menu.Settings'),
                icon: <SettingOutlined/>
            }
        ]
    }, {
        path: '/admin',
        name: i18next.t('Menu.Administration'),
        hideInMenu: !AdminUtils.isUserAdmin(store.getState().user.user),
        icon: <SettingOutlined/>,
        children: [
            {
                path: '/admin/configuration',
                name: i18next.t('Menu.Admin.Configuration')
            }, {
                path: '/admin/users',
                name: i18next.t('Menu.Admin.UserManagement')
            }
        ]
    }
];
