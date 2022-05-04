package ru.textanalysis.tawt.ms.additionalDictionary;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import javax.net.ssl.SSLException;
import java.io.IOException;
import java.net.ConnectException;
import java.net.SocketTimeoutException;
import java.util.List;
import java.util.Locale;
import java.util.Objects;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.stream.Collectors;

/**
 * Установка соединия с Wiktionary,
 * получение html страницы с информацией о словах
 */
class ParsabilityChecker {

    private static final Logger log = Logger.getLogger(ParsabilityChecker.class.getName());
    private org.jsoup.nodes.Document doc;
    private Element initialForm;

    /**
     * получение html страницы для необходимого слова
     *
     * @param word           слово, для которого получается страница
     * @param requestTimeOut ождание время подключения в мс
     */
    public ParsabilityChecker(String word, int requestTimeOut) {
        try {
            this.doc = Jsoup.connect("https://ru.wiktionary.org/wiki/" + word).ignoreHttpErrors(true).timeout(requestTimeOut).userAgent("Mozilla/5.0 (Windows NT 6.1; Win64; x64; rv:25.0) Gecko/20100101 Firefox/25.0")
                    .referrer("https://www.yandex.ru").followRedirects(true).ignoreContentType(true)
                    .get();
            this.initialForm = doc.getElementsByTag("h1").first();
        } catch (SocketTimeoutException | SSLException | ConnectException exc) {
            String messages = "https://ru.wiktionary.org/wiki/" + word + ". Не удалось установить соединиение.";
            log.log(Level.SEVERE, messages, exc);
        } catch (IOException e) {
            log.log(Level.SEVERE, e.getMessage(), e);
        }
    }

    /**
     * Повтор подключения к странице Wiktionary
     * если страница уже была получена - сразу возвращает true
     *
     * @param word           слово, для которого получается страница
     * @param requestTimeOut ождание время подключения в мс
     *
     * @return the boolean
     */
    public boolean tryRepeatConnection(String word, int requestTimeOut) {
        try {
            if (this.doc == null) {
                this.doc = Jsoup.connect("https://ru.wiktionary.org/wiki/" + word).ignoreHttpErrors(true).timeout(requestTimeOut).userAgent("Mozilla/5.0 (Windows NT 6.1; Win64; x64; rv:25.0) Gecko/20100101 Firefox/25.0")
                        .referrer("https://www.yandex.ru").followRedirects(true).ignoreContentType(true)
                        .get();
                this.initialForm = doc.getElementsByTag("h1").first();
            }
            return true;
        } catch (Exception e) {
            String messages = "https://ru.wiktionary.org/wiki/" + word + ". Не удалось установить соединиение.";
            log.log(Level.SEVERE, messages, e);
            return false;
        }
    }

    /**
     * Проверка возможности получения информации о слове
     * существует ли вообщения страница с данным словом,
     * было ли оно уже добавлено в словарь,
     * есть ли блок русского языка
     *
     * @param lemmas List в котором хранятся уже добовленные леммы, для предотвращения дублирования
     *
     * @return the boolean
     */
    public boolean checkParsability(List<String> lemmas) {
        AtomicBoolean isParsable = new AtomicBoolean(false);
        if (initialForm != null && !initialForm.text().isEmpty()) {
            Element notFound = doc.getElementsByClass("noarticletext mw-content-ltr").first();
            if (notFound != null) {
                return false;
            }
            if (initialForm.text().toLowerCase(Locale.ROOT).contains("инкубатор")) {
                return false;
            }
            List<String> matchingElements = checkExistingForms(initialForm.text().toLowerCase(Locale.ROOT), lemmas);
            if (matchingElements.size() != 0) {
                return false;
            }
        } else {
            return false;
        }
        Element content = doc.select("div.mw-parser-output").first();
        if (content != null) {
            Elements headLines = content.getElementsByTag("h1");
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

        String tagsStr = "";
        Elements wordProperties = Objects.requireNonNull(doc.getElementsByClass("mw-parser-output").first()).getElementsByTag("p");
        if (wordProperties.size() > 0) {
            tagsStr += wordProperties.get(0).text().toLowerCase(Locale.ROOT);
            tagsStr += ",";
            if (wordProperties.size() > 1) {
                tagsStr += wordProperties.get(1).text().toLowerCase(Locale.ROOT);
                tagsStr += ",";
            }
            if (wordProperties.size() > 2) {
                tagsStr += wordProperties.get(2).text().toLowerCase(Locale.ROOT);
            }
        }

        return !tagsStr.contains("викисловарь:к удалению");
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