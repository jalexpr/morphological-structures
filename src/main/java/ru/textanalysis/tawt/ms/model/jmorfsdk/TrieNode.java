package ru.textanalysis.tawt.ms.model.jmorfsdk;

import lombok.Getter;

import java.util.HashMap;
import java.util.Map;

public class TrieNode {

	@Getter
	private Map<Character, TrieNode> children;

	/**
	 * флаг конечности
	 */
	private boolean isWord;

	public TrieNode() {
		children = new HashMap<>();
	}

	/**
	 * Установка флага, обозначающего конечность флексии
	 *
	 * Пример: есть приставка "по", в лес добавляется две ноды "п" -> "о". Надо "п" не является конечным, так как такой флексии не существует.
	 * В то же время на "о" будет поставлен флаг конечности, так как весь путь "по" является существующей флексией.
	 */
	public void setEndOfWord(boolean endOfWord) {
		isWord = endOfWord;
	}

	/**
	 * true если флексия является конечной, иначе - false
	 */
	public boolean isEndOfWord() {
		return isWord;
	}
}
