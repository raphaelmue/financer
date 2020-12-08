import i18next                          from 'i18next';
import {initReactI18next}               from 'react-i18next';
import translation_en                   from './en_US/translations.json';
import translation_de                   from './de_DE/translations.json';
import store                            from '../store/store';
import {Locale}                         from 'antd/lib/locale-provider';
import enUS                             from 'antd/lib/locale/en_US';
import deDE                             from 'antd/lib/locale/de_DE';
import {createIntl, enUSIntl, IntlType} from '@ant-design/pro-table';
import {deDEIntl}                       from './de_DE/intl';

export function getCurrentLocale(): Locale {
    if (store.getState().user.user?.settings?.LANGUAGE?.value) {
        switch (store.getState().user.user?.settings?.LANGUAGE?.value) {
            case 'de':
                return deDE;
        }
    }
    return enUS;

}

export function getCurrentIntlType(): IntlType {
    if (store.getState().user.user?.settings?.LANGUAGE?.value) {
        switch (store.getState().user.user?.settings?.LANGUAGE?.value) {
            case 'de':
                return createIntl('de_DE', deDEIntl());
        }
    }
    return enUSIntl;
}

export function configureI18N() {
    i18next
        .use(initReactI18next) //
        .init({
            interpolation: {escapeValue: false},  // React already does escaping
            lng: store.getState().user.user?.settings?.LANGUAGE?.value || 'en',
            fallbackLng: 'en',
            ns: ['default'],
            defaultNS: 'default',
            resources: {
                en: {translation: translation_en},
                de: {translation: translation_de},
            },
        });
}
