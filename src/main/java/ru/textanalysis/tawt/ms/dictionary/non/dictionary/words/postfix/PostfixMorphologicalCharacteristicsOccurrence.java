package ru.textanalysis.tawt.ms.dictionary.non.dictionary.words.postfix;

import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import ru.textanalysis.tawt.ms.grammeme.MorfologyParametersHelper;

import java.util.*;

@Slf4j
@Getter
public class PostfixMorphologicalCharacteristicsOccurrence {

	private final PostfixMorphologicalCharacteristics initialFormPostfix;
	private final Map<String, Integer> map;

	public PostfixMorphologicalCharacteristicsOccurrence(PostfixMorphologicalCharacteristics initialFormPostfix) {
		this.initialFormPostfix = initialFormPostfix;
		this.map = new HashMap<>();
	}

	public void add(String partOfSpeech, List<String> tags) {
		StringBuilder result = new StringBuilder(partOfSpeech);
		if (!tags.isEmpty()) {
			result.append(",").append(String.join(",", tags));
		}
		if (map.containsKey(result.toString())) {
			map.replace(result.toString(), map.get(result.toString()) + 1);
		} else {
			map.put(result.toString(), 1);
		}
	}

	public List<Byte> getPartOfSpeeches() {
		List<Byte> partOfSpeeches = new ArrayList<>();
		Object[] postfixOccurrences = map.entrySet().toArray();
		Arrays.sort(postfixOccurrences, new Comparator() {
			public int compare(Object o1, Object o2) {
				return ((Map.Entry<String, Integer>) o2).getValue()
					.compareTo(((Map.Entry<String, Integer>) o1).getValue());
			}
		});
		try {
			for (Object postfixOccurrence : postfixOccurrences) {
				partOfSpeeches.add(MorfologyParametersHelper.getTypeOfSpeech(((Map.Entry<String, Integer>) postfixOccurrence).getKey().split(",")[0].toLowerCase(Locale.ROOT)));
			}
		} catch (Exception e) {
			log.error("Морфологическая характеристика не была найдена", e);
			return Collections.emptyList();
		}
		return partOfSpeeches;
	}

	public List<Long> getTags() {
		List<Long> tags = new ArrayList<>();
		Object[] postfixOccurrences = map.entrySet().toArray();
		Arrays.sort(postfixOccurrences, new Comparator() {
			public int compare(Object o1, Object o2) {
				return ((Map.Entry<String, Integer>) o2).getValue()
					.compareTo(((Map.Entry<String, Integer>) o1).getValue());
			}
		});
		try {
			for (Object postfixOccurrence : postfixOccurrences) {
				String[] formTags = (((Map.Entry<String, Integer>) postfixOccurrence).getKey().split(","));
				long tagsBits = 0;
				for (int i = 1; i < formTags.length; i++) {
					try {
						long tagBitValue = MorfologyParametersHelper.getParameter(formTags[i]);
						tagsBits |= tagBitValue;
					} catch (NullPointerException ex) {

					}
				}
				tags.add(tagsBits);
			}
		} catch (Exception e) {
			log.error("Морфологическая характеристика не была найдена", e);
			return Collections.emptyList();
		}
		return tags;
	}
}
