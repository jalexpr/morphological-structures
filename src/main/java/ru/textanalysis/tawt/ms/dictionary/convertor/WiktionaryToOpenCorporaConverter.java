package ru.textanalysis.tawt.ms.dictionary.convertor;

import static ru.textanalysis.tawt.ms.constant.Const.*;
import static ru.textanalysis.tawt.ms.constant.TypeOfSpeechs.*;

/**
 * Преобразование тегов с Wiktionary в стандарт OpenCorpora
 */
public class WiktionaryToOpenCorporaConverter {

    private final WiktionaryTagsData wiktionaryTagsData;

    /**
     * Instantiates a new Tags to word form converter.
     *
     * @param wiktionaryTagsData объект с информацией о преобразовании тегов
     */
    public WiktionaryToOpenCorporaConverter(WiktionaryTagsData wiktionaryTagsData) {
        this.wiktionaryTagsData = wiktionaryTagsData;
    }

    /**
     * Преобразовывает неизменяемые части речи из
     * стандарта Wiktionary в стандарт OpenCorpora
     *
     * @param token слово и его теги, разделённые '\t'
     * @return WordForm с леммой в стандарте OpenCorpora
     */
    public WordFormForConverter convertImmutableToWordForm(String token) {
        String[] tokens = token.split(TAB_SEPARATOR);
        String[] tags = tokens[1].split(TAB_AND_COMMA_REGEX);
        StringBuilder builder = new StringBuilder(tokens[0]).append(TAB_SEPARATOR);
        for (String tag : tags) {
            tag = tag.trim();
            if (wiktionaryTagsData.getTags().containsKey(tag)) {
                builder.append(wiktionaryTagsData.getTags().get(tag)).append(COMMA_SEPARATOR);
            }
        }
        String result = builder.toString();
        if (result.contains(COMMA_SEPARATOR)) {
            result = result.substring(0, result.length() - 1).replaceAll(VERB, INFN);
        }
        return new WordFormForConverter(result);
    }

    /**
     * Преобразовывает изменяемые части речи из
     * стандарте Wiktionary в стандарт OpenCorpora
     * Задействуется таблица словоформ
     *
     * @param token слово и его теги, разделённые '\t'
     * @return WordForm с леммой в стандарте OpenCorpora
     */
    public WordFormForConverter convertTableFormToWordForm(String token) {
        String[] tokens = token.split(TAB_SEPARATOR);
        String[] tags = tokens[1].split(TAB_AND_COMMA_REGEX);
        StringBuilder builder = new StringBuilder(tokens[0]).append(TAB_SEPARATOR);
        for (String tag : tags) {
            tag = tag.trim();
            if (wiktionaryTagsData.getTags().containsKey(tag) && builder.indexOf(wiktionaryTagsData.getTags().get(tag)) == -1) {
                builder.append(wiktionaryTagsData.getTags().get(tag)).append(COMMA_SEPARATOR);
            }
        }
        String result = builder.toString();
        if (result.contains(COMMA_SEPARATOR)) {
            result = result.substring(0, result.length() - 1);
        }
        if (token.contains("порядковое")) {
            result = result.replaceAll(NUMR, ADJF);
        }
        if (token.contains("кратк. форма")) {
            if (result.contains(ADJF)) {
                result = result.replaceAll(ADJF, ADJS);
            } else if (result.contains(PRTF)) {
                result = result.replaceAll(PRTF, PRTS);
            }
        }
        return new WordFormForConverter(result);
    }
}