package ru.textanalysis.tawt.ms.additionalDictionary;

import org.w3c.dom.Attr;

import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Добавление в xml файл новых лемм
 */
class XmlDocumentLemmaCreator {

    private static final Logger log = Logger.getLogger(XmlDocumentLemmaCreator.class.getName());
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
     * @param forms       добавляемые формы
     */
    public void addLemmaForms(List<List<WordForm>> forms) {
        synchronized (xmlDocument) {
            for (List<WordForm> form : forms) {
                addLemma(form);
                if (form.get(0).contains("INFN")) {
                    org.w3c.dom.Element linkElement = createNewElement(xmlDocument.getLinksElement(),
                            xmlDocument.getLemmaId(), xmlDocument.getLemmaId() - 1, 3);
                }
            }
        }
    }

    private org.w3c.dom.Element createNewElement(org.w3c.dom.Element parentElement, String tagName, String attributeName, String attributeValue) {
        org.w3c.dom.Element newElement = xmlDocument.getDoc().createElement(tagName);
        parentElement.appendChild(newElement);
        Attr attr = xmlDocument.getDoc().createAttribute(attributeName);
        attr.setValue(attributeValue);
        newElement.setAttributeNode(attr);

        return newElement;
    }

    private org.w3c.dom.Element createNewElement(org.w3c.dom.Element parentElement, int fromIndex, int toIndex, int linkType) {
        org.w3c.dom.Element newElement = xmlDocument.getDoc().createElement("link");
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

    private void addLemma(List<WordForm> forms) {
        try {
            if (forms != null && forms.size() > 0) {
                if (!wiktionaryTagsData.getToS().contains(forms.get(0).getTags().get(0))) {
                    throw new Exception("Неверный формат токена.");
                }

                org.w3c.dom.Element lemmaElement = createNewElement(xmlDocument.getLemmataElement(), "lemma", "id", String.valueOf(xmlDocument.getLemmaId()));

                org.w3c.dom.Element lElement = createNewElement(lemmaElement, "l", "t", forms.get(0).getWord());

                List<String> overallTags;
                if (forms.get(0).getTags().size() > 0) {
                    overallTags = new ArrayList<>(forms.get(0).getTags());
                } else {
                    throw new Exception("Пустой токен.");
                }

                for (int i = 1; i < forms.size(); i++) {
                    List<String> derivitiveTags = new ArrayList<>(forms.get(i).getTags());
                    for (int j = overallTags.size() - 1; j >= 0; j--) {
                        if (!derivitiveTags.contains(overallTags.get(j))) {
                            overallTags.remove(j);
                        }
                    }
                }

                org.w3c.dom.Element gElement;
                for (String overallTag : overallTags) {
                    gElement = createNewElement(lElement, "g", "v", overallTag);
                }

                for (WordForm form : forms) {
                    org.w3c.dom.Element fElement = createNewElement(lemmaElement, "f", "t", form.toString().split("\t")[0]);
                    List<String> derivitiveTags = new ArrayList<>(form.getTags());
                    for (int k = derivitiveTags.size() - 1; k >= 0; k--) {
                        if (overallTags.contains(derivitiveTags.get(k))) {
                            derivitiveTags.remove(k);
                        }
                    }
                    for (String derivitiveTag : derivitiveTags) {
                        gElement = createNewElement(fElement, "g", "v", derivitiveTag);
                    }
                }

                xmlDocument.setLemmaId(xmlDocument.getLemmaId() + 1);
            }
        } catch (Exception e) {
            log.log(Level.SEVERE, e.getMessage(), e);
        }
    }
}