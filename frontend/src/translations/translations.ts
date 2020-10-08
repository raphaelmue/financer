import i18next            from 'i18next';
import {initReactI18next} from 'react-i18next';
import translation_en     from './en_US/translations.json';
import translation_de     from './de_DE/translations.json';
import store              from '../store/store';
import {TableLocale}      from 'antd/lib/table/interface';

export function configureI18N() {
    i18next
        .use(initReactI18next) //
        .init({
            interpolation: {escapeValue: false},  // React already does escaping
            lng: store.getState().user.user?.settings?.LANGUAGE?.value || 'en',
            fallbackLng: 'en',
            resources: {
                en: {translation: translation_en},
                de: {translation: translation_de},
            },
        });
}

export const tableTranslations = (): TableLocale => {
    return {
        filterTitle: i18next.t('Form.Table.FilterTitle'),
        filterConfirm: i18next.t('Form.Table.FilterConfirm'),
        filterReset: i18next.t('Form.Table.FilterReset'),
        filterEmptyText: i18next.t('Form.Table.FilterEmpty'),
        emptyText: i18next.t('Form.Table.NoData'),
        selectAll: i18next.t('Form.Table.SelectAll'),
        selectInvert: i18next.t('Form.Table.SelectInvert'),
        // selectionAll: i18next.t('Form.Table.FilterConfirm'),
        sortTitle: i18next.t('Form.Table.SortTitle'),
        expand: i18next.t('Form.Table.Expand'),
        collapse: i18next.t('Form.Table.Collapse'),
        // triggerDesc: i18next.t('Form.Table.FilterConfirm'),
        // triggerAsc: i18next.t('Form.Table.FilterConfirm'),
        // cancelSort: i18next.t('Form.Table.FilterConfirm')
    };
};