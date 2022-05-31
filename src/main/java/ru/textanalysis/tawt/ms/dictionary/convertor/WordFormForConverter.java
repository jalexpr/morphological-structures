package ru.textanalysis.tawt.ms.dictionary.convertor;

import lombok.extern.slf4j.Slf4j;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static ru.textanalysis.tawt.ms.constant.Const.COMMA_SEPARATOR;
import static ru.textanalysis.tawt.ms.constant.Const.TAB_SEPARATOR;

/**
 * Хранение слова и его тегов
 */
@Slf4j
public class WordFormForConverter {

    private String word;
    private List<String> tags;

    /**
     * Instantiates a new Word form.
     *
     * @param OpenCorporaWordForm слово и его теги в стандарте OpenCorpora
     */
    public WordFormForConverter(String OpenCorporaWordForm) {
        try {
            if (OpenCorporaWordForm.length() == 0) {
                throw new Exception("Неверный формат словоформы");
            }
            String[] wordTags = OpenCorporaWordForm.split(TAB_SEPARATOR);
            if (wordTags.length != 2) {
                throw new Exception("Неверный формат словоформы");
            }
            this.word = wordTags[0];
            String[] tags = wordTags[1].split(COMMA_SEPARATOR);
            this.tags = new ArrayList<>();
            this.tags.addAll(Arrays.asList(tags));
        } catch (Exception ex) {
            log.info(ex.getMessage(), ex);
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
            StringBuilder result = new StringBuilder(this.word + TAB_SEPARATOR + this.tags.get(0));
            for (int i = 1; i < this.tags.size(); i++) {
                result.append(COMMA_SEPARATOR);
                result.append(this.tags.get(i));
            }
            return result.toString();
        } catch (Exception ex) {
            log.error("Не удалось получить токен.", ex);
            return "";
        }
    }
}