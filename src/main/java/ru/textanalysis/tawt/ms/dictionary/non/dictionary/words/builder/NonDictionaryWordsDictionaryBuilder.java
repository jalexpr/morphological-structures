package ru.textanalysis.tawt.ms.dictionary.non.dictionary.words.builder;

import lombok.extern.slf4j.Slf4j;
import ru.textanalysis.tawt.ms.dictionary.builder.DictionaryBuilder;
import ru.textanalysis.tawt.ms.dictionary.non.dictionary.words.convertor.TagsForNonDictionaryWordConversion;
import ru.textanalysis.tawt.ms.dictionary.non.dictionary.words.file.postfix.PostfixBinaryFile;
import ru.textanalysis.tawt.ms.dictionary.non.dictionary.words.file.prefix.PrefixBinaryFile;
import ru.textanalysis.tawt.ms.dictionary.non.dictionary.words.parser.WiktionaryFlexionsInfoParser;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Основной класс
 * Ведёт статистику о приставках и окончаниях исследуемых слов
 * Запись информации в бинарные файлы
 */
@Slf4j
public class NonDictionaryWordsDictionaryBuilder implements DictionaryBuilder {

	private final TagsForNonDictionaryWordConversion tagsForNonDictionaryWordConversion;
	private final WiktionaryFlexionsInfoParser wiktionaryFlexionsInfoParser;
	private final List<String> lemmas;

	public NonDictionaryWordsDictionaryBuilder() {
		this.lemmas = Collections.synchronizedList(new ArrayList<>());
		tagsForNonDictionaryWordConversion = new TagsForNonDictionaryWordConversion();
		wiktionaryFlexionsInfoParser = new WiktionaryFlexionsInfoParser(tagsForNonDictionaryWordConversion, this.lemmas);
	}

	/**
	 * Добавление информации о флексиях слова в статистику
	 *
	 * @param word исследуемое слово
	 *
	 * @return true - успешное добавление, false - ошибка
	 */
	@Override
	public boolean addInfo(String word) {
		return wiktionaryFlexionsInfoParser.getInfo(word).getResponse();
	}

	/**
	 * Записывает информацию о флексиях в бинарные файлы
	 */
	@Override
	public void recordInFile() {
		try {
			PrefixBinaryFile prefixBinaryFile = new PrefixBinaryFile(wiktionaryFlexionsInfoParser.getPrefixMorphologicalCharacteristicsChangesInfo());
			prefixBinaryFile.recreate();
			PostfixBinaryFile postfixBinaryFile = new PostfixBinaryFile(wiktionaryFlexionsInfoParser.getPostfixMorphologicalCharacteristicsInfo());
			postfixBinaryFile.recreate();
		} catch (Exception e) {
			log.error("Ошибка при создании файлов с флексиями", e);
		}
	}
}