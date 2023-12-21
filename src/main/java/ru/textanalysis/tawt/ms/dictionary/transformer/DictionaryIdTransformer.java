package ru.textanalysis.tawt.ms.dictionary.transformer;

import lombok.extern.slf4j.Slf4j;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import ru.textanalysis.tawt.ms.constant.OpenCorporaDictionaryConst;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Slf4j
public class DictionaryIdTransformer {

    public static void main(String[] args) {
        DictionaryIdTransformer dictionaryIdTransformer = new DictionaryIdTransformer();
        List<String> additionalDictionaryPaths = new ArrayList<>();
        additionalDictionaryPaths.add("additionalDictionary.xml");
        additionalDictionaryPaths.add("additionalDictionary2.xml");
        dictionaryIdTransformer.transformDictionary("dict.opcorpora.xml", additionalDictionaryPaths);
    }

    public void transformDictionary(String sourceDictionaryPath, List<String> additionalDictionaryPaths) {
        transformIdInDictionary(sourceDictionaryPath, additionalDictionaryPaths);
    }

    private void transformIdInDictionary(String sourceDictionaryPath, List<String> additionalDictionaryPaths) {
        if (additionalDictionaryPaths.isEmpty()) {
            return;
        }

        try {
            DocumentBuilder documentBuilder = DocumentBuilderFactory.newInstance().newDocumentBuilder();
            Document openCorporaDictionaryDocument = documentBuilder.parse(new File(sourceDictionaryPath).toURI().toString());
            long idOffset = getOpenCorporaDictionaryMaxId(openCorporaDictionaryDocument);

            Map<Long, Long> idConversion = new HashMap<>();
            long lemmaCounter = 0;

            for (String dictionaryPath : additionalDictionaryPaths) {
                Document dictionaryDocument = documentBuilder.parse(new File(dictionaryPath).toURI().toString());

                Node dictionary = dictionaryDocument.getDocumentElement();
                NodeList dictionaryProps = dictionary.getChildNodes();
                for (int i = 0; i < dictionaryProps.getLength(); i++) {
                    Node lemmata = dictionaryProps.item(i);
                    if (lemmata.getNodeType() != Node.TEXT_NODE && lemmata.getNodeName().equals(OpenCorporaDictionaryConst.LEMMATA)) {
                        NodeList lemmataProps = lemmata.getChildNodes();
                        for (int j = 0; j < lemmataProps.getLength(); j++) {
                            Node lemma = lemmataProps.item(j);
                            if (lemma.getNodeType() != Node.TEXT_NODE && lemma.getNodeName().equals(OpenCorporaDictionaryConst.LEMMA)) {
                                lemmaCounter++;
                                String id = lemma.getAttributes().getNamedItem(OpenCorporaDictionaryConst.ID).getNodeValue();
                                long converterIdValue = idOffset + lemmaCounter;
                                idConversion.put(Long.parseLong(id), converterIdValue);
                                ((Element) lemma).setAttribute(OpenCorporaDictionaryConst.ID, String.valueOf(converterIdValue));
                            }
                        }
                    } else if (lemmata.getNodeType() != Node.TEXT_NODE && lemmata.getNodeName().equals(OpenCorporaDictionaryConst.LINKS)) {
                        NodeList lemmataProps = lemmata.getChildNodes();
                        for (int j = 0; j < lemmataProps.getLength(); j++) {
                            Node lemma = lemmataProps.item(j);
                            if (lemma.getNodeType() != Node.TEXT_NODE && lemma.getNodeName().equals(OpenCorporaDictionaryConst.LINK)) {
                                String fromId = lemma.getAttributes().getNamedItem(OpenCorporaDictionaryConst.FROM).getNodeValue();
                                long fromIdConvertedValue = idConversion.get(Long.parseLong(fromId));
                                ((Element) lemma).setAttribute(OpenCorporaDictionaryConst.FROM, String.valueOf(fromIdConvertedValue));
                                String toId = lemma.getAttributes().getNamedItem(OpenCorporaDictionaryConst.TO).getNodeValue();
                                long toIdConvertedValue = idConversion.get(Long.parseLong(toId));
                                ((Element) lemma).setAttribute(OpenCorporaDictionaryConst.TO, String.valueOf(toIdConvertedValue));
                            }
                        }
                    }
                }

                idOffset += lemmaCounter;
                lemmaCounter = 0;
                idConversion.clear();

                TransformerFactory transformerFactory = TransformerFactory.newInstance();
                Transformer transformer = transformerFactory.newTransformer();
                transformer.setOutputProperty(OutputKeys.ENCODING, "UTF-8");
                transformer.setOutputProperty("{http://xml.apache.org/xslt}indent-amount", "2");

                DOMSource domSource = new DOMSource(dictionaryDocument);
                StreamResult streamResult = new StreamResult(new File(dictionaryPath));

                transformer.transform(domSource, streamResult);
            }

        } catch (Exception ex) {
            log.error("Ошибка при чтении файла. Файл: {}", sourceDictionaryPath, ex);
        }
    }

    private long getOpenCorporaDictionaryMaxId(Document openCorporaDocument) {
        long idOffset = 0;

        Node dictionary = openCorporaDocument.getDocumentElement();
        NodeList dictionaryProps = dictionary.getChildNodes();
        for (int i = 0; i < dictionaryProps.getLength(); i++) {
            Node lemmata = dictionaryProps.item(i);
            if (lemmata.getNodeType() != Node.TEXT_NODE && lemmata.getNodeName().equals(OpenCorporaDictionaryConst.LEMMATA)) {
                NodeList lemmataProps = lemmata.getChildNodes();
                for (int j = 0; j < lemmataProps.getLength(); j++) {
                    Node lemma = lemmataProps.item(j);
                    if (lemma.getNodeType() != Node.TEXT_NODE && lemma.getNodeName().equals(OpenCorporaDictionaryConst.LEMMA)) {
                        String id = lemma.getAttributes().getNamedItem(OpenCorporaDictionaryConst.ID).getNodeValue();
                        long currentId = Long.parseLong(id);
                        if (currentId > idOffset) {
                            idOffset = currentId;
                        }
                    }
                }
            }
        }

        return idOffset;
    }
}
