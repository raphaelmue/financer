import {MenuDataItem, Route} from '@ant-design/pro-layout/lib/typings';
import i18next               from 'i18next';

export const routes = (): Route[] => [{
    path: '#/internal',
    name: 'Home',
    routes: [
        {
            path: '/dashboard',
            name: i18next.t('Menu.Dashboard'),
            component: './dashboard/Dashboard'
        }, {
            path: '/transactions',
            name: i18next.t('Menu.Transactions'),
            icon: '',
            routes: [
                {
                    path: '/variable',
                    name: i18next.t('Menu.VariableTransactions'),
                    icon: '',
                }, {
                    path: '/fixed',
                    name: i18next.t('Menu.FixedTransactions'),
                    icon: '',
                    component: './Home',
                }, {
                    path: '/categories',
                    name: i18next.t('Menu.Categories'),
                    icon: '',
                }, {
                    path: '/profile',
                    name: i18next.t('Menu.Profile'),
                    icon: '',
                }, {
                    path: '/settings',
                    name: i18next.t('Menu.Settings'),
                    icon: '',
                }
            ]
        }
    ]
}];

export const menu = (): MenuDataItem[] => [
    {
        path: '/internal/dashboard',
        name: i18next.t('Menu.Dashboard')
    }, {
        path: '/internal/transactions',
        name: i18next.t('Menu.Transactions'),
        children: [
            {
                path: '/internal/transactions/variable',
                name: i18next.t('Menu.VariableTransactions')
            }, {
                path: '/internal/transactions/fixed',
                name: i18next.t('Menu.FixedTransactions')
            }
        ]
    }, {
        name: i18next.t('Menu.Settings'),
        children: [
            {
                path: '/internal/profile',
                name: i18next.t('Menu.Profile')
            }, {
                path: '/internal/settings',
                name: i18next.t('Menu.Settings')
            }
        ]
    }
];
