package ru.textanalysis.tawt.ms.dictionary.non.dictionary.words.prefix;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@Slf4j
@Getter
@AllArgsConstructor
public class PrefixInfo {

	private String prefix;
	private String word;
	private List<String> tags;

	public PrefixInfo(String openCorporaWordForm) {
		String[] openCorporaWordParts;
		try {
			if (openCorporaWordForm.length() == 0) {
				throw new Exception("Неверный формат словоформы");
			}
			openCorporaWordParts = openCorporaWordForm.split("\t");
			if (openCorporaWordParts.length != 3) {
				throw new Exception("Неверный формат словоформы " + openCorporaWordForm);
			}
		} catch (Exception e) {
			log.info(e.getMessage(), e);
			return;
		}
		this.prefix = openCorporaWordParts[0];
		this.word = openCorporaWordParts[1];
		String[] openCorporaWordTags = openCorporaWordParts[2].split(",");
		this.tags = new ArrayList<>();
		this.tags.addAll(Arrays.asList(openCorporaWordTags));
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
		try {
			StringBuilder result = new StringBuilder(this.prefix + "\t" + this.word + "\t" + this.tags.get(0));
			for (int i = 1; i < this.tags.size(); i++) {
				result.append(",");
				result.append(this.tags.get(i));
			}
			return result.toString();
		} catch (Exception e) {
			log.error("Не удалось получить токен.", e);
			return "";
		}
	}
}