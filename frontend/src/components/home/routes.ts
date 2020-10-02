import {MenuDataItem, Route} from "@ant-design/pro-layout/lib/typings";

export const routes: Route[] = [{
    path: '#/internal',
    name: 'Home',
    routes: [
        {
            path: '/dashboard',
            name: 'Dashboard',
            component: './dashboard/Dashboard'
        }, {
            path: '/transactions',
            name: 'Transactions',
            icon: '',
            routes: [
                {
                    path: '/variable',
                    name: 'Variable Transactions',
                    icon: '',
                }, {
                    path: '/fixed',
                    name: 'Fixed Transactions',
                    icon: '',
                    component: './Home',
                }, {
                    path: '/categories',
                    name: 'Categories',
                    icon: '',
                }, {
                    path: '/profile',
                    name: 'Profile',
                    icon: '',
                }, {
                    path: '/settings',
                    name: 'Settings',
                    icon: '',
                }
            ]
        }
    ]
}];

export const menu: MenuDataItem[] = [
    {
        path: '/internal/dashboard',
        name: 'Dashboard'
    }, {
        path: '/internal/transactions',
        name: 'Transactions',
        children: [
            {
                path: '/internal/transactions/variable',
                name: 'Variable Transactions'
            }, {
                path: '/internal/transactions/fixed',
                name: 'Fixed Transactions'
            }
        ]
    }, {
        name: "Settings",
        children: [
            {
                path: '/internal/profile',
                name: 'Profile'
            }, {
                path: '/internal/settings',
                name: 'Settings'
            }
        ]
    }
];
