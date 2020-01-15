let i18n = require('i18n');

i18n.configure({
    // setup locales
    locales: ['en', 'de'],

    defaultLocale: 'en',

    // sets a custom cookie name to parse locale settings from
    cookie: 'lang',

    // where to store json files - defaults to './locales'
    directory: __dirname + '/../locales'
});

module.exports = i18n;
