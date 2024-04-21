package ru.textanalysis.tawt.ms.model.jmorfsdk;

import java.util.List;

public interface Trie {

	/**
	 * @param flexion флексия для добавления
	 *
	 * Добавляет в лес новую флексию
	 */
	void insert(String flexion);

	/**
	 * @param flexion слово для поиска
	 *
	 * Проверяет, что флексия добавлена в лес, а также то, что она является конечной, а не промежуточной
	 *
	 * @return true - найдена в лесу, иначе false
	 */
	boolean contains(String flexion);

	/**
	 * @param word слово для поиска
	 *
	 * Возвращает самую длинную флексию (приставку или окончание в зависимости от реализации) для данного слова
	 *
	 * @return Самая длинная флексия, найденная в словаре, подходящая для анализируемого слова
	 */
	String findLongest(String word);

	/**
	 * @param word слово для поиска
	 *
	 * Возвращает все найденные в словаре флексии (приставки или окончания в зависимости от реализации) для данного слова
	 *
	 * @return Список существующих флексий, найденных для анализируемого слова
	 */
	List<String> findAll(String word);
}
