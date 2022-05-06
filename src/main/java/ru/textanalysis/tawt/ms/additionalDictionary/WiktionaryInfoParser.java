package ru.textanalysis.tawt.ms.additionalDictionary;

import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import javax.net.ssl.SSLException;
import java.net.ConnectException;
import java.net.SocketTimeoutException;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Objects;
import java.util.logging.Level;
import java.util.logging.Logger;

import static java.lang.Thread.sleep;

/**
 * Парсер информации о тегах слова из Wiktionary
 */
class WiktionaryInfoParser {

    private static final Logger log = Logger.getLogger(WiktionaryInfoParser.class.getName());
    private final WiktionaryTagsData wiktionaryTagsData;
    private final TagsToWordFormConverter tagsToWordFormConverter;
    private ParsabilityChecker parsabilityChecker;

    /**
     * Instantiates a new Wiktionary info parser.
     *
     * @param wiktionaryTagsData объект с информацией о преобразовании тегов
     */
    public WiktionaryInfoParser(WiktionaryTagsData wiktionaryTagsData) {
        this.wiktionaryTagsData = wiktionaryTagsData;
        this.tagsToWordFormConverter = new TagsToWordFormConverter(this.wiktionaryTagsData);
    }

    /**
     * Получение информации о слове и его тегах с Wiktionary
     *
     * @param word           исследуемое слово
     * @param sleepTime      время между запросами
     * @param requestTimeOut время ожидания подключения
     * @param lemmas         List в котором хранятся уже добовленные леммы, для предотвращения дублирования
     *
     * @return the words tags
     */
    public List<List<WordForm>> getWordsTags(String word, int sleepTime, int requestTimeOut, List<String> lemmas) {
        List<List<WordForm>> lexems = new ArrayList<>();
        if (lemmas.contains(word.toLowerCase(Locale.ROOT))) {
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
            parsabilityChecker = new ParsabilityChecker(word.toLowerCase(Locale.ROOT), requestTimeOut);
            if (parsabilityChecker.getDoc() == null) {
                sleep(sleepTime / 2);
                boolean isSuccessful = parsabilityChecker.tryRepeatConnection(word.toLowerCase(Locale.ROOT), 2 * requestTimeOut);
                if (!isSuccessful) {
                    throw new SocketTimeoutException("Неудачное повторное соединение.");
                }
            }
            if (lemmas.contains(parsabilityChecker.getInitialForm().text().toLowerCase(Locale.ROOT))) {
                return new ArrayList<>();
            }
            if (!parsabilityChecker.checkParsability(lemmas)) {
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
                            tagsStr.append(",");
                        } else if (mainElements.get(i).tagName().equals("h3") || mainElements.get(i).tagName().equals("h4")) {
                            var info = parseInfo(table, tagsStr.toString(), word.toLowerCase(Locale.ROOT), lemmas);
                            isFoundWordTags = false;
                            table = null;
                            tagsStr = new StringBuilder();
                            lexems.addAll(info);
                        }
                    }
                }
            }

            lexems.removeIf(lexem -> lemmas.contains(lexem.get(0).getWord()));

            if (!tryAdd(parsabilityChecker.getInitialForm().text().toLowerCase(Locale.ROOT), lemmas)) {
                return new ArrayList<>();
            }

            return lexems;
        } catch (SocketTimeoutException exc) {
            String messages = "";
            if (Objects.equals(exc.getMessage(), "Неудачное повторное соединение.")) {
                messages = "https://ru.wiktionary.org/wiki/" + word.toLowerCase(Locale.ROOT) + ". Неудачное повторное соединение.";
            } else {
                messages = "https://ru.wiktionary.org/wiki/" + word.toLowerCase(Locale.ROOT) + ". Не удалось установить соединиение.";
            }
            log.log(Level.SEVERE, messages, exc);
            return null;
        } catch (NullPointerException e) {
            String messages = "Не удалось разобрать страницу.";
            log.log(Level.SEVERE, messages, e);
            return new ArrayList<>();
        }
        catch (Exception e) {
            log.log(Level.SEVERE, e.getMessage(), e);
            return new ArrayList<>();
        }
    }

    private List<List<WordForm>> parseInfo(Element table, String tagsStr, String word, List<String> lemmas) {
        List<WordForm> lemma = new ArrayList<>();
        List<WordForm> additionalLemma = new ArrayList<>();
        List<List<WordForm>> lexems = new ArrayList<>();
        if (table == null) {
            if (tagsStr.contains("наречие") || tagsStr.contains("союз") || tagsStr.contains("предлог")
                    || tagsStr.contains("частица") || tagsStr.contains("междометие") || tagsStr.contains("неизменяем")) {
                lemma.add(tagsToWordFormConverter.convertImmutableToWordForm(word.toLowerCase(Locale.ROOT) + "\t" + tagsStr));
            }
        } else {
            Elements tags = table.getElementsByTag("tr");

            if (tagsStr.contains("глагол")) {
                additionalLemma.add(tagsToWordFormConverter.convertImmutableToWordForm(parsabilityChecker.getInitialForm().text().toLowerCase(Locale.ROOT) + "\t" + tagsStr));
            }
            var tokenTags = new ArrayList<String>();
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
                                    tokenTags.set(counter, tokenTags.get(counter) + "," + child.get(counter).text().toLowerCase(Locale.ROOT));
                                    counter++;
                                }
                            }
                        } else if (child.get(j).tagName().equals("td")) {
                            if (child.get(j).childrenSize() != 0 && !child.get(j).children().get(0).tagName().equals("br")) {
                                if (rowTokenTags.length() == 0) {
                                    rowTokenTags = new StringBuilder(child.get(j).getElementsByTag("a").text().toLowerCase(Locale.ROOT));
                                } else {
                                    rowTokenTags.append(",");
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
                                            String token = tex[p] + "\t" + tagsStr + "," + tokenTags.get(columnNumber) + "," + rowTokenTags;
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
                                                additionalLemma.add(tagsToWordFormConverter.convertTableFormToWordForm(token.toLowerCase(Locale.ROOT)));
                                            } else {
                                                lemma.add(tagsToWordFormConverter.convertTableFormToWordForm(token.toLowerCase(Locale.ROOT)));
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

    private boolean tryAdd(String initialForm, List<String> lemmas) {
        synchronized (lemmas) {
            if (!lemmas.contains(initialForm)) {
                lemmas.add(initialForm);
                return true;
            }
            return false;
        }
    }

    private String changeSpecialCharacter(String s) {
        s = s.replaceAll("́", "").replaceAll("́ѐ", "е").replaceAll("́о̀", "о").
                replaceAll("а̀", "а").replaceAll("ѐ", "е")
                .replaceAll("ѝ", "и").replaceAll("о̀", "о")
                .replaceAll("у̀", "у").replaceAll("ё̀", "ё")
                .replaceAll("э̀", "э").replaceAll(",", "")
                .replaceAll("ѝ", "и").replaceAll("я̀","я")
                .replaceAll("о̀", "о").replaceAll("о̀", "о")
                .replaceAll("у̀", "у").replaceAll("я̀", "я")
                .replaceAll("ы̀", "ы").replaceAll("ю̀", "ю");
        return s;
    }
}