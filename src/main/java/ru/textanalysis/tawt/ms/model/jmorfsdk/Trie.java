package ru.textanalysis.tawt.ms.model.jmorfsdk;

import java.util.List;

public interface Trie {

	void insert(String word);

	boolean contains(String word);

	String findLongest(String word);

	List<String> findAll(String word);
}
