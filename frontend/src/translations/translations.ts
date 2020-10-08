import i18next            from 'i18next';
import {initReactI18next} from 'react-i18next';
import translation_en     from './en_US/translations.json';
import translation_de     from './de_DE/translations.json';
import store              from '../store/store';

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

export const tableTranslations = (): any => {
    return {
        tableFrom: {
            search: 'Query',
            reset: 'Reset',
            submit: 'Submit',
            collapsed: 'Expand',
            expand: 'Collapse',
            inputPlaceholder: 'Please enter',
            selectPlaceholder: 'Please select',
        },
        alert: {
            clear: 'Clear',
        },
        tableToolBar: {
            leftPin: 'Pin to left',
            rightPin: 'Pin to right',
            noPin: 'Unpinned',
            leftFixedTitle: 'Fixed the left',
            rightFixedTitle: 'Fixed the right',
            noFixedTitle: 'Not Fixed',
            reset: 'Reset',
            columnDisplay: 'Column Display',
            columnSetting: 'Settings',
            fullScreen: 'Full Screen',
            exitFullScreen: 'Exit Full Screen',
            reload: 'Refresh',
            density: 'Density',
            densityDefault: 'Default',
            densityLarger: 'Larger',
            densityMiddle: 'Middle',
            densitySmall: 'Compact',
        },
    };
};