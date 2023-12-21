package ru.textanalysis.tawt.ms.dictionary.non.dictionary.words.prefix;


import ru.textanalysis.tawt.ms.dictionary.non.dictionary.words.convertor.TagsForNonDictionaryWordConversion;

import java.util.HashMap;
import java.util.Map;

public class PrefixMorphologicalCharacteristicsChangesInfo {

	private final Map<String, PrefixMorphologicalCharacteristicsChangesForPartOfSpeechInfo> morphologyCharacteristicsChanges;
	private final TagsForNonDictionaryWordConversion tagsForNonDictionaryWordConversion;

	public PrefixMorphologicalCharacteristicsChangesInfo() {
		morphologyCharacteristicsChanges = new HashMap<>();
		tagsForNonDictionaryWordConversion = new TagsForNonDictionaryWordConversion();
	}

	public synchronized void add(PrefixInfo prefixInfoForCurrentWord, PrefixInfo prefixInfoForWordWithPrefix) {
		if (morphologyCharacteristicsChanges.containsKey(prefixInfoForWordWithPrefix.getPrefix())) {
			PrefixMorphologicalCharacteristicsChangesForPartOfSpeechInfo prefixMorphologicalCharacteristicsChangesForPartOfSpeechInfo = morphologyCharacteristicsChanges.get(prefixInfoForWordWithPrefix.getPrefix());
			if (prefixMorphologicalCharacteristicsChangesForPartOfSpeechInfo == null) {
				prefixMorphologicalCharacteristicsChangesForPartOfSpeechInfo = new PrefixMorphologicalCharacteristicsChangesForPartOfSpeechInfo(tagsForNonDictionaryWordConversion);
			}
			prefixMorphologicalCharacteristicsChangesForPartOfSpeechInfo.add(prefixInfoForCurrentWord, prefixInfoForWordWithPrefix);
			morphologyCharacteristicsChanges.replace(prefixInfoForWordWithPrefix.getPrefix(), prefixMorphologicalCharacteristicsChangesForPartOfSpeechInfo);
		} else {
			PrefixMorphologicalCharacteristicsChangesForPartOfSpeechInfo prefixMorphologicalCharacteristicsChangesForPartOfSpeechInfo = new PrefixMorphologicalCharacteristicsChangesForPartOfSpeechInfo(tagsForNonDictionaryWordConversion);
			prefixMorphologicalCharacteristicsChangesForPartOfSpeechInfo.add(prefixInfoForCurrentWord, prefixInfoForWordWithPrefix);
			morphologyCharacteristicsChanges.put(prefixInfoForWordWithPrefix.getPrefix(), prefixMorphologicalCharacteristicsChangesForPartOfSpeechInfo);
		}
	}

	public synchronized void addPrefix(PrefixInfo prefixInfo) {
		if (prefixInfo.getPrefix() != null && prefixInfo.getPrefix().matches("[а-яА-ЯёЁ-]+") && !morphologyCharacteristicsChanges.containsKey(prefixInfo.getPrefix())) {
			morphologyCharacteristicsChanges.put(prefixInfo.getPrefix(), null);
		}
	}

	public Map<String, PrefixMorphologicalCharacteristicsChangesForPartOfSpeechInfo> getMorphologyCharacteristicsChanges() {
		return morphologyCharacteristicsChanges;
	}
}
