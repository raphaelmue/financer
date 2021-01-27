import translation from './translations.json';

export const deDEIntl = () => {
    return {
        form: {
            lightFilter: {
                more: 'Mehr',
                clear: 'Löschen',
                confirm: translation.Form.Button.Ok,
                itemUnit: 'Elemente'
            }
        },
        tableForm: {
            search: 'Suche',
            reset: 'Zurücksetzen',
            submit: translation.Form.Button.Submit,
            collapsed: 'Ausklappen',
            expand: 'Einklappen',
            inputPlaceholder: 'Bitte eingeben',
            selectPlaceholder: 'Bitte auswählen'
        },
        alert: {
            clear: 'Auswahl löschen',
            selected: 'Ausgewählt',
            item: 'Element'
        },
        pagination: {
            total: {
                range: ' ',
                total: 'von',
                item: 'Elementen'
            }
        },
        tableToolBar: {
            leftPin: 'Links anheften',
            rightPin: 'Rechts anheften',
            noPin: 'Nicht angeheftet',
            leftFixedTitle: 'Links fixiert',
            rightFixedTitle: 'Rechts fixiert',
            noFixedTitle: 'Nicht fixiert',
            reset: 'Zurücksetzen',
            columnDisplay: 'Spaltenanzeige',
            columnSetting: 'Einstellungen',
            fullScreen: 'Vollbild',
            exitFullScreen: 'Vollbild verlassen',
            reload: translation.Form.Button.Refresh,
            density: 'Dichte',
            densityDefault: 'Standard',
            densityLarger: 'Groß',
            densityMiddle: 'Mittel',
            densitySmall: 'Klein'
        },
        stepsForm: {
            next: translation.Form.Button.Next,
            prev: translation.Form.Button.Previous,
            submit: 'Finish'
        }
    };
};
