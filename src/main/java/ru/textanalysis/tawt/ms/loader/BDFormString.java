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
package ru.textanalysis.tawt.ms.loader;

import ru.textanalysis.tawt.ms.model.Property;
import template.wrapper.classes.BDSqlite;

import java.io.File;
import java.util.logging.Level;
import java.util.logging.Logger;

import static template.wrapper.classes.FileHelper.deleteFile;
import static template.wrapper.classes.Lzma2FileHelper.*;

public class BDFormString {

	public final static String FOLDER = Property.FOLDER;
	public final static String NAME_BD_INITIAL_FORM = FOLDER + Property.NAME_BD_INITIAL_FORM;
	public final static String NAME_BD_WORD_FORM = FOLDER + Property.NAME_BD_WORD_FORM;
	public final static BDSqlite BD_INITIAL_FORM_STRING;
	public final static BDSqlite BD_WORD_FORM_STRING;
	public final static int START_ID_INITIAL_FORM = Property.START_ID_INITIAL_FORM;
	public final static int START_ID_DERIVATIVE_FORM = Property.START_ID_DERIVATIVE_FORM;

	static {
		deCompressDd();
		BD_INITIAL_FORM_STRING = new BDSqlite(new File(System.getProperty("java.io.tmpdir"), NAME_BD_INITIAL_FORM).getAbsolutePath());
		BD_WORD_FORM_STRING = new BDSqlite(new File(System.getProperty("java.io.tmpdir"), NAME_BD_WORD_FORM).getAbsolutePath());
	}

	public static String getLiteralById(int id) {
		if (id < START_ID_DERIVATIVE_FORM) {
			return getLiteralById(id, true);
		} else {
			return getLiteralById(id, false);
		}
	}

	private static String getLiteralById(int idKey, boolean isInitialForm) {
		String str = null;
		try {
			String executeString = String.format("SELECT * FROM 'Form' where id = %d", idKey);
			if (isInitialForm) {
				str = BD_INITIAL_FORM_STRING.executeQuery(executeString, "StringForm");
			} else {
				str = BD_WORD_FORM_STRING.executeQuery(executeString, "StringForm");
			}
			if (str == null || str.trim().isEmpty()) {
				if (isInitialForm) {
					Logger.getLogger(BDFormString.class.getName()).log(Level.SEVERE, String.format("Ошибкуа запроса, проверте актуальность версии БД \"%s\"", Property.NAME_BD_INITIAL_FORM));
				} else {
					Logger.getLogger(BDFormString.class.getName()).log(Level.SEVERE, String.format("Ошибкуа запроса, проверте актуальность версии БД \"%s\"", Property.NAME_BD_WORD_FORM));
				}
			}
		} catch (NullPointerException ex) {
			if (isInitialForm) {
				Logger.getLogger(BDFormString.class.getName()).log(Level.SEVERE, String.format("БД \"%s\" не найдена", Property.NAME_BD_INITIAL_FORM), ex);
			} else {
				Logger.getLogger(BDFormString.class.getName()).log(Level.SEVERE, String.format("БД \"%s\" не найдена", Property.NAME_BD_WORD_FORM), ex);
			}
		}
		return str;
	}

	public static void printSumme(boolean isInitialForm) {
		try {
			String executeString = String.format("SELECT Count(*) FROM  'Form'");
			if (isInitialForm) {
				System.out.println(BD_INITIAL_FORM_STRING.executeQuery(executeString, 1));
			} else {
				System.out.println(BD_WORD_FORM_STRING.executeQuery(executeString, 1));
			}
		} catch (NullPointerException ex) {
			if (isInitialForm) {
				Logger.getLogger(BDFormString.class.getName()).log(Level.SEVERE, String.format("БД \"%s\" не найдена", Property.NAME_BD_INITIAL_FORM), ex);
			} else {
				Logger.getLogger(BDFormString.class.getName()).log(Level.SEVERE, String.format("БД \"%s\" не найдена", Property.NAME_BD_WORD_FORM), ex);
			}
		}
	}

	public static void compressionBd() {
		compressionBd(BD_INITIAL_FORM_STRING, NAME_BD_INITIAL_FORM);
		compressionBd(BD_WORD_FORM_STRING, NAME_BD_WORD_FORM);
	}

	public static void compressionBd(BDSqlite bds, String pathFile) {
		compressionFile(pathFile);
		deleteFile(pathFile);
	}

	public static void deCompressDd() {
		deCompressDd(NAME_BD_INITIAL_FORM);
		deCompressDd(NAME_BD_WORD_FORM);
	}

	public static void deCompressDd(String pathBd) {
		File file = new File(System.getProperty("java.io.tmpdir"), pathBd);
		if (!file.exists()) {
			File file1 = new File(System.getProperty("java.io.tmpdir"), FOLDER);
			file1.mkdirs();
//            file = new File(pathBd + ARCHIVE_EXPANSION);
//            if(file.exists()) {
			deCompressionFile(pathBd + ARCHIVE_EXPANSION, new File(System.getProperty("java.io.tmpdir"), pathBd));
//            }
		}
	}
}
