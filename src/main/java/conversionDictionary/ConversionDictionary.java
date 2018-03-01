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

package conversionDictionary;

import load.BDSqlite;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileOutputStream;
import java.io.IOException;
import java.lang.reflect.Array;
import java.math.BigInteger;
import java.util.*;
import java.util.logging.Level;
import java.util.logging.Logger;
import load.FileHelper;

import static conversionDictionary.BDSqliteForConversion.closeDB;
import static morphologicalstructures.Property.START_ID_INITIAL_SAVE;

public class ConversionDictionary {

    private static final byte[] CONTROL_VALUE;
    private static BufferedReader readerSourceDictionary;
    private static FileOutputStream streamKeyAndHashAndMorfCharacteristics;
//    private static HashMap<Integer, IdAndString> stringWordFormAndId;
//    private static HashMap<Integer, IdAndString> stringInitialFormAndId;
    private int idInitialSave = START_ID_INITIAL_SAVE;
    private static BDSqlite CONNECT_BD_INITIAL_FORM;
    private static BDSqlite CONNECT_BD_WORD_FORM;
    private static final String FIRST_TAG = "<(/lemma|lemma.?id=\"\\d+\".?rev=\"\\d+\")>";
    private static final String SPLIT_TAG = "</(l|f)>";
    private static final String DELETE_TAG = "(<(l|f|g) (t|v)=\"|/?>)";

    static {
        CONTROL_VALUE = getBytes(PropertyForConversion.CONTROL_VALUE);// new byte[]{-1, -1, -1, -1};
    }

    private ConversionDictionary() {}

    public static void conversionDictionary(String sourceDictionaryPath, String encoding) {
        initFiles(sourceDictionaryPath, encoding);
        conversionDictionary();
        closeFiles();
    }

    //TODO:PATH_KEY_HASH_AND_MORF_CHARACTERISTICS in ZIP
    private static void initFiles(String sourceDictionaryPath, String encoding) {
        readerSourceDictionary = FileHelper.openBufferedReaderStream(sourceDictionaryPath, encoding);
        streamKeyAndHashAndMorfCharacteristics = FileHelper.openFileInputStream(PropertyForConversion.PATH_KEY_HASH_AND_MORF_CHARACTERISTICS);
        CONNECT_BD_INITIAL_FORM = new BDSqlite(PropertyForConversion.PATH_BD_INITIAL_FORM);
        CONNECT_BD_WORD_FORM = new BDSqlite(PropertyForConversion.PATH_BD_WORD_FORM);
    }

    private static void conversionDictionary() {
        searchFirstLemma();
        conversionLemmas();

        /**
         * проводит в единный формат для конвертации
         * Написать метод, который распознает значение характеристики и переводит в шкалу.
         * Для начальных формы записать в БД, записать в файл в формате ключ от БД, хэш-код, часть речи характеристика
         * Для производный форм, проверить в мапе существует ли похожый хэш,
         * если сущесвтует, то прерить одинаковый ли стринг,
         *  если нет, то вывести в лог,
         *  если да, то записать файл в формате ключ в БД (берем из мапы) хэшкод, характиристика
         * если не сущевтует, то добавить в БД, добавить в мап где ключ это хэшкод а значение ключ в БД
         *  и записать в файл
         * проверить слово на йо, если да, то повторить операцию выше, но ключ берется тот же для слова, а не создается новый.
         *
         * повторят пока не пройдем все лемы
         *
         * закрыть все соединения
        **/
    }

    private static void searchFirstLemma() {
        while(!FileHelper.readLine(readerSourceDictionary).trim().equals("<lemmata>")){}
    }

    private static void conversionLemmas() {
        while(FileHelper.ready(readerSourceDictionary)) {
            String stringLemma = FileHelper.readLine(readerSourceDictionary);
            List<Form> forms = conversionLemma(stringLemma);
        }
    }

    public static List<Form> conversionLemma(String lemmaString) {
        List<Form> formList = new LinkedList<>();
        for(String tag : getTagLemma(lemmaString)) {
            formList.add(createdForm(tag.replaceAll(DELETE_TAG, "").split("\"")));
        }
        return formList;
    }

    public static String[] getTagLemma(String lemmaString) {
        String lemmaStringTemp = lemmaString.trim().toLowerCase().replaceAll(FIRST_TAG, "");
        return lemmaStringTemp.split(SPLIT_TAG);
    }

    public static Form createdForm(String[] stringCharacteristics) {
        Form form = new Form();
        form.setStringName(stringCharacteristics[0]);
        form.setPartOfSpeec(getPartOfSpeech(stringCharacteristics));
        form.setMorfCharacteristics(getMorfCharacteristics(stringCharacteristics));
        return form;
    }

    public static byte getPartOfSpeech(String[] stringPartOfSpeech) {
        if(stringPartOfSpeech.length > 0) {
            return stringPartOfSpeech[1];
        }
        return 0;
    }

    public static long getMorfCharacteristics(String[] stringCharacteristics) {
        for(int i = 2; i < stringCharacteristics.length; i++) {

        }
        return 0;
    }

    private static HashMap<Integer, IdAndString> generateMapIdAndString(BufferedReader outReader) {
        HashMap<Integer, IdAndString> mapStringAndId = new HashMap<>();
        try {
            int id = 0;
            mapStringAndId.put(0, new IdAndString("??_??_???????", 0));
            while (outReader.ready()) {
                id++;
                String word = outReader.readLine();
                IdAndString stringAndID = new IdAndString(word, id);
                mapStringAndId.put(stringAndID.hashCode(), stringAndID);
                if(word.matches("ё")) {
                    stringAndID = new IdAndString(word.replace("ё", "e"), id);
                    mapStringAndId.put(stringAndID.hashCode(), stringAndID);
                }
            }
        } catch (IOException ex) {
            Logger.getLogger(ConversionDictionary.class.getName()).log(Level.SEVERE, null, ex);
        }

        return mapStringAndId;
    }


    public static int saveFormInBd(String stringForm, boolean isInitialForm) {
        return 0;
    }

    public static void closeFiles() {
        FileHelper.closeFile(readerSourceDictionary);
        FileHelper.closeFile(streamKeyAndHashAndMorfCharacteristics);
        closeDB();
    }

    public static void main(String[] args) {
        String old = " <lemma id=\"1\" rev=\"1\"><l t=\"ёж\"><g v=\"NOUN\"/><g v=\"anim\"/><g v=\"masc\"/></l><f t=\"ёж\"><g v=\"sing\"/><g v=\"nomn\"/></f><f t=\"ежа\"><g v=\"sing\"/><g v=\"gent\"/></f><f t=\"ежу\"><g v=\"sing\"/><g v=\"datv\"/></f><f t=\"ежа\"><g v=\"sing\"/><g v=\"accs\"/></f><f t=\"ежом\"><g v=\"sing\"/><g v=\"ablt\"/></f><f t=\"еже\"><g v=\"sing\"/><g v=\"loct\"/></f><f t=\"ежи\"><g v=\"plur\"/><g v=\"nomn\"/></f><f t=\"ежей\"><g v=\"plur\"/><g v=\"gent\"/></f><f t=\"ежам\"><g v=\"plur\"/><g v=\"datv\"/></f><f t=\"ежей\"><g v=\"plur\"/><g v=\"accs\"/></f><f t=\"ежами\"><g v=\"plur\"/><g v=\"ablt\"/></f><f t=\"ежах\"><g v=\"plur\"/><g v=\"loct\"/></f></lemma>";
        System.out.print(conversionLemma(old));
    }



}
