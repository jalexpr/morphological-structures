package ru.textanalysis.tawt.ms.dictionary.additional.words.builder;

import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import ru.textanalysis.tawt.ms.dictionary.additional.words.convertor.TagsForOpenCorporaDictionaryConversion;
import ru.textanalysis.tawt.ms.dictionary.additional.words.convertor.WordFormForConverter;
import ru.textanalysis.tawt.ms.dictionary.additional.words.open.corpora.OpenCorporaXmlDocument;
import ru.textanalysis.tawt.ms.dictionary.additional.words.open.corpora.XmlDocumentLemmaCreator;
import ru.textanalysis.tawt.ms.dictionary.additional.words.parser.WiktionaryWordsInfoParser;
import ru.textanalysis.tawt.ms.dictionary.builder.DictionaryBuilder;

import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Основной класс
 * Создание нового xml файла или открытие существующего
 * Получение словоформ в стандарте OpenCorpora из Wiktionary
 * Добавление новых лемм в xml файл
 * Запись информации в xml файл
 */
@Slf4j
public class XmlBuilder implements DictionaryBuilder {

    private final String DEFAULT_RESULT_FILE_PATH = "/result.xml";

    private final OpenCorporaXmlDocument xmlDocument;
    private final XmlDocumentLemmaCreator lemmaCreator;
    private final TagsForOpenCorporaDictionaryConversion tagsForOpenCorporaDictionaryConversion;
    private final WiktionaryWordsInfoParser wiktionaryWordsInfoParser;
    private final List<String> lemmas;
    @Setter
    private String resultFilePath;

    /**
     * Instantiates a new Xml builder.
     */
    public XmlBuilder() {
        this.resultFilePath = DEFAULT_RESULT_FILE_PATH;
        this.lemmas = Collections.synchronizedList(new ArrayList<>());
        xmlDocument = new OpenCorporaXmlDocument();
        tagsForOpenCorporaDictionaryConversion = new TagsForOpenCorporaDictionaryConversion();
        lemmaCreator = new XmlDocumentLemmaCreator(tagsForOpenCorporaDictionaryConversion, xmlDocument);
        wiktionaryWordsInfoParser = new WiktionaryWordsInfoParser(tagsForOpenCorporaDictionaryConversion, this.lemmas);
    }

    /**
     * Instantiates a new Xml builder.
     *
     * @param filePath путь до файла xml в формате OpenCorpora
     */
    public XmlBuilder(String filePath) {
        this.lemmas = Collections.synchronizedList(new ArrayList<>());
        xmlDocument = new OpenCorporaXmlDocument(filePath, this.lemmas);
        tagsForOpenCorporaDictionaryConversion = new TagsForOpenCorporaDictionaryConversion();
        lemmaCreator = new XmlDocumentLemmaCreator(tagsForOpenCorporaDictionaryConversion, xmlDocument);
        wiktionaryWordsInfoParser = new WiktionaryWordsInfoParser(tagsForOpenCorporaDictionaryConversion, this.lemmas);
    }

    /**
     * Получение информации о слове и его тегах в стандарте OpenCorpora
     *
     * @param word           исследуемое слово
     * @return the words tags
     */
    @Override
    public boolean addInfo(String word) {
		List<List<WordFormForConverter>> forms = wiktionaryWordsInfoParser.getInfo(word).getResponse();
        if (!forms.isEmpty()) {
            addLemmaForms(forms);
            return true;
        }
        return false;
    }

    /**
     * Добавление новой формы в xml файл
     *
     * @param forms список форм в стандарте OpenCorpora
     */
    private void addLemmaForms(List<List<WordFormForConverter>> forms) {
        synchronized (lemmaCreator) {
            lemmaCreator.addLemmaForms(forms);
        }
    }

    /**
     * Записывает информацию в xml файл
     */
    @Override
    public synchronized void recordInFile() {
        recordInFile(this.resultFilePath);
    }

    /**
     * Записывает информацию в xml файл
     *
     * @param filePath путь до файла, в который будет идти запись
     */
    public synchronized void recordInFile(String filePath) {
        try {
            TransformerFactory transformerFactory = TransformerFactory.newInstance();
            Transformer transformer = transformerFactory.newTransformer();
            transformer.setOutputProperty(OutputKeys.INDENT, "yes");
            transformer.setOutputProperty(OutputKeys.ENCODING, "UTF-8");
            transformer.setOutputProperty("{http://xml.apache.org/xslt}indent-amount", "2");

            DOMSource domSource = new DOMSource(xmlDocument.getDoc());
            StreamResult streamResult = new StreamResult(new File(filePath));

            transformer.transform(domSource, streamResult);
        } catch (TransformerException ex) {
            log.error("Ошибка записи в файл.", ex);
        }
    }
}