package ru.textanalysis.tawt.ms.additionalDictionary;

import java.util.logging.Logger;

/**
 * Преобразование тегов с Wiktionary в стандарт OpenCorpora
 */
class TagsToWordFormConverter {

    private static final Logger log = Logger.getLogger(TagsToWordFormConverter.class.getName());
    private final WiktionaryTagsData wiktionaryTagsData;

    /**
     * Instantiates a new Tags to word form converter.
     *
     * @param wiktionaryTagsData объект с информацией о преобразовании тегов
     */
    public TagsToWordFormConverter(WiktionaryTagsData wiktionaryTagsData) {
        this.wiktionaryTagsData = wiktionaryTagsData;
    }

    /**
     * Преобразовывает неизменяемые части речи из
     * стандарте Wiktionary в стандарт OpenCorpora
     *
     * @param token слово и его теги, разделённые '\t'
     *
     * @return WordForm с леммой в стандарте OpenCorpora
     */
    public WordForm convertImmutableToWordForm(String token) {
        String[] tokens = token.split("\t");
        String[] tags = tokens[1].split("[,;]");
        StringBuilder result = new StringBuilder(tokens[0] + "\t");
        for (String tag : tags) {
            if (wiktionaryTagsData.getTags().containsKey(tag.trim())) {
                result.append(wiktionaryTagsData.getTags().get(tag.trim())).append(",");
            }
        }
        if (result.toString().contains(",")) {
            result = new StringBuilder(result.substring(0, result.length() - 1).replaceAll("VERB", "INFN"));
        }
        return new WordForm(result.toString());
    }

    /**
     * Преобразовывает изменяемые части речи из
     * стандарте Wiktionary в стандарт OpenCorpora
     * Задействуется таблица словоформ
     *
     * @param token слово и его теги, разделённые '\t'
     *
     * @return WordForm с леммой в стандарте OpenCorpora
     */
    public WordForm convertTableFormToWordForm(String token) {
        String[] tokens = token.split("\t");
        String[] tags = tokens[1].split("[,;]");
        StringBuilder result = new StringBuilder(tokens[0] + "\t");
        for (String tag : tags) {
            if (wiktionaryTagsData.getTags().containsKey(tag.trim()) && !result.toString().contains(wiktionaryTagsData.getTags().get(tag.trim()))) {
                result.append(wiktionaryTagsData.getTags().get(tag.trim())).append(",");
            }
        }
        if (result.toString().contains(",")) {
            result = new StringBuilder(result.substring(0, result.length() - 1));
        }
        if (token.contains("порядковое")) {
            result = new StringBuilder(result.toString().replaceAll("NUMR", "ADJF"));
        }
        if (token.contains("кратк. форма")) {
            if (result.toString().contains("ADJF")) {
                result = new StringBuilder(result.toString().replaceAll("ADJF", "ADJS"));
            } else if (result.toString().contains("PRTF")) {
                result = new StringBuilder(result.toString().replaceAll("PRTF", "PRTS"));
            }
        }
        return new WordForm(result.toString());
    }
}