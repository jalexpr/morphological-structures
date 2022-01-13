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
package ru.textanalysis.tawt.ms.model;

import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.xml.sax.SAXException;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;

public final class Property {

	public final static String MY_REPOSITORY = "https://github.com/jalexpr/MorphologicalStructures";
	public final static String FOLDER = "dictionary/";
	public final static String NAME_BD_DERIVATIVE_FORM = "dictionary.derivativeFormString.db";
	public final static String NAME_BD_INITIAL_FORM = "dictionary.initialFormString.db";
	public final static String NAME_HASH_AND_MORF_CHARACTERISTICS = "dictionary.format.morfCharacteristic";
	public final static int CONTROL_VALUE = -1;
	public final static int KEY_OFFSET = 8;
	public final static int START_ID_INITIAL_FORM = 1 << KEY_OFFSET;
	public final static int START_ID_DERIVATIVE_FORM = 524287 << KEY_OFFSET;
	public final static byte CONTROL_OFFSET = 23;
	public final static String MOVE_TO_NEW_LINE = System.lineSeparator();
	public final static String VERSION = "2021.12.02";

	static {
//		loadProperty();
	}

	public static void loadProperty() {
		try {
			DocumentBuilder documentBuilder = DocumentBuilderFactory.newInstance().newDocumentBuilder();
			Document document = documentBuilder.parse("property.xml");
			Node root = document.getDocumentElement();
			readProperty(root);
		} catch (ParserConfigurationException | SAXException | IOException ex) {
			String messages = "Не удается найти property.xml\r\nПрименены параметры по умолчанию!\r\n";
			Logger.getLogger(Property.class.getName()).log(Level.WARNING, messages);
		}
	}

	public static void readProperty(Node root) {

//        NodeList propertys = root.getChildNodes();
//        for (int i = 0; i < propertys.getLength(); i++) {
//            Node node = propertys.item(i);
//            if (node.getNodeType() != Node.TEXT_NODE) {
//                switch (node.getNodeName()) {
//                    case "pathNumber":
//                        pathNumber = node.getChildNodes().item(0).getTextContent();
//                        break;
//                    case "pathHashAndMorfCharacteristics":
//                        pathHashAndMorfCharacteristics = node.getChildNodes().item(0).getTextContent();
//                        break;
//                    case "pathInitialFormString":
//                        pathInitialFormString = node.getChildNodes().item(0).getTextContent();
//                        break;
//                    case "pathWordFormString":
//                        pathWordFormString = node.getChildNodes().item(0).getTextContent();
//                        break;
//                    case "pathZipDictionary":
//                        pathZipDictionary = node.getChildNodes().item(0).getTextContent();
//                        break;
//                    case "encoding":
//                        encoding = node.getChildNodes().item(0).getTextContent();
//                        break;
//                    default:
//                        readProperty(node);
//                }
//            }
//        }
	}
}
