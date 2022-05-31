/*
 * Copyright (C) 2017  Alexander Porechny alex.porechny@mail.ru
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the Attribution-NonCommercial-ShareAlike 3.0 Unported
 * (CC BY-SA 3.0) as published by the Creative Commons.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * Attribution-NonCommercial-ShareAlike 3.0 Unported (CC BY-SA 3.0)
 * for more details.
 *
 * You should have received a copy of the Attribution-NonCommercial-ShareAlike
 * 3.0 Unported (CC BY-SA 3.0) along with this program.
 * If not, see <https://creativecommons.org/licenses/by-nc-sa/3.0/legalcode>
 *
 * Thanks to Sergey Politsyn and Katherine Politsyn for their help in the development of the library.
 *
 *
 * Copyright (C) 2017 Александр Поречный alex.porechny@mail.ru
 *
 * Эта программа свободного ПО: Вы можете распространять и / или изменять ее
 * в соответствии с условиями Attribution-NonCommercial-ShareAlike 3.0 Unported
 * (CC BY-SA 3.0), опубликованными Creative Commons.
 *
 * Эта программа распространяется в надежде, что она будет полезна,
 * но БЕЗ КАКИХ-ЛИБО ГАРАНТИЙ; без подразумеваемой гарантии
 * КОММЕРЧЕСКАЯ ПРИГОДНОСТЬ ИЛИ ПРИГОДНОСТЬ ДЛЯ ОПРЕДЕЛЕННОЙ ЦЕЛИ.
 * См. Attribution-NonCommercial-ShareAlike 3.0 Unported (CC BY-SA 3.0)
 * для более подробной информации.
 *
 * Вы должны были получить копию Attribution-NonCommercial-ShareAlike 3.0
 * Unported (CC BY-SA 3.0) вместе с этой программой.
 * Если нет, см. <https://creativecommons.org/licenses/by-nc-sa/3.0/legalcode>
 *
 * Благодарим Сергея и Екатерину Полицыных за оказание помощи в разработке библиотеки.
 */
package ru.textanalysis.tawt.ms.conversion.dictionary;

import lombok.extern.slf4j.Slf4j;
import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import ru.textanalysis.tawt.ms.loader.DatabaseFactory;
import ru.textanalysis.tawt.ms.loader.DatabaseLemmas;
import ru.textanalysis.tawt.ms.loader.DatabaseStrings;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import java.io.File;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.util.*;

import static ru.textanalysis.tawt.ms.constant.Const.COMMA_SEPARATOR;
import static ru.textanalysis.tawt.ms.constant.Const.TAB_SEPARATOR;
import static ru.textanalysis.tawt.ms.constant.TypeOfSpeechs.INFN;
import static ru.textanalysis.tawt.ms.constant.TypeOfSpeechs.VERB;

@Slf4j
public class ConversionDictionary {

    private final DatabaseStrings databaseStrings = DatabaseFactory.getInstanceDatabaseStrings();
    private final DatabaseLemmas databaseLemmas = DatabaseFactory.getInstanceDatabaseLemmas();

    public static void main(String[] args) {
        ConversionDictionary conversionDictionary = new ConversionDictionary();
        conversionDictionary.conversionDictionary("dict.opcorpora.xml", StandardCharsets.UTF_8);
    }

    public void conversionDictionary(String sourceDictionaryPath, Charset encoding, String... additionalDictionaryPaths) {
        List<List<FormForConversion>> lemmas = convertLemmasFromInitDictionary(sourceDictionaryPath, encoding, additionalDictionaryPaths);
        databaseLemmas.recreate(lemmas);
        databaseStrings.recreate(lemmas);

        databaseStrings.compression();
        databaseLemmas.compression();
    }

    private List<List<FormForConversion>> convertLemmasFromInitDictionary(String sourceDictionaryPath, Charset encoding, String... additionalDictionaryPaths) {
        List<String> dictionaryPaths = new ArrayList<>();
        dictionaryPaths.add(sourceDictionaryPath);
        dictionaryPaths.addAll(Arrays.asList(additionalDictionaryPaths));
        List<List<FormForConversion>> lemmas = new ArrayList<>();
        HashMap<Integer, List<FormForConversion>> lemmasMap = new HashMap<>();
        HashMap<Integer, List<String>> verbs = new HashMap<>();

        try {
            DocumentBuilder documentBuilder = DocumentBuilderFactory.newInstance().newDocumentBuilder();
            for (String dictionaryPath : dictionaryPaths) {
                Document document = documentBuilder.parse(new File(dictionaryPath).toURI().toString());

                Node dictionary = document.getDocumentElement();
                NodeList dictionaryProps = dictionary.getChildNodes();
                for (int i = 0; i < dictionaryProps.getLength(); i++) {
                    Node node = dictionaryProps.item(i);
                    if (node.getNodeType() != Node.TEXT_NODE && (node.getNodeName().equals("grammemes") || node.getNodeName().equals("restrictions") || node.getNodeName().equals("link_types"))) {
                        while (node.hasChildNodes()) {
                            node.removeChild(node.getFirstChild());
                        }
                    }
                }
                for (int i = 0; i < dictionaryProps.getLength(); i++) {
                    Node lemmata = dictionaryProps.item(i);
                    if (lemmata.getNodeType() != Node.TEXT_NODE && lemmata.getNodeName().equals("lemmata")) {
                        NodeList lemmataProps = lemmata.getChildNodes();
                        for (int j = 0; j < lemmataProps.getLength(); j++) {
                            Node lemma = lemmataProps.item(j);
                            if (lemma.getNodeType() != Node.TEXT_NODE && lemma.getNodeName().equals("lemma")) {
                                String commonCharacteristics = "";
                                int formNumber = 0;
                                boolean isVerb = false;
                                List<String> verbInfn = new ArrayList<>();
                                List<FormForConversion> wordLemma = new LinkedList<>();
                                NodeList lemmaProps = lemma.getChildNodes();
                                for (int k = 0; k < lemmaProps.getLength(); k++) {
                                    Node value = lemmaProps.item(k);
                                    if (value.getNodeType() != Node.TEXT_NODE && value.getNodeName().equals("l")) {
                                        NodeList valueProps = value.getChildNodes();
                                        for (int m = 0; m < valueProps.getLength(); m++) {
                                            Node characteristic = valueProps.item(m);
                                            if (characteristic.getNodeType() != Node.TEXT_NODE && characteristic.getNodeName().equals("g")) {
                                                commonCharacteristics += characteristic.getAttributes().getNamedItem("v").getNodeValue();
                                                commonCharacteristics += COMMA_SEPARATOR;
                                            }
                                        }
                                        if (commonCharacteristics.length() > 0) {
                                            commonCharacteristics = commonCharacteristics.substring(0, commonCharacteristics.length() - 1);
                                        }
                                    } else if (value.getNodeType() != Node.TEXT_NODE && value.getNodeName().equals("f")) {
                                        StringBuilder formCharacteristics = new StringBuilder(value.getAttributes().getNamedItem("t").getNodeValue());
                                        formCharacteristics.append(TAB_SEPARATOR);
                                        formCharacteristics.append(commonCharacteristics);
                                        NodeList valueProps = value.getChildNodes();
                                        for (int m = 0; m < valueProps.getLength(); m++) {
                                            Node characteristic = valueProps.item(m);
                                            if (characteristic.getNodeType() != Node.TEXT_NODE && characteristic.getNodeName().equals("g")) {
                                                formCharacteristics.append(COMMA_SEPARATOR);
                                                formCharacteristics.append(characteristic.getAttributes().getNamedItem("v").getNodeValue());
                                            }
                                        }
                                        if (formCharacteristics.toString().contains(INFN) || formCharacteristics.toString().contains(VERB)) {
                                            isVerb = true;
                                            verbInfn.add(formCharacteristics.toString());
                                        } else {
                                            if (formNumber == 0) {
                                                FormForConversion initialForm = createForm(formCharacteristics.toString(), true);
                                                wordLemma.add(initialForm);
                                            } else {
                                                wordLemma.add(createForm(formCharacteristics.toString(), false));
                                            }
                                            formNumber++;
                                        }
                                    }
                                }
                                if (isVerb) {
                                    verbs.put(Integer.valueOf(lemma.getAttributes().getNamedItem("id").getNodeValue()), verbInfn);
                                } else {
                                    lemmasMap.put(Integer.valueOf(lemma.getAttributes().getNamedItem("id").getNodeValue()), wordLemma);
                                }
                                while (lemma.hasChildNodes()) {
                                    lemma.removeChild(lemma.getFirstChild());
                                }
                            }
                        }
                    } else if (lemmata.getNodeType() != Node.TEXT_NODE && lemmata.getNodeName().equals("links")) {
                        NodeList lemmataProps = lemmata.getChildNodes();
                        for (int j = 0; j < lemmataProps.getLength(); j++) {
                            Node lemma = lemmataProps.item(j);
                            if (lemma.getNodeType() != Node.TEXT_NODE && lemma.getNodeName().equals("link")) {
                                if (Objects.equals(lemma.getAttributes().getNamedItem("type").getNodeValue(), "3")) {
                                    List<FormForConversion> wordLemma = new LinkedList<>();
                                    List<String> infn = verbs.get(Integer.parseInt(lemma.getAttributes().getNamedItem("from").getNodeValue()));
                                    FormForConversion initialForm = createForm(infn.get(0).replaceAll(INFN, VERB), true);
                                    wordLemma.add(initialForm);
                                    List<String> verb = verbs.get(Integer.parseInt(lemma.getAttributes().getNamedItem("to").getNodeValue()));
                                    verb.forEach(form -> {
                                        FormForConversion derivativeForm = createForm(form, false);
                                        wordLemma.add(derivativeForm);
                                    });
                                    lemmasMap.put(Integer.valueOf(lemma.getAttributes().getNamedItem("from").getNodeValue()), wordLemma);
                                } else if (!Objects.equals(lemma.getAttributes().getNamedItem("type").getNodeValue(), "11")) {
                                    if (lemmasMap.containsKey(Integer.parseInt(lemma.getAttributes().getNamedItem("to").getNodeValue()))) {
                                        lemmasMap.get(Integer.parseInt(lemma.getAttributes().getNamedItem("to").getNodeValue())).forEach(formLemma -> {
                                            if (lemmasMap.containsKey(Integer.parseInt(lemma.getAttributes().getNamedItem("from").getNodeValue()))) {
                                                formLemma.setLink(lemmasMap.get(Integer.parseInt(lemma.getAttributes().getNamedItem("from").getNodeValue())).get(0).hashCode(),
                                                        lemmasMap.get(Integer.parseInt(lemma.getAttributes().getNamedItem("from").getNodeValue())).get(0).getKey());
                                            }
                                        });
                                    }
                                }
                                while (lemma.hasChildNodes()) {
                                    lemma.removeChild(lemma.getFirstChild());
                                }
                            }
                        }
                    }
                }
            }
            lemmas = new ArrayList<>(lemmasMap.values());
            lemmasMap.clear();
            verbs.clear();
            lemmas.add(addSupportYo(lemmas));
        } catch (Exception ex) {
            log.error("Ошибка при чтении файла. Файл: {}", sourceDictionaryPath, ex);
        }
        return lemmas;
    }

    private List<FormForConversion> addSupportYo(List<List<FormForConversion>> lemmas) {
        boolean firstYo = true;
        List<FormForConversion> yoLemma = new LinkedList<>();
        for (List<FormForConversion> lemma : lemmas) {
            for (FormForConversion formForConversion : lemma) {
                if (formForConversion.getStringName().contains("ё")) {
                    String curLemmaString = formForConversion.getStringName().split(TAB_SEPARATOR)[0];
                    if (firstYo) {
                        FormForConversion yoForm = createForm(curLemmaString.replaceAll("ё", "е"), true);
                        yoForm.setLink(formForConversion.hashCode(), formForConversion.getKey());
                        yoLemma.add(yoForm);
                        firstYo = false;
                    } else {
                        FormForConversion yoForm = createForm(curLemmaString.replaceAll("ё", "е"), false);
                        yoForm.setLink(formForConversion.hashCode(), formForConversion.getKey());
                        yoLemma.add(yoForm);
                    }
                }
            }
        }
        return yoLemma;
    }

    private FormForConversion createForm(String line, boolean isInitialForm) {
        String[] parameters = line.toLowerCase(Locale.ROOT).split(TAB_SEPARATOR);
        FormForConversion form = new FormForConversion(parameters[0], isInitialForm);
        if (parameters.length > 1) {
            form.setCharacteristics(parameters[1].split("[, ]"));
        } else {
            form.setCharacteristics(new String[0]);
        }
        return form;
    }
}
