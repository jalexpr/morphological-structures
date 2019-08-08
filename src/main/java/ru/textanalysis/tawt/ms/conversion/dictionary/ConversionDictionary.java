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

import ru.textanalysis.tawt.ms.storage.OmoFormList;
import template.wrapper.classes.FileHelper;

import java.io.BufferedReader;
import java.io.FileOutputStream;
import java.io.OutputStream;
import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

import static ru.textanalysis.tawt.ms.loader.BDFormString.compressionBd;
import static template.wrapper.classes.FileHelper.*;

public class ConversionDictionary {
    private final byte[] CONTROL_VALUE = ByteBuffer.allocate(4).putInt(PropertyForConversion.CONTROL_VALUE).array();
    private BufferedReader readerSourceDictionary;
    private FileOutputStream streamKeyAndHashAndMorfCharacteristics;
    private BDSqliteForConversion bds;

    private ConversionDictionary() {}

    //todo
    public void conversionDictionary(String sourceDictionaryPath, String encoding) {
        initFiles(sourceDictionaryPath, encoding);
        conversionAndSaveDictionary();
        closeFiles();
        compressionBd();
        compressionFile();
    }

    private void initFiles(String sourceDictionaryPath, String encoding) {
        readerSourceDictionary = openBufferedReaderStream(sourceDictionaryPath, encoding);
        streamKeyAndHashAndMorfCharacteristics = openFileOutputStream(PropertyForConversion.PATH_KEY_HASH_AND_MORF_CHARACTERISTICS);
        bds = new BDSqliteForConversion();
    }

    private void conversionAndSaveDictionary() {
        conversionAndSaveLemmas(readerSourceDictionary);
//        TODO:проверить слово на йо, если да, то повторить операцию выше, но ключ берется тот же для слова, а не создается новый.
    }

    private void conversionAndSaveLemmas(BufferedReader readerSourceDictionary) {
        List<FormForConversion> lemma;
        while(ready(readerSourceDictionary)) {
            lemma = conversionLemma(readerSourceDictionary);
            saveLemma(lemma);
        }
    }

    private List<FormForConversion> conversionLemma(BufferedReader readerSourceDictionary) {
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

    private FormForConversion createForm(String line, boolean isInitialForm) {
        String[] parameters = line.toLowerCase().split("\t");
        FormForConversion form = new FormForConversion(parameters[0], isInitialForm);
        if(parameters.length > 1) {
            form.setCharacteristics(parameters[1].split("[, ]"));
        }
        return form;
    }

    private void saveLemma(List<FormForConversion> lemma) {
        saveLemmaInBd(lemma);
        saveLemmaInFile(streamKeyAndHashAndMorfCharacteristics, lemma);
    }

    private void saveLemmaInBd(List<FormForConversion> lemma) {
        lemma.forEach((form) -> {
            if(!form.isExistInBd()) {
                bds.saveInBD(form);
            }
        });
    }

    private void saveLemmaInFile(OutputStream fileOutput, List<FormForConversion> lemma) {
        lemma.forEach((form) -> {
            saveInFile(fileOutput, form);
        });
        saveInFile(fileOutput, CONTROL_VALUE);
    }

    private void saveInFile(OutputStream fileOutput, FormForConversion form) {
        saveInFile(fileOutput, form.getByteFileFormat());
    }

    private void saveInFile(OutputStream fileOutput, byte[] bytse) {
        FileHelper.write(fileOutput, bytse);
    }

    private void closeFiles() {
        FileHelper.closeFile(readerSourceDictionary);
        FileHelper.closeFile(streamKeyAndHashAndMorfCharacteristics);
        bds.closeBDs();
    }

    private void compressionFile() {
        zipCompressFile(PropertyForConversion.PATH_KEY_HASH_AND_MORF_CHARACTERISTICS, PropertyForConversion.PATH_KEY_HASH_AND_MORF_CHARACTERISTICS.split("/")[1]);
    }

    public void createSmallDictionary(String nameDictionary, String text) {

    }

    public void saveSmallDictionary(String nameDictionary, OmoFormList initialFormList) {
        List<List<FormForConversion>> lemmas = getLemmas(initialFormList);
    }

    private List<List<FormForConversion>> getLemmas(OmoFormList initialFormList) {
        List<List<FormForConversion>> lemmas = new ArrayList<>();
        initialFormList.forEach(initialForm -> {
//            lemmas.add(getLemma(initialForm));
        });
        return lemmas;
    }

    private void saveLemmasInFile(String pathFile, List<List<FormForConversion>> lemmas) {
        OutputStream fileOutput = FileHelper.openFileOutputStream(PropertyForConversion.DIR_DICTIONARY + pathFile);
        lemmas.forEach((lemma) -> {
            saveLemmaInFile(fileOutput, lemma);
        });
        FileHelper.closeFile(fileOutput);
    }
}
