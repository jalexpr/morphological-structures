package ru.textanalysis.tawt.ms.dictionary.wiktionary;

import lombok.extern.slf4j.Slf4j;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.IOException;
import java.util.List;
import java.util.Locale;
import java.util.Objects;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.stream.Collectors;

import static ru.textanalysis.tawt.ms.constant.Const.COMMA_SEPARATOR;

/**
 * Установка соединение с Wiktionary,
 * получение html страницы с информацией о словах
 */
@Slf4j
public class WiktionaryParsabilityChecker {

    public final String WIKTIONARY_URL = "https://ru.wiktionary.org/wiki/";
    public final String USER_AGENT = "Mozilla/5.0 (Windows NT 6.1; Win64; x64; rv:25.0) Gecko/20100101 Firefox/25.0";
    public final String REFERRER = "https://www.yandex.ru";
    public final boolean IGNORE_HTTP_ERRORS = Boolean.TRUE;
    public final boolean FOLLOW_REDIRECTS = Boolean.TRUE;
    public final boolean IGNORE_CONTENT_TYPE = Boolean.TRUE;
    public final String ELEMENTS_BY_TAG_H1 = "h1";

    private Document doc;
    private Element initialForm;

    /**
     * получение html страницы для необходимого слова
     *
     * @param word           слово, для которого получается страница
     * @param requestTimeOut ождание время подключения в мс
     */
    public WiktionaryParsabilityChecker(String word, int requestTimeOut) {
        String url = WIKTIONARY_URL + word;
        try {
            this.doc = Jsoup.connect(url)
                .ignoreHttpErrors(IGNORE_HTTP_ERRORS)
                .timeout(requestTimeOut)
                .userAgent(USER_AGENT)
                .referrer(REFERRER)
                .followRedirects(FOLLOW_REDIRECTS)
                .ignoreContentType(IGNORE_CONTENT_TYPE)
                .get();
            this.initialForm = doc.getElementsByTag(ELEMENTS_BY_TAG_H1).first();
        } catch (IOException ex) {
            log.warn(url + " Не удалось установить соединение.", ex);
        }
    }

    /**
     * Повтор подключения к странице Wiktionary
     * если страница уже была получена - сразу возвращает true
     *
     * @param word           слово, для которого получается страница
     * @param requestTimeOut ожидание время подключения в мс
     * @return true если попытка удалась, иначе false
     */
    public boolean tryRepeatConnection(String word, int requestTimeOut) {
        String url = WIKTIONARY_URL + word;
        try {
            if (this.doc == null) {
                this.doc = Jsoup.connect(url)
                    .ignoreHttpErrors(IGNORE_HTTP_ERRORS)
                    .timeout(requestTimeOut)
                    .userAgent(USER_AGENT)
                    .referrer(REFERRER)
                    .followRedirects(FOLLOW_REDIRECTS)
                    .ignoreContentType(IGNORE_CONTENT_TYPE)
                    .get();
                this.initialForm = doc.getElementsByTag(ELEMENTS_BY_TAG_H1).first();
            }
            return true;
        } catch (IOException ex) {
            log.warn(url + " Не удалось установить соединение.", ex);
            return false;
        }
    }

    /**
     * Проверка возможности получения информации о слове
     * существует ли страница с данным словом,
     * было ли оно уже добавлено в словарь,
     * есть ли блок русского языка
     *
     * @param lemmas List в котором хранятся уже добавленные леммы, для предотвращения дублирования
     * @return true если попытка удалась, иначе false
     */
    public boolean checkParsability(List<String> lemmas) {
        if (initialForm != null && !initialForm.text().isEmpty()) {
            Element notFound = doc.getElementsByClass("noarticletext mw-content-ltr").first();
            if (notFound != null) {
                return false;
            }
            if (initialForm.text().toLowerCase(Locale.ROOT).contains("инкубатор")) {
                return false;
            }
            List<String> matchingElements = checkExistingForms(initialForm.text().toLowerCase(Locale.ROOT), lemmas);
            if (!matchingElements.isEmpty()) {
                return false;
            }
        } else {
            return false;
        }
        Element content = doc.select("div.mw-parser-output").first();
        AtomicBoolean isParsable = new AtomicBoolean(Boolean.FALSE);
        if (content != null) {
            Elements headLines = content.getElementsByTag(ELEMENTS_BY_TAG_H1);
            headLines.forEach(headLine -> {
                Elements head = headLine.select("span.mw-headline");
                head.forEach(line -> {
                    if (line.text().equals("Русский")) {
                        isParsable.set(true);
                    }
                });
            });
        }
        if (!isParsable.get()) {
            return false;
        }

        StringBuilder tags = new StringBuilder();
        Elements wordProperties = Objects.requireNonNull(doc.getElementsByClass("mw-parser-output").first()).getElementsByTag("p");
        if (!wordProperties.isEmpty()) {
            tags.append(wordProperties.get(0).text().toLowerCase(Locale.ROOT)).append(COMMA_SEPARATOR);
            if (wordProperties.size() > 1) {
                tags.append(wordProperties.get(1).text().toLowerCase(Locale.ROOT)).append(COMMA_SEPARATOR);
            }
            if (wordProperties.size() > 2) {
                tags.append(wordProperties.get(2).text().toLowerCase(Locale.ROOT)).append(COMMA_SEPARATOR);
            }
        }

        return !tags.toString().contains("викисловарь:к удалению");
    }

    public Document getDoc() {
        return doc;
    }

    public Element getInitialForm() {
        return initialForm;
    }

    private List<String> checkExistingForms(String initialForm, List<String> lemmas) {
        synchronized (lemmas) {
            return lemmas.stream()
                .filter(str -> str.trim().equals(initialForm))
                .collect(Collectors.toList());
        }
    }
}