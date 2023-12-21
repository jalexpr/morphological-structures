package ru.textanalysis.tawt.ms.model.jmorfsdk;

import lombok.Getter;

import java.util.HashMap;
import java.util.Map;

public class TrieNode {

	@Getter
	private Map<Character, TrieNode> children;
	private boolean isWord;

	public TrieNode() {
		children = new HashMap<>();
	}

	public void setEndOfWord(boolean endOfWord) {
		isWord = endOfWord;
	}

	public boolean isEndOfWord() {
		return isWord;
	}
}
