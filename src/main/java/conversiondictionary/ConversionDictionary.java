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

package conversiondictionary;

import java.io.BufferedReader;
import java.io.FileOutputStream;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import load.BDFormString;
import load.FileHelper;

import static load.BDFormString.compressionBd;

public class ConversionDictionary {

    private static final byte[] CONTROL_VALUE = getPrimitiveBytes(PropertyForConversion.CONTROL_VALUE);
    private static BufferedReader readerSourceDictionary;
    private static FileOutputStream streamKeyAndHashAndMorfCharacteristics;
    private static BDSqliteForConversion bds = new BDSqliteForConversion();

    private ConversionDictionary() {}

    public static void conversionDictionary(String sourceDictionaryPath, String encoding) {
        initFiles(sourceDictionaryPath, encoding);
        conversionDictionary();
        closeFiles();
    }

    private static void initFiles(String sourceDictionaryPath, String encoding) {
        readerSourceDictionary = FileHelper.openBufferedReaderStream(sourceDictionaryPath, encoding);
        streamKeyAndHashAndMorfCharacteristics = FileHelper.openFileOutputStream(PropertyForConversion.PATH_KEY_HASH_AND_MORF_CHARACTERISTICS);
    }

    private static void conversionDictionary() {
        conversionLemmas(readerSourceDictionary);
        bds.closeBDs();
        compressionBd();
//        TODO:compressionFile();
//        TODO:проверить слово на йо, если да, то повторить операцию выше, но ключ берется тот же для слова, а не создается новый.
    }

    private static void conversionLemmas(BufferedReader readerSourceDictionary) {
        List<Form> lemma;
        while(FileHelper.ready(readerSourceDictionary)) {
            lemma = conversionLemma(readerSourceDictionary);
            saveLemma(lemma);
        }
    }

    private static List<Form> conversionLemma(BufferedReader readerSourceDictionary) {
        List<Form> forms = new LinkedList<>();
        FileHelper.readLine(readerSourceDictionary);
        Form initialForm = createForm(FileHelper.readLine(readerSourceDictionary), true);
        forms.add(initialForm);

        while(FileHelper.ready(readerSourceDictionary)) {
            String line = FileHelper.readLine(readerSourceDictionary);
            if(line.trim().isEmpty()) {
                break;
            }
            forms.add(createForm(line, false));
        }

        return forms;
    }

    private static Form createForm(String line, boolean isInitialForm) {
        String[] parameters = line.split("\t");
        Form form = new Form(parameters[0], isInitialForm);
        if(parameters.length > 1) {
            form.setCharacteristics(parameters[1].split(","));
        }
        return form;
    }

    private static void saveLemma(List<Form> lemma) {
        lemma.forEach((form -> {
            saveForm(form);
        }));
    }

    public static void saveForm(Form form) {
        if(!form.isExistInBd()) {
            bds.saveInBD(form);
        }
        saveInFile(form);
    }

    public static void saveInFile(Form form) {
        FileHelper.write(streamKeyAndHashAndMorfCharacteristics, plusByte(form.getByteFileFormat(), CONTROL_VALUE));
    }

    public static byte[] plusByte(byte[] arrA, byte elmB) {
        byte[] arrC = new byte[arrA.length + 1];
        System.arraycopy(arrA, 0, arrC, 0, arrA.length);
        arrC[arrA.length] = elmB;
        return arrC;
    }

    public static byte[] plusByte(byte[] arrA, byte[] arrB) {
        byte[] arrC = new byte[arrA.length + arrB.length];
        System.arraycopy(arrA, 0, arrC, 0, arrA.length);
        System.arraycopy(arrB, 0, arrC, arrA.length, arrB.length);
        return arrC;
    }

    public static byte[] getBytes(int value) {
        byte[] bytes = new byte[]{
                (byte) (value >> 24),
                (byte) (value >> 16),
                (byte) (value >> 8),
                (byte) (value)
        };
        return bytes;
    }

    public static byte[] getPrimitiveBytes(int value) {
        byte[] bytes = new byte[]{
                (byte) (value >> 24),
                (byte) (value >> 16),
                (byte) (value >> 8),
                (byte) (value)
        };
        return bytes;
    }


    public static byte[] getBytes(long value) {
        byte[] bytes = new byte[]{
                (byte) (value >> 56),
                (byte) (value >> 48),
                (byte) (value >> 40),
                (byte) (value >> 32),
                (byte) (value >> 24),
                (byte) (value >> 16),
                (byte) (value >> 8),
                (byte) (value)
        };
        return bytes;
    }

    public static byte getPartOfSpeech(String[] stringPartOfSpeech) {
        if(stringPartOfSpeech.length > 0) {
            return 0;
        }
        return 0;
    }

    public static long getMorfCharacteristics(String[] stringCharacteristics) {
        for(int i = 2; i < stringCharacteristics.length; i++) {

        }
        return 0;
    }

    public static void closeFiles() {
        FileHelper.closeFile(readerSourceDictionary);
        FileHelper.closeFile(streamKeyAndHashAndMorfCharacteristics);
    }

    public static void main(String[] args) {
//        String old = " <lemma id=\"1\" rev=\"1\"><l t=\"ёж\"><g v=\"NOUN\"/><g v=\"anim\"/><g v=\"masc\"/></l><f t=\"ёж\"><g v=\"sing\"/><g v=\"nomn\"/></f><f t=\"ежа\"><g v=\"sing\"/><g v=\"gent\"/></f><f t=\"ежу\"><g v=\"sing\"/><g v=\"datv\"/></f><f t=\"ежа\"><g v=\"sing\"/><g v=\"accs\"/></f><f t=\"ежом\"><g v=\"sing\"/><g v=\"ablt\"/></f><f t=\"еже\"><g v=\"sing\"/><g v=\"loct\"/></f><f t=\"ежи\"><g v=\"plur\"/><g v=\"nomn\"/></f><f t=\"ежей\"><g v=\"plur\"/><g v=\"gent\"/></f><f t=\"ежам\"><g v=\"plur\"/><g v=\"datv\"/></f><f t=\"ежей\"><g v=\"plur\"/><g v=\"accs\"/></f><f t=\"ежами\"><g v=\"plur\"/><g v=\"ablt\"/></f><f t=\"ежах\"><g v=\"plur\"/><g v=\"loct\"/></f></lemma>";
        ConversionDictionary.conversionDictionary("C:/TFWWT/MorphologicalStructures/dict.opcorpora.txt", "UTF-8");
    }

    protected static class Form {

        public static final Map<String, Integer> STRING_INTEGER_WORD_FORM_MAP = new HashMap<>();
        public static final Map<String, Integer> STRING_INTEGER_INITIAL_FORM_MAP = new HashMap<>();
        private static final int START_ID_WORD_FORM = BDFormString.START_ID_WORD_FORM;

        private String stringName;
        private int key;
        private byte partOfSpeech;
        private byte[] morfCharacteristics;
        private boolean isExistInBd;

        public Form(String stringName, boolean isInitialForm) {
            this.stringName = stringName.toLowerCase();
            key = createKey(isInitialForm);
        }

        public String getStringName() {
            return stringName;
        }

        public void setCharacteristics(String[] characteristics) {
            setPartOfSpeech(conversionPartOfSpeech(characteristics[0]));
            setMorfCharacteristics(getBytes(conversionMorfCharacteristics(characteristics)));
        }

        private void setPartOfSpeech(byte partOfSpeech) {
            this.partOfSpeech = partOfSpeech;
        }

        private byte getPartOfSpeech() {
            return partOfSpeech;
        }

        private static byte conversionPartOfSpeech(String partOfSpeech) {
            return 0;
        }

        private static long conversionMorfCharacteristics(String[] morfCharacteristics) {
            return 0;
        }

        private void setMorfCharacteristics(byte[] morfCharacteristics) {
            this.morfCharacteristics = morfCharacteristics;
        }

        private byte[] getMorfCharacteristics() {
            return morfCharacteristics;
        }

        private void setKey(int key) {
            this.key = key;
        }

        public int getKey() {
            return key;
        }

        public boolean isExistInBd() {
            return isExistInBd;
        }

        private int createKey(boolean isInitialForm) {
            if(isInitialForm) {
                return createKey(STRING_INTEGER_INITIAL_FORM_MAP, 1);
            } else {
                return createKey(STRING_INTEGER_WORD_FORM_MAP, START_ID_WORD_FORM);
            }
        }

        private int createKey(Map<String, Integer> stringFormMap, int startKey) {
            if(stringFormMap.containsKey(getStringName())) {
                isExistInBd = true;
                return stringFormMap.get(getStringName());
            } else {
                isExistInBd = false;
                int key = startKey + stringFormMap.size();
                stringFormMap.put(getStringName(), key);
                return key;
            }
        }

        public boolean isInitialForm() {
            return getKey() < START_ID_WORD_FORM;
        }

        public byte[] getByteFileFormat() {
            byte[] hashCode = getBytes(getStringName().hashCode());
            byte[] bytesFormat = plusByte(hashCode, getBytes(getKey()));
            if(isInitialForm()) {
                bytesFormat = plusByte(bytesFormat, getPartOfSpeech());
            }
            bytesFormat = plusByte(bytesFormat, getMorfCharacteristics());
            return bytesFormat;
        }

    }

}
