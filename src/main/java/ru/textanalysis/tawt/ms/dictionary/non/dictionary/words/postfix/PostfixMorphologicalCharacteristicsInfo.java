package ru.textanalysis.tawt.ms.dictionary.non.dictionary.words.postfix;


import lombok.Getter;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Getter
public class PostfixMorphologicalCharacteristicsInfo {

	private final Map<String, PostfixMorphologicalCharacteristicsOccurrence> morphologicalCharacteristicsOccurrences;

	public PostfixMorphologicalCharacteristicsInfo() {
		morphologicalCharacteristicsOccurrences = new HashMap<>();
	}

	public synchronized void add(PostfixInfo postfixInfo) {
		add(postfixInfo.getInitialFormPostfix(), postfixInfo.getAllFormsPostfix());
	}

	private void add(PostfixMorphologicalCharacteristics initialFormPostfix, List<PostfixMorphologicalCharacteristics> getAllFormsPostfix) {
		for (PostfixMorphologicalCharacteristics formPostfix : getAllFormsPostfix) {
			if (initialFormPostfix != null && formPostfix.getPostfix() != null && formPostfix.getPartOfSpeech() != null && formPostfix.getTags() != null) {
				add(initialFormPostfix,
					formPostfix.getPostfix(),
					formPostfix.getPartOfSpeech(),
					formPostfix.getTags());
			}
		}
	}

	private void add(PostfixMorphologicalCharacteristics initialFormPostfix, String postfix, String partOfSpeech, List<String> tags) {
		if (postfix.matches("[а-яА-ЯёЁ-]+")) {
			if (morphologicalCharacteristicsOccurrences.containsKey(postfix)) {
				PostfixMorphologicalCharacteristicsOccurrence postfixMorphologicalCharacteristicsOccurrence = morphologicalCharacteristicsOccurrences.get(postfix);
				postfixMorphologicalCharacteristicsOccurrence.add(partOfSpeech, tags);
				morphologicalCharacteristicsOccurrences.replace(postfix, postfixMorphologicalCharacteristicsOccurrence);
			} else {
				PostfixMorphologicalCharacteristicsOccurrence postfixMorphologicalCharacteristicsOccurrence = new PostfixMorphologicalCharacteristicsOccurrence(initialFormPostfix);
				postfixMorphologicalCharacteristicsOccurrence.add(partOfSpeech, tags);
				morphologicalCharacteristicsOccurrences.put(postfix, postfixMorphologicalCharacteristicsOccurrence);
			}
		}
	}
}
