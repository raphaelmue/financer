import i18next from "i18next";
import {initReactI18next} from "react-i18next";
import common_en from "./en.json";
import common_de from "./de.json";

export function configureI18N() {
    i18next
        .use(initReactI18next) //
        .init({
            interpolation: {escapeValue: false},  // React already does escaping
            lng: 'en',                              // language to use
            resources: {
                en: {common: common_en},
                de: {common: common_de},
            },
        });
}
