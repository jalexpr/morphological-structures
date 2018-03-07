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

import template.wrapper.classes.FileHelper;

import java.io.BufferedReader;
import java.io.FileOutputStream;
import java.io.OutputStream;
import java.util.LinkedList;
import java.util.List;

import static morphological.structures.conversion.dictionary.PropertyForConversion.DIR_DICTIONARY;
import static morphological.structures.conversion.dictionary.PropertyForConversion.PATH_KEY_HASH_AND_MORF_CHARACTERISTICS;
import static morphological.structures.load.BDFormString.compressionBd;
import static template.wrapper.classes.FileHelper.zipCompressFile;
import static template.wrapper.conversion.Bytes.getPrimitiveBytes;

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

}
