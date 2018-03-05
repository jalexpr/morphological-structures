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
package morphological.structures.conversion.dictionary;

import morphological.structures.load.BDFormString;
import morphological.structures.conversion.dictionary.ConversionDictionary.FormForConversion;
import template.wrapper.classes.BDSqlite;

public class BDSqliteForConversion {

    private final BDSqlite BD_INITIAL_FORM_STRING = BDFormString.BD_INITIAL_FORM_STRING;
    private final BDSqlite BD_WORD_FORM_STRING = BDFormString.BD_WORD_FORM_STRING;

    public BDSqliteForConversion() {
        createDd(BD_INITIAL_FORM_STRING);
        createDd(BD_WORD_FORM_STRING);
    }

    private void createDd(BDSqlite bds) {
        createTables(bds);
        bds.execute("BEGIN TRANSACTION");
    }

    private void createTables(BDSqlite bds) {
        bds.execute("DROP TABLE Form;");
        bds.execute("CREATE TABLE if not exists 'Form' ('id' INTEGER NOT NULL, 'StringForm' TEXT NOT NULL, PRIMARY KEY('id'));");
        bds.execute("DROP TABLE Property;");
        bds.execute("CREATE TABLE if not exists 'Property' ('id' INTEGER NOT NULL, 'Attribute' TEXT NOT NULL, 'Value' TEXT NOT NULL, PRIMARY KEY('id'));");
    }

    public void saveInBD(FormForConversion form) {
        if(form.isInitialForm()) {
            saveInBD(BD_INITIAL_FORM_STRING, form);
        } else {
            saveInBD(BD_WORD_FORM_STRING, form);
        }
    }

    private void saveInBD(BDSqlite outDBWordFormString, FormForConversion form) {
        outDBWordFormString.execute(String.format("INSERT INTO 'Form' ('id','StringForm') VALUES (%d, '%s');", form.getKey(), form.getStringName()));
    }

    public void closeBDs() {
        closeBD(BD_INITIAL_FORM_STRING);
        closeBD(BD_WORD_FORM_STRING);
    }

    private void closeBD(BDSqlite bDSqlite) {
        bDSqlite.execute("END TRANSACTION");
        bDSqlite.closeDB();
    }

}
