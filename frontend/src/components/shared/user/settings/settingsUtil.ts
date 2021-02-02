import store                         from '../../../../store/store';
import {Category, CategoryClassEnum} from '../../../../.openapi';

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

export function isDarkTheme(): boolean {
    return store.getState().user.user?.settings?.THEME?.value === 'dark' || false;
}

export function adjustAmountSign(amount: number, category: Category): number {
    if (store.getState().user.user?.settings?.CHANGE_AMOUNT_SIGN_AUTOMATICALLY?.value === 'true') {
        if ((amount > 0 && category.categoryClass === (CategoryClassEnum.FIXEDEXPENSES || CategoryClassEnum.VARIABLEEXPENSES)) ||
            (amount < 0 && category.categoryClass === (CategoryClassEnum.FIXEDREVENUE || CategoryClassEnum.VARIABLEREVENUE))) {
            amount = amount * -1;
        }
    }
    return amount;
}
