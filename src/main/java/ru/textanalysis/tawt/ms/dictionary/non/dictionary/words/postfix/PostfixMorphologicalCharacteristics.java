package ru.textanalysis.tawt.ms.dictionary.non.dictionary.words.postfix;

import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import ru.textanalysis.tawt.ms.grammeme.MorfologyParametersHelper;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@Slf4j
@Getter
public class PostfixMorphologicalCharacteristics {

	private String postfix;
	private String partOfSpeech;
	private List<String> tags;
	@Setter
	private Integer recordNumber = null;

	public PostfixMorphologicalCharacteristics(PostfixMorphologicalCharacteristics postfixMorphologicalCharacteristics) {
		this.postfix = postfixMorphologicalCharacteristics.getPostfix();
		this.partOfSpeech = postfixMorphologicalCharacteristics.getPartOfSpeech();
		this.tags = postfixMorphologicalCharacteristics.getTags();
	}

	public PostfixMorphologicalCharacteristics(String postfix, String partOfSpeech, List<String> tags) {
		this.postfix = postfix;
		this.partOfSpeech = partOfSpeech;
		this.tags = tags;
	}

	public PostfixMorphologicalCharacteristics(String openCorporaWordForm) {
		String[] openCorporaWordParts;
		try {
			if (openCorporaWordForm.length() == 0) {
				throw new Exception("Неверный формат словоформы");
			}
			openCorporaWordParts = openCorporaWordForm.split("\t");
			if (openCorporaWordParts.length != 2) {
				throw new Exception("Неверный формат словоформы");
			}
		} catch (Exception e) {
			log.info(e.getMessage(), e);
			return;
		}
		this.postfix = openCorporaWordParts[0];
		String[] openCorporaWordTags = openCorporaWordParts[1].split(",");
		this.partOfSpeech = openCorporaWordTags[0];
		this.tags = new ArrayList<>();
		this.tags.addAll(Arrays.asList(openCorporaWordTags).subList(1, openCorporaWordTags.length));
	}

	public Long getTagsLong() {
		long tagsBits = 0;
		try {
			for (String tag : tags) {
				try {
					long morphologyParameterLong = MorfologyParametersHelper.getParameter(tag);
					tagsBits |= morphologyParameterLong;
				} catch (NullPointerException ex) {

				}
			}
		} catch (Exception e) {
			log.error("Морфологическая характеристика не была найдена", e);
		}
		return tagsBits;
	}

	/**
	 * проверка, содержится ли искомый тег в тегах
	 *
	 * @param tag проверяемое значение
	 *
	 * @return true - содержится, false - нет
	 */
	public boolean contains(String tag) {
		return this.toString().contains(tag);
	}

	@Override
	public String toString() {
		StringBuilder result = new StringBuilder(this.postfix + "\t" + this.partOfSpeech);
		if (this.tags != null && !this.tags.isEmpty()) {
			result.append("\t");
			result.append(String.join(",", this.tags));
		}
		return result.toString();
	}
}