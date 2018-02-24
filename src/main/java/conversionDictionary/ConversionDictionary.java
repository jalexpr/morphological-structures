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
import java.math.BigInteger;
import java.util.HashMap;
import java.util.logging.Level;
import java.util.logging.Logger;
import load.FileOpen;

import static morphologicalstructures.Property.START_ID_INITIAL_SAVE;

public class ConversionDictionary {

    private static final byte[] controlValue;
    private BufferedReader inReader;
    private FileOutputStream outHashCodeAndMorfCharacteristics;
    private HashMap<Integer, IdAndString> stringWordFormAndId;
    private HashMap<Integer, IdAndString> stringInitialFormAndId;
    private int idInitialSave = START_ID_INITIAL_SAVE;

    static {
        controlValue = getBytes(PropertyForConversion.CONTROL_VALUE);// new byte[]{-1, -1, -1, -1};
    }

    public ConversionDictionary() {
        init();
    }

    private void init() {
        inReader = FileOpen.openBufferedReaderStream(PropertyForConversion.PATH_NUMBER_FORMAT, PropertyForConversion.ENCODING);
        outHashCodeAndMorfCharacteristics = FileOpen.openFileInputStream(PropertyForConversion.PATH_HASH_AND_MORF_CHARACTERISTICS);
        stringWordFormAndId = generateMapIdAndString(FileOpen.openBufferedReaderStream(PropertyForConversion.PATH_WORD_FORM_STRING, PropertyForConversion.ENCODING));
        stringInitialFormAndId = new HashMap<>();
    }

    private HashMap<Integer, IdAndString> generateMapIdAndString(BufferedReader outReader) {
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

    private void saveInBD(String nameBD, boolean isInitialForm) {
        if (isInitialForm) {
            saveInBD(nameBD, stringInitialFormAndId);
        } else {
            saveInBD(nameBD, stringWordFormAndId);
        }
    }

    private void saveInBD(String nameBD, HashMap<Integer, IdAndString> stringFormAndId) {
        BDSqlite outBD = new BDSqlite(nameBD);
        outBD.execute("CREATE TABLE if not exists 'Form' ('id' INTEGER NOT NULL, 'StringForm' TEXT NOT NULL, PRIMARY KEY('id'))");
        saveStringAndIdInBD(stringFormAndId, outBD);
        outBD.closeDB();
    }

    private void saveStringAndIdInBD(HashMap<Integer, IdAndString> stringFormAndId, BDSqlite outDBWordFormString) {

        outDBWordFormString.execute("BEGIN TRANSACTION");
        for (Object obj : stringFormAndId.values()) {
            IdAndString idAndString = (IdAndString) obj;
            outDBWordFormString.execute(String.format("INSERT INTO 'Form' ('id','StringForm') VALUES (%d, '%s'); ", idAndString.myId, idAndString.myString));
        }
        outDBWordFormString.execute("END TRANSACTION");
    }

    public void conversionFile() {
        try {
            inReader.readLine();
            while (inReader.ready()) {
                String word = inReader.readLine();
                saveLemma(word);
                if(word.matches("ё")) {
                    saveLemma(word.replace("ё", "e"));
                }
            }

        } catch (IOException ex) {
            Logger.getLogger(ConversionDictionary.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    private void saveLemma(String strForms) {
        saveInitialForm(strForms);
        saveWordForms(strForms);
        saveEndLemma();
    }

    private void saveInitialForm(String strForms) {
        String strInitialForm;
        if (strForms.contains("\"")) {
            strInitialForm = strForms.substring(0, strForms.indexOf("\""));
        } else {
            strInitialForm = strForms;
        }
        String[] initialFormParameters = strInitialForm.split(" ");

        stringInitialFormAndId.put(0, new IdAndString("?????_??_???????", 0));
        try {
            idInitialSave++;
            outHashCodeAndMorfCharacteristics.write(getBytes(initialFormParameters[0].hashCode()));
            outHashCodeAndMorfCharacteristics.write(Byte.decode("0x" + initialFormParameters[1]));
            outHashCodeAndMorfCharacteristics.write(getBytes(new BigInteger(initialFormParameters[2], 16).longValue()));
            outHashCodeAndMorfCharacteristics.write(getBytes(idInitialSave));

            IdAndString stringAndID = new IdAndString(initialFormParameters[0], idInitialSave);
            stringInitialFormAndId.put(stringAndID.myId, stringAndID);
        } catch (IOException ex) {
            Logger.getLogger(ConversionDictionary.class.getName()).log(Level.SEVERE, null, ex);
        } catch (NumberFormatException ex) {
            System.err.println("");
        }
    }

    private static byte[] getBytes(int value) {
        byte[] bytes = new byte[]{
            (byte) (value >> 24),
            (byte) (value >> 16),
            (byte) (value >> 8),
            (byte) (value)
        };
        return bytes;
    }

    private static byte[] getBytes(long value) {
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

    private void saveWordForms(String strLemma) {

        String[] arrayWordForms = strLemma.split("\"");

        for (int i = 1; i < arrayWordForms.length; i++) {
            saveForm(arrayWordForms[i]);
        }
    }

    private void saveForm(String strForm) {

        String[] wordlFormParameters = strForm.split(" ");
        int hashCodeForm = 0;
        try {
            hashCodeForm = wordlFormParameters[0].hashCode();
            outHashCodeAndMorfCharacteristics.write(getBytes(hashCodeForm));
            outHashCodeAndMorfCharacteristics.write(getBytes(new BigInteger(wordlFormParameters[1], 16).longValue()));
            outHashCodeAndMorfCharacteristics.write(getBytes(stringWordFormAndId.get(hashCodeForm).myId));
        } catch (IOException ex) {
            Logger.getLogger(ConversionDictionary.class.getName()).log(Level.SEVERE, null, ex);
        } catch (Exception ex) {
            Logger.getLogger(ConversionDictionary.class.getName()).log(Level.SEVERE, strForm, ex);
            System.err.println(stringWordFormAndId.get(hashCodeForm));
        }
    }

    private void saveEndLemma() {
        try {
            outHashCodeAndMorfCharacteristics.write(controlValue);
        } catch (IOException ex) {
            Logger.getLogger(ConversionDictionary.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    public void closeFiles() {
        FileOpen.closeFile(inReader);
        FileOpen.closeFile(outHashCodeAndMorfCharacteristics);
    }

    private class IdAndString {

        public final String myString;
        public final int myId;

        public IdAndString(String string, int id) {
            myString = string;
            myId = id;
        }

        @Override
        public int hashCode() {
            return myString.hashCode(); //To change body of generated methods, choose Tools | Templates.
        }

        @Override
        public boolean equals(Object obj) {
            if (this == obj) {
                return true;
            }
            if (obj == null) {
                return false;
            }
            if (getClass() != obj.getClass()) {
                return false;
            }
            final IdAndString other = (IdAndString) obj;
            if (!this.myString.equals(other.myString)) {
                return false;
            }
            return true;
        }
    }

    public static void createdInitianAndWordFormString() {
        BufferedReader inDictionary = FileOpen.openBufferedReaderStream("dictionary.format.number.txt", PropertyForConversion.ENCODING);
        BufferedWriter outInitianString = FileOpen.openBufferedWriterStream(PropertyForConversion.PATH_INITIAL_FORM_STRING, PropertyForConversion.ENCODING);
        BufferedWriter outWordString = FileOpen.openBufferedWriterStream(PropertyForConversion.PATH_WORD_FORM_STRING, PropertyForConversion.ENCODING);

        try {
            inDictionary.readLine();
            while (inDictionary.ready()) {
                String[] str = inDictionary.readLine().split("\"");

                String[] lemms = str[0].split(" ");
                outInitianString.append(lemms[0]);
                outInitianString.newLine();

                for (int i = 1; i < str.length; i++) {
                    outWordString.append(str[i].split(" ")[0]);
                    outWordString.newLine();
                }
            }
        } catch (IOException ex) {
            Logger.getLogger(ConversionDictionary.class.getName()).log(Level.SEVERE, null, ex);
        } finally {
            try {
                outInitianString.close();
                outWordString.close();
            } catch (IOException ex) {
                Logger.getLogger(ConversionDictionary.class.getName()).log(Level.SEVERE, null, ex);
            }
        }

    }

    public static void main(String[] args) {

        ConversionDictionary.createdInitianAndWordFormString();
        ConversionDictionary converFile = new ConversionDictionary();
        converFile.saveInBD(PropertyForConversion.PATH_BD_WORD_FORM, false);
        converFile.conversionFile();
        converFile.saveInBD(PropertyForConversion.PATH_BD_INITIAL_FORM, true);
        converFile.closeFiles();
    }
}
