package ru.textanalysis.tawt.ms.dictionary.open.corpora;

import lombok.extern.slf4j.Slf4j;
import org.w3c.dom.Attr;
import org.w3c.dom.Element;
import ru.textanalysis.tawt.ms.dictionary.convertor.WiktionaryTagsData;
import ru.textanalysis.tawt.ms.dictionary.convertor.WordFormForConverter;

import java.util.ArrayList;
import java.util.List;

import static ru.textanalysis.tawt.ms.constant.Const.TAB_SEPARATOR;
import static ru.textanalysis.tawt.ms.constant.TypeOfSpeechs.*;

/**
 * Добавление в xml файл новых лемм
 */
@Slf4j
public class XmlDocumentLemmaCreator {

    private final WiktionaryTagsData wiktionaryTagsData;
    private final OpenCorporaXmlDocument xmlDocument;

    /**
     * Instantiates a new Xml document lemma creator.
     *
     * @param wiktionaryTagsData объект с информацией о преобразовании тегов
     */
    public XmlDocumentLemmaCreator(WiktionaryTagsData wiktionaryTagsData, OpenCorporaXmlDocument xmlDocument) {
        this.wiktionaryTagsData = wiktionaryTagsData;
        this.xmlDocument = xmlDocument;
    }

    /**
     * Добавление новых лемм в xml файл
     *
     * @param forms добавляемые формы
     */
    public void addLemmaForms(List<List<WordFormForConverter>> forms) {
        for (int i = 0; i < forms.size(); i++) {
            if (forms.get(i).get(0).contains(VERB)) {
                continue;
            }
            if (forms.get(i).get(0).contains(INFN) && i > 0 && forms.get(i - 1).get(0).contains(VERB)) {
                synchronized (xmlDocument) {
                    addLemma(forms.get(i - 1));
                    addLemma(forms.get(i));
                    Element linkElement = createNewElement(xmlDocument.getLinksElement(), xmlDocument.getLemmaId() - 1, xmlDocument.getLemmaId() - 2, 3);
                }
            } else {
                synchronized (xmlDocument) {
                    addLemma(forms.get(i));
                }
            }
            if (forms.get(i).get(0).contains(ADJS) && i > 0 && forms.get(i - 1).get(0).contains(ADJF)) {
                synchronized (xmlDocument) {
                    Element linkElement = createNewElement(xmlDocument.getLinksElement(), xmlDocument.getLemmaId() - 2, xmlDocument.getLemmaId() - 1, 1);
                }
            } else if (forms.get(i).get(0).contains(PRTS) && i > 0 && forms.get(i - 1).get(0).contains(PRTF)) {
                synchronized (xmlDocument) {
                    Element linkElement = createNewElement(xmlDocument.getLinksElement(), xmlDocument.getLemmaId() - 2, xmlDocument.getLemmaId() - 1, 6);
                }
            }
        }
    }

    private Element createNewElement(Element parentElement, String tagName, String attributeName, String attributeValue) {
        Element newElement = xmlDocument.getDoc().createElement(tagName);
        parentElement.appendChild(newElement);
        Attr attr = xmlDocument.getDoc().createAttribute(attributeName);
        attr.setValue(attributeValue);
        newElement.setAttributeNode(attr);

        return newElement;
    }

    private Element createNewElement(Element parentElement, int fromIndex, int toIndex, int linkType) {
        Element newElement = xmlDocument.getDoc().createElement("link");
        parentElement.appendChild(newElement);
        Attr firstAttr = xmlDocument.getDoc().createAttribute("from");
        firstAttr.setValue(String.valueOf(fromIndex));
        newElement.setAttributeNode(firstAttr);
        Attr secondAttr = xmlDocument.getDoc().createAttribute("to");
        secondAttr.setValue(String.valueOf(toIndex));
        newElement.setAttributeNode(secondAttr);
        Attr thirdAttr = xmlDocument.getDoc().createAttribute("type");
        thirdAttr.setValue(String.valueOf(linkType));
        newElement.setAttributeNode(thirdAttr);

        return newElement;
    }

    private void addLemma(List<WordFormForConverter> forms) {
        try {
            if (forms != null && forms.size() > 0) {
                if (!wiktionaryTagsData.getToS().contains(forms.get(0).getTags().get(0))) {
                    throw new Exception("Неверный формат токена.");
                }

                Element lemmaElement = createNewElement(xmlDocument.getLemmataElement(), "lemma", "id", String.valueOf(xmlDocument.getLemmaId()));

                Element lElement = createNewElement(lemmaElement, "l", "t", forms.get(0).getWord());

                List<String> overallTags;
                if (forms.get(0).getTags().size() > 0) {
                    overallTags = new ArrayList<>(forms.get(0).getTags());
                } else {
                    throw new Exception("Пустой токен.");
                }

                for (int i = 1; i < forms.size(); i++) {
                    List<String> derivitiveTags = new ArrayList<>(forms.get(i).getTags());
                    overallTags.retainAll(derivitiveTags);
                }

                Element gElement;
                for (String overallTag : overallTags) {
                    gElement = createNewElement(lElement, "g", "v", overallTag);
                }

                for (WordFormForConverter form : forms) {
                    Element fElement = createNewElement(lemmaElement, "f", "t", form.toString().split(TAB_SEPARATOR)[0]);
                    List<String> derivitiveTags = new ArrayList<>(form.getTags());
                    derivitiveTags.removeAll(overallTags);
                    for (String derivitiveTag : derivitiveTags) {
                        gElement = createNewElement(fElement, "g", "v", derivitiveTag);
                    }
                }

                xmlDocument.setLemmaId(xmlDocument.getLemmaId() + 1);
            }
        } catch (NullPointerException ex) {
            log.error("Неверный формат токена.", ex);
        } catch (Exception ex) {
            log.error(ex.getMessage(), ex);
        }
    }
}