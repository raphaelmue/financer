import store from '../../../../store/store';

export function getCurrencySign(): string {
    switch (store.getState().user.user?.settings?.CURRENCY?.value) {
        case 'EUR':
        case '€':
            return '€';
        default:
            return '$';
    }
}

export function getCurrencySymbol(): string {
    return store.getState().user.user?.settings?.CURRENCY?.value || '$';
}