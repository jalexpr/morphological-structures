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
package morphological.structures.conversion.dictionary;

import java.io.BufferedReader;
import java.io.FileOutputStream;
import java.io.OutputStream;
import java.util.*;
import java.util.logging.Level;
import java.util.logging.Logger;

import morphological.structures.grammeme.MorfologyParameters;
import morphological.structures.load.BDFormString;
import template.wrapper.classes.FileHelper;

import static morphological.structures.conversion.dictionary.PropertyForConversion.PATH_KEY_HASH_AND_MORF_CHARACTERISTICS;
import static morphological.structures.conversion.dictionary.PropertyForConversion.DIR_DICTIONARY;
import static morphological.structures.grammeme.MorfologyParametersHelper.getParameter;
import static morphological.structures.grammeme.MorfologyParametersHelper.getTypeOfSpeech;
import static morphological.structures.load.BDFormString.compressionBd;
import static template.wrapper.classes.FileHelper.zipCompressFile;

public class ConversionDictionary {

    private static final byte[] CONTROL_VALUE = getPrimitiveBytes(PropertyForConversion.CONTROL_VALUE);
    private static BufferedReader readerSourceDictionary;
    private static FileOutputStream streamKeyAndHashAndMorfCharacteristics;
    private static BDSqliteForConversion bds;

    private ConversionDictionary() {}

    public static void conversionDictionary(String sourceDictionaryPath, String encoding) {
        initFiles(sourceDictionaryPath, encoding);
        conversionAndSaveDictionary();
        closeFiles();
        compressionBd();
        compressionFile();
        closeFiles();
    }

    private static void initFiles(String sourceDictionaryPath, String encoding) {
        readerSourceDictionary = FileHelper.openBufferedReaderStream(sourceDictionaryPath, encoding);
        streamKeyAndHashAndMorfCharacteristics = FileHelper.openFileOutputStream(PATH_KEY_HASH_AND_MORF_CHARACTERISTICS);
        bds = new BDSqliteForConversion();
    }

    private static void conversionAndSaveDictionary() {
        conversionAndSaveLemmas(readerSourceDictionary);
//        TODO:проверить слово на йо, если да, то повторить операцию выше, но ключ берется тот же для слова, а не создается новый.
    }

    private static void conversionAndSaveLemmas(BufferedReader readerSourceDictionary) {
        List<FormForConversion> lemma;
        while(FileHelper.ready(readerSourceDictionary)) {
            lemma = conversionLemma(readerSourceDictionary);
            saveLemma(lemma);
        }
    }

    private static List<FormForConversion> conversionLemma(BufferedReader readerSourceDictionary) {
        List<FormForConversion> forms = new LinkedList<>();
        FileHelper.readLine(readerSourceDictionary);
        FormForConversion initialForm = createForm(FileHelper.readLine(readerSourceDictionary), true);
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

    private static FormForConversion createForm(String line, boolean isInitialForm) {
        String[] parameters = line.toLowerCase().split("\t");
        FormForConversion form = new FormForConversion(parameters[0], isInitialForm);
        if(parameters.length > 1) {
            form.setCharacteristics(parameters[1].split("[, ]"));
        }
        return form;
    }

    private static void saveLemma(List<FormForConversion> lemma) {
        saveLemmaInBd(lemma);
        saveLemmaInFile(streamKeyAndHashAndMorfCharacteristics, lemma);
    }

    private static void saveLemmaInBd(List<FormForConversion> lemma) {
        lemma.forEach((form) -> {
            if(!form.isExistInBd()) {
                bds.saveInBD(form);
            }
        });
    }

    private static void saveLemmaInFile(OutputStream fileOutput, List<FormForConversion> lemma) {
        lemma.forEach((form) -> {
            saveInFile(fileOutput, form);
        });
        saveInFile(fileOutput, CONTROL_VALUE);
    }

    private static void saveInFile(OutputStream fileOutput, FormForConversion form) {
        saveInFile(fileOutput, form.getByteFileFormat());
    }

    private static void saveInFile(OutputStream fileOutput, byte[] bytse) {
        FileHelper.write(fileOutput, bytse);
    }

    public static void saveLemmasInFile(String pathFile, List<List<FormForConversion>> lemmas) {
        OutputStream fileOutput = FileHelper.openFileOutputStream(DIR_DICTIONARY + pathFile);
        lemmas.forEach((lemma) -> {
            saveLemmaInFile(fileOutput, lemma);
        });
        FileHelper.closeFile(fileOutput);
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

    private static void closeFiles() {
        FileHelper.closeFile(readerSourceDictionary);
        FileHelper.closeFile(streamKeyAndHashAndMorfCharacteristics);
        bds.closeBDs();
    }

    private static void compressionFile() {
        zipCompressFile(PATH_KEY_HASH_AND_MORF_CHARACTERISTICS, PATH_KEY_HASH_AND_MORF_CHARACTERISTICS.split("/")[0]);
    }

    public static void main(String[] args) {
//        String old = " <lemma id=\"1\" rev=\"1\"><l t=\"ёж\"><g v=\"NOUN\"/><g v=\"anim\"/><g v=\"masc\"/></l><f t=\"ёж\"><g v=\"sing\"/><g v=\"nomn\"/></f><f t=\"ежа\"><g v=\"sing\"/><g v=\"gent\"/></f><f t=\"ежу\"><g v=\"sing\"/><g v=\"datv\"/></f><f t=\"ежа\"><g v=\"sing\"/><g v=\"accs\"/></f><f t=\"ежом\"><g v=\"sing\"/><g v=\"ablt\"/></f><f t=\"еже\"><g v=\"sing\"/><g v=\"loct\"/></f><f t=\"ежи\"><g v=\"plur\"/><g v=\"nomn\"/></f><f t=\"ежей\"><g v=\"plur\"/><g v=\"gent\"/></f><f t=\"ежам\"><g v=\"plur\"/><g v=\"datv\"/></f><f t=\"ежей\"><g v=\"plur\"/><g v=\"accs\"/></f><f t=\"ежами\"><g v=\"plur\"/><g v=\"ablt\"/></f><f t=\"ежах\"><g v=\"plur\"/><g v=\"loct\"/></f></lemma>";
        ConversionDictionary.conversionDictionary("dict.opcorpora.txt", "UTF-8");
    }

    public static class FormForConversion {

        private static final Map<String, Integer> STRING_INTEGER_WORD_FORM_MAP = new HashMap<>();
        private static final Map<String, Integer> STRING_INTEGER_INITIAL_FORM_MAP = new HashMap<>();
        private static final int START_ID_INITIAL_FORM = BDFormString.START_ID_INITIAL_FORM;
        private static final int START_ID_WORD_FORM = BDFormString.START_ID_WORD_FORM;

        private String stringName;
        private int key;
        private byte partOfSpeech;
        private byte[] morfCharacteristics;
        private boolean isExistInBd;

        protected FormForConversion(String stringName, boolean isInitialForm) {
            this.stringName = stringName.toLowerCase();
            key = createKey(isInitialForm);
        }

        public FormForConversion(String stringName, int keyBd, byte partOfSpeech, long morfCharacteristics) {
            this.stringName = stringName;
            this.key = keyBd;
            this.partOfSpeech = partOfSpeech;
            this.morfCharacteristics = getBytes(morfCharacteristics);
            isExistInBd = true;
        }

        protected String getStringName() {
            return stringName;
        }

        protected void setCharacteristics(String[] characteristics) {
            List<String> parameters = new ArrayList<>(Arrays.asList(characteristics));
            setPartOfSpeech(conversionPartOfSpeech(parameters));
            setMorfCharacteristics(getBytes(conversionMorfCharacteristics(parameters)));
        }

        private void setPartOfSpeech(byte partOfSpeech) {
            this.partOfSpeech = partOfSpeech;
        }

        private byte getPartOfSpeech() {
            return partOfSpeech;
        }

        private static byte conversionPartOfSpeech(List<String> parameters) {
            byte partOfSpeech;
            int indexEnd = parameters.size() - 1;
            try {
                partOfSpeech = getTypeOfSpeech(parameters.get(indexEnd));
                parameters.remove(indexEnd);
                if(indexEnd != 0) {
                    parameters.remove(0);
                }
                return partOfSpeech;
            } catch (Exception ex) {
                try {
                    partOfSpeech = getTypeOfSpeech(parameters.get(0));
                    parameters.remove(0);
                    if(partOfSpeech == MorfologyParameters.TypeOfSpeech.NUMERAL && parameters.size() > 0 && parameters.get(0).equals("coll")) {
                        partOfSpeech = getTypeOfSpeech(parameters.get(0));
                        parameters.remove(0);
                    }
                    if(partOfSpeech == MorfologyParameters.TypeOfSpeech.ADJECTIVEFULL || partOfSpeech == MorfologyParameters.TypeOfSpeech.NOUNPRONOUN) {
                        if(parameters.size() > 3 && parameters.get(3).equals("anph")) {
                            partOfSpeech = getTypeOfSpeech(parameters.get(3));
                            parameters.remove(3);
                        }
                        if(parameters.size() > 2 && parameters.get(2).equals("anph")) {
                            partOfSpeech = getTypeOfSpeech(parameters.get(2));
                            parameters.remove(2);
                        }
                        if(parameters.size() > 1 && parameters.get(1).equals("anph")) {
                            partOfSpeech = getTypeOfSpeech(parameters.get(1));
                            parameters.remove(1);
                        }
                        if(parameters.size() > 0 && parameters.get(0).equals("anph")) {
                            partOfSpeech = getTypeOfSpeech(parameters.get(0));
                            parameters.remove(0);
                        }
                    }
                    return partOfSpeech;
                } catch (Exception exc) {
                    String messages = String.format("Часть речи не найдена: %s и $s", parameters.get(0), parameters.get(parameters.size() - 1));
                    Logger.getLogger(ConversionDictionary.class.getName()).log(Level.SEVERE, messages, exc);
                    return 0;
                }
            }
        }

        private static long conversionMorfCharacteristics(List<String> parameters) {
            long numberParameters = 0;
            for(String parameter : parameters) {
                try {
                    numberParameters |= getParameter(parameter);
                } catch (Exception ex){
                    String messages = "Характеристика не найдена: " + parameter;
                    Logger.getLogger(ConversionDictionary.class.getName()).log(Level.SEVERE, messages, ex);
                }
            }
            return numberParameters;
        }

        private void setMorfCharacteristics(byte[] morfCharacteristics) {
            this.morfCharacteristics = morfCharacteristics;
        }

        private byte[] getMorfCharacteristics() {
            return morfCharacteristics;
        }

        public int getKey() {
            return key;
        }

        protected boolean isExistInBd() {
            return isExistInBd;
        }

        private int createKey(boolean isInitialForm) {
            if(isInitialForm) {
                return createKey(STRING_INTEGER_INITIAL_FORM_MAP, START_ID_INITIAL_FORM);
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

        protected boolean isInitialForm() {
            return getKey() < START_ID_WORD_FORM;
        }

        protected byte[] getByteFileFormat() {
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
