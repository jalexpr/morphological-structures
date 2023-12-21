package ru.textanalysis.tawt.ms.dictionary.additional.words.parser;

import lombok.extern.slf4j.Slf4j;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import ru.textanalysis.tawt.ms.dictionary.additional.words.convertor.TagsForOpenCorporaDictionaryConversion;
import ru.textanalysis.tawt.ms.dictionary.additional.words.convertor.WiktionaryToOpenCorporaConverter;
import ru.textanalysis.tawt.ms.dictionary.additional.words.convertor.WordFormForConverter;
import ru.textanalysis.tawt.ms.dictionary.parser.AbstractInfoParser;
import ru.textanalysis.tawt.ms.dictionary.parser.InfoParserResponse;
import ru.textanalysis.tawt.ms.dictionary.wiktionary.WiktionaryParsabilityChecker;

import java.net.SocketTimeoutException;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Objects;

import static java.lang.Thread.sleep;
import static ru.textanalysis.tawt.ms.constant.Const.COMMA_SEPARATOR;
import static ru.textanalysis.tawt.ms.constant.Const.TAB_SEPARATOR;

/**
 * Парсер информации о тегах слова из Wiktionary
 */
@Slf4j
public class WiktionaryWordsInfoParser extends AbstractInfoParser {

    private final WiktionaryToOpenCorporaConverter wiktionaryToOpenCorporaConverter;
    private WiktionaryParsabilityChecker parsabilityChecker;

    /**
     * Instantiates a new Wiktionary info parser.
     *
     * @param wiktionaryTagsData объект с информацией о преобразовании тегов
     */
    public WiktionaryWordsInfoParser(TagsForOpenCorporaDictionaryConversion wiktionaryTagsData, List<String> lemmas) {
        super(lemmas);
        this.wiktionaryToOpenCorporaConverter = new WiktionaryToOpenCorporaConverter(wiktionaryTagsData);
    }

    /**
     * Получение информации о слове и его тегах с Wiktionary
     *
     * @param word           исследуемое слово
     *
     * @return the words tags
     */
	@Override
    public InfoParserResponse<List<List<WordFormForConverter>>> getInfo(String word) {
		return InfoParserResponse.<List<List<WordFormForConverter>>>builder()
			.response(getInfo(word, sleepTime, requestTimeOut))
			.build();
    }

    /**
     * Получение информации о слове и его тегах с Wiktionary
     *
     * @param word           исследуемое слово
     * @param sleepTime      время между запросами
     * @param requestTimeOut время ожидания подключения
     * @return the words tags
     */
    private List<List<WordFormForConverter>> getInfo(String word, int sleepTime, int requestTimeOut) {
        List<List<WordFormForConverter>> lexems = new ArrayList<>();
        if (getLemmas().contains(word.toLowerCase(Locale.ROOT))) {
            return new ArrayList<>();
        }
        if (word.isEmpty()) {
            return new ArrayList<>();
        }
        if (!word.matches("[а-яА-ЯёЁ-]+")) {
            return new ArrayList<>();
        }
        try {
            sleep(sleepTime);
            boolean isConnected = checkConnection(word, sleepTime, requestTimeOut);
            if (!isConnected){
                return new ArrayList<>();
            }

            Element table = null;
            boolean isFoundRussianBlock = false;
            boolean isFoundWordTags = false;
            StringBuilder tagsStr = new StringBuilder();
            Element mainElement = parsabilityChecker.getDoc().getElementsByClass("mw-parser-output").first();
            Elements mainElements = mainElement != null ? mainElement.getAllElements() : null;
            for (int i = 0; i < Objects.requireNonNull(mainElements).size(); i++) {
                if (mainElements.get(i).tagName().equals("h1")) {
                    if (isFoundRussianBlock) {
                        break;
                    }
                    if (Objects.requireNonNull(mainElements.get(i).getElementsByClass("mw-headline").first()).text().equals("Русский")) {
                        isFoundRussianBlock = true;
                        continue;
                    }
                }
                if (isFoundRussianBlock) {
                    if (mainElements.get(i).tagName().equals("h3")) {
                        if (Objects.requireNonNull(mainElements.get(i).getElementsByClass("mw-headline").first()).text().equals("Морфологические и синтаксические свойства")) {
                            isFoundWordTags = true;
                            continue;
                        }
                    }
                    if (isFoundWordTags) {
                        if (mainElements.get(i).tagName().equals("table")) {
                            table = mainElements.get(i);
                        } else if (mainElements.get(i).tagName().equals("p")) {
                            tagsStr.append(mainElements.get(i).text().toLowerCase(Locale.ROOT));
                            tagsStr.append(COMMA_SEPARATOR);
                        } else if (mainElements.get(i).tagName().equals("h3") || mainElements.get(i).tagName().equals("h4")) {
                            List<List<WordFormForConverter>> info = parseInfo(table, tagsStr.toString(), word.toLowerCase(Locale.ROOT));
                            isFoundWordTags = false;
                            table = null;
                            tagsStr = new StringBuilder();
                            lexems.addAll(info);
                        }
                    }
                }
            }

            lexems.removeIf(lexem -> getLemmas().contains(lexem.get(0).getWord()));

            if (!tryAdd(parsabilityChecker.getInitialForm().text().toLowerCase(Locale.ROOT))) {
                return new ArrayList<>();
            }

            return lexems;
        } catch (SocketTimeoutException exc) {
            String messages;
            if (Objects.equals(exc.getMessage(), "Неудачное повторное соединение.")) {
                messages = "https://ru.wiktionary.org/wiki/" + word.toLowerCase(Locale.ROOT) + ". Неудачное повторное соединение.";
            } else {
                messages = "https://ru.wiktionary.org/wiki/" + word.toLowerCase(Locale.ROOT) + ". Не удалось установить соединение.";
            }
            log.error(messages, exc);
            return new ArrayList<>();
        } catch (NullPointerException ex) {
            log.error("Не удалось разобрать страницу.", ex);
            return new ArrayList<>();
        } catch (Exception ex) {
            log.error(ex.getMessage(), ex);
            return new ArrayList<>();
        }
    }

    private List<List<WordFormForConverter>> parseInfo(Element table, String tagsStr, String word) {
        List<WordFormForConverter> lemma = new ArrayList<>();
        List<WordFormForConverter> additionalLemma = new ArrayList<>();
        List<List<WordFormForConverter>> lexems = new ArrayList<>();
        if (table == null) {
            if (tagsStr.contains("наречие") || tagsStr.contains("союз") || tagsStr.contains("предлог")
                || tagsStr.contains("частица") || tagsStr.contains("междометие") || tagsStr.contains("неизменяем")) {
                lemma.add(wiktionaryToOpenCorporaConverter.convertImmutableToWordForm(word.toLowerCase(Locale.ROOT) + TAB_SEPARATOR + tagsStr));
            }
        } else {
            Elements tags = table.getElementsByTag("tr");

            if (tagsStr.contains("глагол")) {
                additionalLemma.add(wiktionaryToOpenCorporaConverter.convertImmutableToWordForm(parsabilityChecker.getInitialForm().text().toLowerCase(Locale.ROOT) + TAB_SEPARATOR + tagsStr));
            }
            List<String> tokenTags = new ArrayList<>();
            for (int tag = 0; tag < tags.size(); tag++) {
                Elements child = tags.get(tag).children();
                int counter = 0;
                int columnNumber = 0;
                StringBuilder rowTokenTags = new StringBuilder();
                boolean repeatingForm = false;
                if (child.size() > tokenTags.size() || child.get(0).tagName().equals("th")) {
                    for (int j = 0; j < child.size(); j++) {
                        if (tag == 0 && j == 0 && child.get(j).tagName().equals("th")) {
                            continue;
                        }
                        int colSpan;
                        if (child.get(j).tagName().equals("th")) {
                            if (child.get(j).hasAttr("colspan")) {
                                colSpan = Integer.parseInt(child.get(j).attr("colspan"));
                            } else {
                                colSpan = 1;
                            }
                            if (tag == 0) {
                                for (int k = 0; k < colSpan; k++) {
                                    tokenTags.add(child.get(j).text().toLowerCase(Locale.ROOT));
                                }
                            } else {
                                for (int k = 0; k < colSpan; k++) {
                                    tokenTags.set(counter, tokenTags.get(counter) + COMMA_SEPARATOR + child.get(counter).text().toLowerCase(Locale.ROOT));
                                    counter++;
                                }
                            }
                        } else if (child.get(j).tagName().equals("td")) {
                            if (!child.get(j).children().isEmpty() && !child.get(j).children().get(0).tagName().equals("br")) {
                                if (rowTokenTags.length() == 0) {
                                    rowTokenTags = new StringBuilder(child.get(j).getElementsByTag("a").text().toLowerCase(Locale.ROOT));
                                } else {
                                    rowTokenTags.append(COMMA_SEPARATOR);
                                    rowTokenTags.append(child.get(j).getElementsByTag("a").text().toLowerCase(Locale.ROOT));
                                }
                            } else {
                                String[] tex = child.get(j).text().split(" ");
                                for (int p = 0; p < tex.length; p++) {
                                    if (tex.length > 1 && changeSpecialCharacter(tex[0]).equals(changeSpecialCharacter(tex[1]))) {
                                        repeatingForm = true;
                                    }
                                    if (!(p > 0 && repeatingForm)) {
                                        tex[p] = changeSpecialCharacter(tex[p]);
                                        if (!tex[p].equals("—")) {
                                            String token = tex[p] + TAB_SEPARATOR + tagsStr + COMMA_SEPARATOR + tokenTags.get(columnNumber) + COMMA_SEPARATOR + rowTokenTags;
                                            if (token.contains("он она оно") && tex.length > 1) {
                                                if (p == 0) {
                                                    token += ",муж. р.";
                                                }
                                                if (p == 1) {
                                                    token += ",жен. р.";
                                                }
                                                if (p == 2) {
                                                    token += ",ср. р.";
                                                }
                                            }
                                            if (token.contains(",м.")) {
                                                continue;
                                            }
                                            if ((rowTokenTags.toString().contains("я") || rowTokenTags.toString().contains("ты") || rowTokenTags.toString().contains("вы")
                                                || rowTokenTags.toString().contains("они")) && tokenTags.get(columnNumber).contains("прош.")) {
                                                continue;
                                            } else if ((rowTokenTags.toString().contains("он она оно") || rowTokenTags.toString().contains("мы"))
                                                && tokenTags.get(columnNumber).contains("прош.")) {
                                                token = token.replaceAll("он она оно", "").replaceAll(",мы", "");
                                            }
                                            if (token.contains("кратк. форма")) {
                                                additionalLemma.add(wiktionaryToOpenCorporaConverter.convertTableFormToWordForm(token.toLowerCase(Locale.ROOT)));
                                            } else {
                                                lemma.add(wiktionaryToOpenCorporaConverter.convertTableFormToWordForm(token.toLowerCase(Locale.ROOT)));
                                            }
                                        }
                                    }
                                }
                                columnNumber++;
                            }
                        }
                    }
                }
            }
        }

        if (lemma.size() > 0) {
            lexems.add(lemma);
            if (additionalLemma.size() > 0) {
                lexems.add(additionalLemma);
            }
        }

        return lexems;
    }

    private boolean checkConnection(String word, int sleepTime, int requestTimeOut) throws SocketTimeoutException {
        try {
            parsabilityChecker = new WiktionaryParsabilityChecker(word.toLowerCase(Locale.ROOT), requestTimeOut);
            if (parsabilityChecker.getDoc() == null) {
                sleep(sleepTime / 2);
                boolean isSuccessful = parsabilityChecker.tryRepeatConnection(word.toLowerCase(Locale.ROOT), 2 * requestTimeOut);
                if (!isSuccessful) {
                    throw new SocketTimeoutException("Неудачное повторное соединение.");
                }
            }
            if (getLemmas().contains(parsabilityChecker.getInitialForm().text().toLowerCase(Locale.ROOT))) {
                return false;
            }
            return parsabilityChecker.checkParsability(getLemmas());
        } catch (IllegalArgumentException | InterruptedException e) {
            log.error(e.getMessage(), e);
            return false;
        }
    }
}