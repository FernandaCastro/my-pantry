import i18n from 'i18next';
import { initReactI18next } from 'react-i18next';
import Backend from 'i18next-http-backend';
import LanguageDetector from 'i18next-browser-languagedetector';
import { DateTime } from 'luxon';

i18n
    .use(Backend) // passes i18n down
    .use(LanguageDetector)
    .use(initReactI18next)
    .init({
        supportedLngs: ['en-GB', 'pt-BR'],

        // lng: 'pt-BR', // when using a language detector, disable this option
        fallbackLng: 'en-GB', //when language detector fails

        interpolation: {
            escapeValue: false, // react already safes from xss => https://www.i18next.com/translation-function/interpolation#unescape
            format: (value, format, lng) => {
                // legacy usage
                if (value instanceof Date) {
                    return DateTime.fromJSDate(value)
                        .setLocale(lng)
                        .toLocaleString(DateTime[format]);
                }
                return value;
            },

        },
    });