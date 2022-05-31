package ru.textanalysis.tawt.ms.dictionary.open.corpora;

import lombok.extern.slf4j.Slf4j;
import ru.textanalysis.tawt.ms.dictionary.convertor.WiktionaryTagsData;
import ru.textanalysis.tawt.ms.dictionary.convertor.WordFormForConverter;
import ru.textanalysis.tawt.ms.dictionary.wiktionary.WiktionaryInfoParser;

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
public class XmlBuilder {

    private final OpenCorporaXmlDocument xmlDocument;
    private final XmlDocumentLemmaCreator lemmaCreator;
    private final WiktionaryTagsData wiktionaryTagsData;
    private final WiktionaryInfoParser wiktionaryInfoParser;
    private final List<String> lemmas;

    /**
     * Instantiates a new Xml builder.
     */
    public XmlBuilder() {
        this.lemmas = Collections.synchronizedList(new ArrayList<>());
        xmlDocument = new OpenCorporaXmlDocument();
        wiktionaryTagsData = new WiktionaryTagsData();
        lemmaCreator = new XmlDocumentLemmaCreator(wiktionaryTagsData, xmlDocument);
        wiktionaryInfoParser = new WiktionaryInfoParser(wiktionaryTagsData);
    }

    /**
     * Instantiates a new Xml builder.
     *
     * @param filePath путь до файла xml в формате OpenCorpora
     */
    public XmlBuilder(String filePath) {
        this.lemmas = Collections.synchronizedList(new ArrayList<>());
        xmlDocument = new OpenCorporaXmlDocument(filePath, this.lemmas);
        wiktionaryTagsData = new WiktionaryTagsData();
        lemmaCreator = new XmlDocumentLemmaCreator(wiktionaryTagsData, xmlDocument);
        wiktionaryInfoParser = new WiktionaryInfoParser(wiktionaryTagsData);
    }

    /**
     * Получение информации о слове и его тегах в стандарте OpenCorpora
     *
     * @param word           исследуемое слово
     * @param sleepTime      время между запросами
     * @param requestTimeOut время ожидания подключения
     * @return the words tags
     */
    public List<List<WordFormForConverter>> getWordsTags(String word, int sleepTime, int requestTimeOut) {
        return wiktionaryInfoParser.getWordsTags(word, sleepTime, requestTimeOut, lemmas);
    }

    /**
     * Добавление новой формы в xml файл
     *
     * @param forms список форм в стандарте OpenCorpora
     */
    public void addLemmaForms(List<List<WordFormForConverter>> forms) {
        synchronized (lemmaCreator) {
            lemmaCreator.addLemmaForms(forms);
        }
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