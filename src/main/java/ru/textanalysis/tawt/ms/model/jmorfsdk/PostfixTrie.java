package ru.textanalysis.tawt.ms.model.jmorfsdk;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class PostfixTrie implements Trie {

	private final TrieNode root;

	public PostfixTrie() {
		root = new TrieNode();
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void insert(String postfix) {
		TrieNode current = root;

		for (int i = postfix.length() - 1; i >= 0; --i) {
			current = current.getChildren().computeIfAbsent(postfix.charAt(i), c -> new TrieNode());
		}
		current.setEndOfWord(true);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public boolean contains(String postfix) {
		TrieNode current = root;
		for (int i = postfix.length() - 1; i >= 0; --i) {
			char ch = postfix.charAt(i);
			TrieNode node = current.getChildren().get(ch);
			if (node == null) {
				return false;
			}
			current = node;
		}
		return current.isEndOfWord();
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public String findLongest(String word) {
		String postfix = "";
		TrieNode current = root;

		for (int i = word.length() - 1; i >= 1; --i) {
			char ch = word.charAt(i);
			TrieNode node = current.getChildren().get(ch);
			if (node == null) {
				break;
			}
			if (node.isEndOfWord()) {
				postfix = word.substring(i);
			}
			current = node;
		}
		return postfix;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public List<String> findAll(String word) {
		List<String> postfixes = new ArrayList<>();
		TrieNode current = root;

		for (int i = word.length() - 1; i >= 1; --i) {
			char ch = word.charAt(i);
			TrieNode node = current.getChildren().get(ch);
			if (node == null) {
				break;
			}
			if (node.isEndOfWord()) {
				postfixes.add(word.substring(i));
			}
			current = node;
		}
		Collections.reverse(postfixes);
		return postfixes;
	}
}
