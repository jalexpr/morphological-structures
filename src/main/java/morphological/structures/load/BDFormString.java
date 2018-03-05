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
package morphological.structures.load;

import java.io.File;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.logging.Level;
import java.util.logging.Logger;

import morphological.structures.internal.Property;
import template.wrapper.classes.BDSqlite;

import static template.wrapper.classes.FileHelper.deleteFile;
import static template.wrapper.classes.Lzma2FileHelper.ARCHIVE_EXPANSION;
import static template.wrapper.classes.Lzma2FileHelper.compressionFile;
import static template.wrapper.classes.Lzma2FileHelper.deCompressionFile;

public class BDFormString {

    public final static String PATH_BD_INITIAL_FORM = Property.PATH_BD_INITIAL_FORM;
    public final static String PATH_BD_WORD_FORM = Property.PATH_BD_WORD_FORM;
    public final static BDSqlite BD_INITIAL_FORM_STRING;
    public final static BDSqlite BD_WORD_FORM_STRING;
    public final static int START_ID_INITIAL_FORM = Property.START_ID_INITIAL_FORM;
    public final static int START_ID_WORD_FORM = Property.START_ID_WORD_FORM;

    static {
        deCompressDd();
        BD_INITIAL_FORM_STRING = new BDSqlite(PATH_BD_INITIAL_FORM);
        BD_WORD_FORM_STRING = new BDSqlite(PATH_BD_WORD_FORM);
    }

    public static String getStringById(int idKey) {
        if(idKey < START_ID_WORD_FORM) {
            return getStringById(idKey, true);
        } else {
            return getStringById(idKey, false);
        }
    }

    public static String getStringById(int idKey, boolean isInitialForm) {
        try {
            ResultSet resultSet;
            String executeString = String.format("SELECT * FROM 'Form' where id = %d", idKey);
            if (isInitialForm) {
                resultSet = BD_INITIAL_FORM_STRING.executeQuery(executeString);
            } else {
                resultSet = BD_WORD_FORM_STRING.executeQuery(executeString);
            }
            return (String) resultSet.getObject("StringForm");
        } catch (NullPointerException ex) {
            if (isInitialForm) {
                Logger.getLogger(BDFormString.class.getName()).log(Level.SEVERE, String.format("БД \"%s\" не найдена", Property.PATH_BD_INITIAL_FORM), ex);
            } else {
                Logger.getLogger(BDFormString.class.getName()).log(Level.SEVERE, String.format("БД \"%s\" не найдена", Property.PATH_BD_WORD_FORM), ex);
            }
        } catch (SQLException ex) {
            if (isInitialForm) {
                Logger.getLogger(BDFormString.class.getName()).log(Level.SEVERE, String.format("Ошибкуа запроса, проверте актуальность версии БД \"%s\"", Property.PATH_BD_INITIAL_FORM), ex);
            } else {
                Logger.getLogger(BDFormString.class.getName()).log(Level.SEVERE, String.format("Ошибкуа запроса, проверте актуальность версии БД \"%s\"", Property.PATH_BD_WORD_FORM), ex);
            }
        }
        return null;
    }

    public static void printSumme(boolean isInitialForm) {

        try {
            String executeString = String.format("SELECT Count(*) FROM  'Form'");
            if (isInitialForm) {
                System.out.println(BD_INITIAL_FORM_STRING.executeQuery(executeString).getObject(1));
            } else {
                System.out.println(BD_WORD_FORM_STRING.executeQuery(executeString));
            }
        } catch (NullPointerException ex) {
            if (isInitialForm) {
                Logger.getLogger(BDFormString.class.getName()).log(Level.SEVERE, String.format("БД \"%s\" не найдена", Property.PATH_BD_INITIAL_FORM), ex);
            } else {
                Logger.getLogger(BDFormString.class.getName()).log(Level.SEVERE, String.format("БД \"%s\" не найдена", Property.PATH_BD_WORD_FORM), ex);
            }
        } catch (SQLException ex) {
            if (isInitialForm) {
                Logger.getLogger(BDFormString.class.getName()).log(Level.SEVERE, String.format("Ошибкуа запроса, проверте актуальность версии БД \"%s\"", Property.PATH_BD_INITIAL_FORM), ex);
            } else {
                Logger.getLogger(BDFormString.class.getName()).log(Level.SEVERE, String.format("Ошибкуа запроса, проверте актуальность версии БД \"%s\"", Property.PATH_BD_WORD_FORM), ex);
            }
        }
    }

    public static void compressionBd() {
        compressionBd(BD_INITIAL_FORM_STRING, PATH_BD_INITIAL_FORM);
        compressionBd(BD_WORD_FORM_STRING, PATH_BD_WORD_FORM);
    }

    public static void compressionBd(BDSqlite bds, String pathFile) {
        bds.closeDB();
        compressionFile(pathFile);
        deleteFile(pathFile);
    }

    public static void deCompressDd() {
        deCompressDd(PATH_BD_INITIAL_FORM);
        deCompressDd(PATH_BD_WORD_FORM);
    }

    public static void deCompressDd(String pathBd) {
        File file = new File(pathBd);
        if(!file.exists()) {
            file = new File(pathBd + ARCHIVE_EXPANSION);
            if(file.exists()) {
                deCompressionFile(pathBd + ARCHIVE_EXPANSION, pathBd);
            }
        }
    }

    public static void main(String[] args) {

    }

}
