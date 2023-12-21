package ru.textanalysis.tawt.ms.dictionary.non.dictionary.words.prefix;

import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import ru.textanalysis.tawt.ms.dictionary.non.dictionary.words.convertor.TagsForNonDictionaryWordConversion;
import ru.textanalysis.tawt.ms.grammeme.MorfologyParameters;
import ru.textanalysis.tawt.ms.grammeme.MorfologyParametersHelper;

import java.util.*;
import java.util.concurrent.atomic.AtomicReference;

@Slf4j
public class PrefixMorphologicalCharacteristicsChangesForPartOfSpeechInfo {

	@Getter
	private final Map<Byte, MorphologicalCharacteristicsChangesByPrefix> morphologicalCharacteristicsChanges;
	private final TagsForNonDictionaryWordConversion tagsForNonDictionaryWordConversion;

	public PrefixMorphologicalCharacteristicsChangesForPartOfSpeechInfo(TagsForNonDictionaryWordConversion tagsForNonDictionaryWordConversion) {
		this.tagsForNonDictionaryWordConversion = tagsForNonDictionaryWordConversion;
		this.morphologicalCharacteristicsChanges = new HashMap<>();
	}

	public void add(PrefixInfo prefixInfoForCurrentWord, PrefixInfo prefixInfoForWordWithPrefix) {
		Byte typeOfSpeechForCurrentWord = getTypeOfSpeechCode(prefixInfoForCurrentWord);
		Byte typeOfSpeechForWordWithPrefix = getTypeOfSpeechCode(prefixInfoForWordWithPrefix);
		if (Objects.equals(typeOfSpeechForWordWithPrefix, typeOfSpeechForCurrentWord) && (typeOfSpeechForCurrentWord == MorfologyParameters.TypeOfSpeech.VERB || typeOfSpeechForCurrentWord == MorfologyParameters.TypeOfSpeech.INFINITIVE)) {
			List<String> currentWordTags = prefixInfoForCurrentWord.getTags();
			List<String> wordWithPrefixTags = prefixInfoForWordWithPrefix.getTags();
			List<String> uniqueTagsForCurrentWord = new ArrayList<>();
			for (String tag : currentWordTags) {
				if (!wordWithPrefixTags.contains(tag)) {
					uniqueTagsForCurrentWord.add(tag);
				}
			}
			List<String> uniqueTagsForWordWithPrefix = new ArrayList<>();
			for (String tag : wordWithPrefixTags) {
				if (!currentWordTags.contains(tag)) {
					uniqueTagsForWordWithPrefix.add(tag);
				}
			}
			if (this.morphologicalCharacteristicsChanges.containsKey(typeOfSpeechForWordWithPrefix)) {
				MorphologicalCharacteristicsChangesByPrefix morphologicalCharacteristicsChangesByPrefix = morphologicalCharacteristicsChanges.get(typeOfSpeechForWordWithPrefix);
				morphologicalCharacteristicsChangesByPrefix.add(uniqueTagsForWordWithPrefix, uniqueTagsForCurrentWord);
				this.morphologicalCharacteristicsChanges.replace(typeOfSpeechForWordWithPrefix, morphologicalCharacteristicsChangesByPrefix);
			} else {
				MorphologicalCharacteristicsChangesByPrefix morphologicalCharacteristicsChangesByPrefix = new MorphologicalCharacteristicsChangesByPrefix(tagsForNonDictionaryWordConversion);
				morphologicalCharacteristicsChangesByPrefix.add(uniqueTagsForWordWithPrefix, uniqueTagsForCurrentWord);
				if (!morphologicalCharacteristicsChangesByPrefix.getCharacteristicChanges().isEmpty()) {
					this.morphologicalCharacteristicsChanges.put(typeOfSpeechForWordWithPrefix, morphologicalCharacteristicsChangesByPrefix);
				}
			}
		}
	}

	private Byte getTypeOfSpeechCode(PrefixInfo prefixInfo) {
		AtomicReference<Byte> typeOfSpeech = new AtomicReference<>((byte) 0);
		prefixInfo.getTags().forEach(tag -> {
			if (tagsForNonDictionaryWordConversion.getToS().contains(tag)) {
				try {
					typeOfSpeech.set(MorfologyParametersHelper.getTypeOfSpeech(tag.toLowerCase(Locale.ROOT)));
				} catch (Exception e) {
					log.error(String.format("Морфологическая характеристика %s не была найдена", tag), e);
				}
			}
		});
		return typeOfSpeech.get();
	}
}
