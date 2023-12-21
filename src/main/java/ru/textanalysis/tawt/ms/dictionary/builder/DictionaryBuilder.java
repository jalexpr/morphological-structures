package ru.textanalysis.tawt.ms.dictionary.builder;


public interface DictionaryBuilder {

	boolean addInfo(String word);

	void recordInFile();
}
