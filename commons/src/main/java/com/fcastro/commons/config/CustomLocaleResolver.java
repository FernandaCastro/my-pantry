package com.fcastro.commons.config;

import jakarta.servlet.http.HttpServletRequest;
import org.springframework.util.StringUtils;
import org.springframework.web.servlet.i18n.AcceptHeaderLocaleResolver;

import java.util.Arrays;
import java.util.List;
import java.util.Locale;

public class CustomLocaleResolver extends AcceptHeaderLocaleResolver {

    private final String LANGUAGE_HEADER = "language";
    private final String DEFAULT_LOCALE = "en-GB";
    private final List<Locale> LOCALES = Arrays.asList(Locale.forLanguageTag("en-GB"), Locale.forLanguageTag("pt-BR"));

    @Override
    public Locale resolveLocale(HttpServletRequest request) {
        var languageHeader = request.getHeader(LANGUAGE_HEADER);

        if (StringUtils.isEmpty(languageHeader)) {
            return Locale.forLanguageTag(DEFAULT_LOCALE);
        }

        var list = Locale.LanguageRange.parse(languageHeader);
        var locale = Locale.lookup(list, LOCALES);
        if (locale == null) {
            return Locale.forLanguageTag(DEFAULT_LOCALE);
        }
        return locale;
    }
}
