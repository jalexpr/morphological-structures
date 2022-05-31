package ru.textanalysis.tawt.ms.dictionary.open.corpora;

import lombok.extern.slf4j.Slf4j;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import javax.management.modelmbean.XMLParseException;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import java.io.File;
import java.util.List;
import java.util.Objects;

/**
 * Предоставляет создание нового xml файла,
 * открытие существующего xml файла в стандарте OpenCorpora,
 * добавление новых лемм в xml файлом
 */
@Slf4j
public class OpenCorporaXmlDocument {

    private Document doc;
    private Element lemmataElement;
    private Element linksElement;
    private int lemmaId;

    /**
     * Instantiates a new Open corpora xml document.
     */
    public OpenCorporaXmlDocument() {
        try {
            DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
            DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();
            this.doc = dBuilder.newDocument();

            Element rootElement = doc.createElement("dictionary");
            doc.appendChild(rootElement);
            Element grammemesElement = doc.createElement("grammemes");
            rootElement.appendChild(grammemesElement);
            Element restrictionsElement = doc.createElement("restrictions");
            rootElement.appendChild(restrictionsElement);
            this.lemmataElement = doc.createElement("lemmata");
            rootElement.appendChild(lemmataElement);
            Element linkTypesElement = doc.createElement("link_types");
            rootElement.appendChild(linkTypesElement);
            this.linksElement = doc.createElement("links");
            rootElement.appendChild(linksElement);

            lemmaId = 100000001;
        } catch (ParserConfigurationException e) {
            log.warn(e.getMessage(), e);
        }
    }

    /**
     * Instantiates a new Open corpora xml document.
     *
     * @param filePath путь до файла xml в стандарте OpenCorpora
     * @param lemmas   List в котором хранятся уже добавленные леммы, для предотвращения дублирования
     */
    public OpenCorporaXmlDocument(String filePath, List<String> lemmas) {
        try {
            DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
            dbFactory.setIgnoringElementContentWhitespace(true);
            dbFactory.setNamespaceAware(true);
            DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();
            this.doc = dBuilder.parse(new File(filePath));
            doc.getDocumentElement().normalize();

            Element rootElement = doc.getDocumentElement();
            deleteEmptyNodes(rootElement);
            if (Objects.equals(rootElement.getNodeName(), "dictionary")) {
                NodeList rootElementChilds = rootElement.getChildNodes();
                for (int i = 0; i < rootElementChilds.getLength(); i++) {
                    Node nNode = rootElementChilds.item(i);
                    if (nNode.getNodeType() == Node.ELEMENT_NODE) {
                        Element eElement = (org.w3c.dom.Element) nNode;
                        if (eElement.getTagName().equals("lemmata")) {
                            this.lemmataElement = eElement;
                            NodeList lemmataElementChilds = lemmataElement.getChildNodes();
                            for (int j = 0; j < lemmataElementChilds.getLength(); j++) {
                                Node lNode = lemmataElementChilds.item(j);
                                if (lNode.getNodeType() == Node.ELEMENT_NODE) {
                                    Element lElement = (org.w3c.dom.Element) lNode;
                                    if (lElement.getTagName().equals("lemma")) {
                                        if (this.lemmaId < Integer.parseInt(lElement.getAttributeNode("id").getValue())) {
                                            this.lemmaId = Integer.parseInt(lElement.getAttributeNode("id").getValue());
                                        }
                                        NodeList lemmaElementChilds = lElement.getChildNodes();
                                        for (int k = 0; k < lemmaElementChilds.getLength(); k++) {
                                            Node tNode = lemmaElementChilds.item(k);
                                            if (tNode.getNodeType() == Node.ELEMENT_NODE) {
                                                Element tElement = (org.w3c.dom.Element) tNode;
                                                if (tElement.getTagName().equals("l")) {
                                                    synchronized (lemmas) {
                                                        lemmas.add(tElement.getAttributeNode("t").getValue());
                                                    }
                                                }
                                            }
                                        }
                                    }
                                }
                            }
                            this.lemmaId++;
                        } else if (eElement.getTagName().equals("links")) {
                            this.linksElement = eElement;
                        }
                    }
                }
            } else {
                throw new XMLParseException();
            }
            if (this.lemmataElement == null || this.linksElement == null) {
                throw new XMLParseException();
            }
            if (lemmaId == 0) {
                lemmaId = 100000001;
            }
        } catch (XMLParseException ex) {
            log.error("Неправильный формат xml файла.", ex);
        } catch (Exception ex) {
            ex.printStackTrace();
            log.error(ex.getMessage(), ex);
        }
    }

    public Document getDoc() {
        return doc;
    }

    public Element getLemmataElement() {
        return lemmataElement;
    }

    public Element getLinksElement() {
        return linksElement;
    }

    public int getLemmaId() {
        return lemmaId;
    }

    public void setLemmaId(int lemmaId) {
        this.lemmaId = lemmaId;
    }

    private void deleteEmptyNodes(org.w3c.dom.Element element) {
        if (element.hasChildNodes()) {
            NodeList nodelist = element.getChildNodes();
            for (int i = nodelist.getLength() - 1; i >= 0; i--) {
                if (nodelist.item(i).getNodeType() != Node.ELEMENT_NODE) {
                    element.removeChild(nodelist.item(i));
                }
            }
            for (int i = 0; i < element.getChildNodes().getLength(); i++) {
                deleteEmptyNodes((org.w3c.dom.Element) element.getChildNodes().item(i));
            }
        }
    }
}