package ru.textanalysis.tawt.ms.dictionary.non.dictionary.words.convertor;


import lombok.AllArgsConstructor;
import ru.textanalysis.tawt.ms.dictionary.non.dictionary.words.postfix.PostfixMorphologicalCharacteristics;
import ru.textanalysis.tawt.ms.dictionary.non.dictionary.words.prefix.PrefixInfo;

/**
 * Преобразование тегов с Wiktionary в стандарт JMorfSdk
 */
@AllArgsConstructor
public class TagsToWordFlexionsInfoConverter {

	private final TagsForNonDictionaryWordConversion tagsForNonDictionaryWordConversion;

	/**
	 * Преобразовывает неизменяемые части речи из
	 * стандарте Wiktionary в стандарт JMorfSdk
	 *
	 * @param token префикс, слово и его теги, разделённые '\t'
	 *
	 * @return информация о префиксе слова и его тегах
	 */
	public PrefixInfo convertImmutableToPrefixInfo(String token) {
		String[] tokens = token.split("\t");
		String[] tags = tokens[2].split("[,;]");
		StringBuilder result = new StringBuilder(tokens[0] + "\t" + tokens[1] + "\t");
		for (String tag : tags) {
			if (tagsForNonDictionaryWordConversion.getTags().containsKey(tag.trim())) {
				result.append(tagsForNonDictionaryWordConversion.getTags().get(tag.trim())).append(",");
			}
		}
		if (result.toString().contains(",")) {
			result = new StringBuilder(result.substring(0, result.length() - 1).replaceAll("VERB", "INFN"));
		}
		return new PrefixInfo(result.toString());
	}

	/**
	 * Преобразовывает неизменяемые части речи из
	 * стандарте Wiktionary в стандарт JMorfSdk
	 *
	 * @param token слово и его теги, разделённые '\t'
	 *
	 * @return информация об окончании слова, а также части речи и морфолоических характеристиках относящихся к этому окончанию
	 */
	public PostfixMorphologicalCharacteristics convertImmutableToPostfixInfo(String token) {
		String[] tokens = token.split("\t");
		String[] tags = tokens[1].split("[,;]");
		StringBuilder result = new StringBuilder(tokens[0] + "\t");
		if (token.contains("инфинитив")) {
			result.append("INFN,");
		}
		for (String tag : tags) {
			if (tagsForNonDictionaryWordConversion.getTags().containsKey(tag.trim())) {
				result.append(tagsForNonDictionaryWordConversion.getTags().get(tag.trim())).append(",");
			}
		}
		if (result.toString().contains(",")) {
			result = new StringBuilder(result.substring(0, result.length() - 1));
		}
		return new PostfixMorphologicalCharacteristics(result.toString());
	}

	/**
	 * Преобразовывает изменяемые части речи из
	 * стандарте Wiktionary в стандарт JMorfSdk
	 * Задействуется таблица словоформ
	 *
	 * @param token слово и его теги, разделённые '\t'
	 *
	 * @return информация об окончании слова, а также части речи и морфолоических характеристиках относящихся к этому окончанию
	 */
	public PostfixMorphologicalCharacteristics convertTableFormToPostfixInfo(String token) {
		String[] tokens = token.split("\t");
		String[] tags = tokens[1].split("[,;]");
		StringBuilder result = new StringBuilder(tokens[0] + "\t");
		for (String tag : tags) {
			if (tagsForNonDictionaryWordConversion.getTags().containsKey(tag.trim()) && !result.toString().contains(tagsForNonDictionaryWordConversion.getTags().get(tag.trim()))) {
				result.append(tagsForNonDictionaryWordConversion.getTags().get(tag.trim())).append(",");
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
		return new PostfixMorphologicalCharacteristics(result.toString());
	}
}