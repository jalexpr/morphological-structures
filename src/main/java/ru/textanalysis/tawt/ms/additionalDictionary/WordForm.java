package ru.textanalysis.tawt.ms.additionalDictionary;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Хранение слова и его тегов
 */
class WordForm {

    private static final Logger log = Logger.getLogger(WordForm.class.getName());
    private String word;
    private List<String> tags;

    /**
     * Instantiates a new Word form.
     *
     * @param OpenCorporaWordForm слово и его теги в стандарте OpenCorpora
     */
    public WordForm(String OpenCorporaWordForm) {
        try {
            if (OpenCorporaWordForm.length() == 0) {
                throw new Exception("Неверный формат словоформы");
            }
            String[] wordTags = OpenCorporaWordForm.split("\t");
            if (wordTags.length != 2) {
                throw new Exception("Неверный формат словоформы");
            }
            this.word = wordTags[0];
            String[] tags = wordTags[1].split(",");
            this.tags = new ArrayList<>();
            this.tags.addAll(Arrays.asList(tags));
        } catch (Exception e) {
            log.log(Level.INFO, e.getMessage(), e);
        }
    }

    public String getWord() {
        return word;
    }

    public List<String> getTags() {
        return tags;
    }

    /**
     * проверка, содержится в WordForm необходимый тег
     *
     * @param str проверяемое значение
     *
     * @return the boolean
     */
    public boolean contains(String str) {
        return this.toString().contains(str);
    }

    @Override
    public String toString() {
        try {
            StringBuilder result = new StringBuilder(this.word + "\t" + this.tags.get(0));
            for (int i = 1; i < this.tags.size(); i++) {
                result.append(",");
                result.append(this.tags.get(i));
            }
            return result.toString();
        } catch (Exception e) {
            String messages = "Не удалось получить токен.";
            log.log(Level.SEVERE, messages, e);
            return "";
        }
    }
}