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
package ru.textanalysis.tawt.ms.grammeme;

public interface MorfologyParameters {

	/**
	 * одушевленность
	 */
	interface Animacy {

		/**
		 * одушевлённое
		 */
		long ANIMATE = 0x2L;
		/**
		 * неодушевлённое
		 */
		long INANIMATE = 0x3L;

		long IDENTIFIER = 0x3L;
	}

	/**
	 * Род
	 */
	interface Gender {

		/**
		 * общий род (м/ж)
		 */
		long COMMON = 0x0L << 2;
		/**
		 * Гендер не выражен
		 */
		long UNCLEARGENDER = 0x0L << 2;
		/**
		 * мужской род
		 */
		long MANS = 0x1L << 2;
		/**
		 * женский род
		 */
		long FEMININ = 0x2L << 2;
		/**
		 * средний род
		 */
		long NEUTER = 0x3L << 2;

		long IDENTIFIER = 0x3L << 2;
	}

	/**
	 * Число
	 */
	interface Numbers {

		/**
		 * единственное число
		 */
		long SINGULAR = 0x2L << 4;
		/**
		 * множественное число
		 */
		long PLURAL = 0x3L << 4;

		long IDENTIFIER = 0x3L << 4;
	}

	/**
	 * Падеж
	 */
	interface Case {

		/**
		 * именительный падеж
		 */
		long NOMINATIVE = 0x1L << 6;
		/**
		 * родительный падеж, для обобщения - 1*10
		 */
		long GENITIVE = 0x2L << 6;
		/**
		 * первый родительный падеж
		 */
		long GENITIVE1 = 0xAL << 6;
		/**
		 * второй родительный (частичный) падеж
		 */
		long GENITIVE2 = 0xEL << 6;
		/**
		 * дательный падеж
		 */
		long DATIVE = 0x3L << 6;
		/**
		 * винительный падеж, для обобщения 1*00
		 */
		long ACCUSATIVE = 0x8L << 6;
		/**
		 * второй винительный падеж
		 */
		long ACCUSATIVE2 = 0xCL << 6;
		/**
		 * творительный падеж
		 */
		long ABLTIVE = 0x5L << 6;
		/**
		 * предложный падеж, для обобщения - 1*11
		 */
		long PREPOSITIONA = 0x7L << 6;
		/**
		 * первый предложный падеж
		 */
		long PREPOSITIONA1 = 0xBL << 6;
		/**
		 * второй предложный (местный) падеж
		 */
		long PREPOSITIONA2 = 0xFL << 6;
		/**
		 * звательный падеж
		 */
		long VOATIVE = 0x9L << 6;
		long IDENTIFIER = 0xFL << 6;
	}

	/**
	 * Вид
	 */
	interface View {

		/**
		 * совершенный вид
		 */
		long PERFECT = 0x2L << 10;
		/**
		 * несовершенный вид
		 */
		long IMPERFECT = 0x3L << 10;

		long IDENTIFIER = 0x3L << 10;
	}

	/**
	 * Переходность
	 */
	interface Transitivity {

		/**
		 * переходный
		 */
		long TRAN = 0x2L << 12;
		/**
		 * непереходный
		 */
		long INTR = 0x3L << 12;

		long IDENTIFIER = 0x3L << 12;
	}

	/**
	 * Лицо
	 */
	interface Liso {

		/**
		 * 1 лицо
		 */
		long PER1 = 0x1L << 14;
		/**
		 * 2 лицо
		 */
		long PER2 = 0x2L << 14;
		/**
		 * 3 лицо
		 */
		long PER3 = 0x3L << 14;

		long IDENTIFIER = 0x3L << 14;
	}

	/**
	 * Время
	 */
	interface Time {

		/**
		 * настоящее время
		 */
		long PRESENT = 0x1L << 16;
		/**
		 * прошедшее время
		 */
		long PAST = 0x2L << 16;
		/**
		 * будущее время
		 */
		long FUTURE = 0x3L << 16;

		long IDENTIFIER = 0x3L << 16;
	}

	/**
	 * наклонение изъявительное/повелительное
	 */
	interface Mood {

		/**
		 * изъявительное наклонение
		 */
		long INDICATIVE = 0x2L << 18;
		/**
		 * повелительное наклонение
		 */
		long IMPERATIVE = 0x3L << 18;

		long IDENTIFIER = 0x3L << 18;
	}

	/**
	 * говорящий включён / не включен в действие (идем / иди)
	 */
	interface Act {

		/**
		 * говорящий включён (идем, идемте)
		 */
		long INCLUSIVE = 0x2L << 20;
		/**
		 * говорящий не включён в действие (иди, идите)
		 */
		long EXCLUSIVE = 0x3L << 20;

		long IDENTIFIER = 0x3L << 20;
	}

	/**
	 * Залог действительный/страдательный
	 */
	interface Voice {

		/**
		 * действительный залог
		 */
		long ACTIVE = 0x2L << 22;
		/**
		 * страдательный залог
		 */
		long PASSIVE = 0x3L << 22;

		long IDENTIFIER = 0x3L << 22;
	}

	/**
	 * аббревиатура/имя/фамилия/отчество/инициалы
	 */
	interface Name {

		/**
		 * аббревиатура
		 */
		long ABBREVIATION = 0x1L << 24;
		/**
		 * имя
		 */
		long NAME = 0x4L << 24;
		/**
		 * фамилия
		 */
		long SURN = 0x5L << 24;
		/**
		 * отчество
		 */
		long PART = 0x6L << 24;
		/**
		 * Инициал
		 */
		long INIT = 0x7L << 24;

		long IDENTIFIER = 0x7L << 24;
	}

	/**
	 * типы местоимений
	 */
	interface TepePronoun {

		/**
		 * местоименное
		 */
		long APRO = 0x1L << 27;
		/**
		 * порядковое
		 */
		long ANUM = 0x2L << 27;
		/**
		 * притяжательное
		 */
		long POSS = 0x3L << 27;

		long IDENTIFIER = 0x3L << 27;
	}

	/**
	 * форма на _ье/_ие отчество через _ие_/на _ьи
	 */
	interface TerminationForm {

		/**
		 * форма на _ье
		 */
		long V_BE = 0x1L << 29;
		/**
		 * форма на _ие; отчество через _ие_
		 */
		long V_IE = 0x2L << 29;
		/**
		 * форма на _ьи
		 */
		long V_BI = 0x3L << 29;

		long IDENTIFIER = 0x3L << 29;
	}

	interface Alone {

		long SHIFTBIT = 31;
		/**
		 * singularia tantum
		 */
		long SGTM = 0x1L << SHIFTBIT << 0;
		/**
		 * pluralia tantum
		 */
		long PLTM = 0x1L << SHIFTBIT << 1;
		/**
		 * форма на _енен
		 */
		long V_EN = 0x1L << SHIFTBIT << 2;
		/**
		 * безличный
		 */
		long IMPE = 0x1L << SHIFTBIT << 3;
		/**
		 * возможно безличное употребление
		 */
		long IMPX = 0x1L << SHIFTBIT << 4;
		/**
		 * многократный
		 */
		long MULT = 0x1L << SHIFTBIT << 5;
		/**
		 * возвратный
		 */
		long REFL = 0x1L << SHIFTBIT << 6;
		/**
		 * неизменяемое
		 */
		long FIXD = 0x1L << SHIFTBIT << 7;
		/**
		 * топоним
		 */
		long GEOX = 0x1L << SHIFTBIT << 8;
		/**
		 * организация
		 */
		long ORGN = 0x1L << SHIFTBIT << 9;
		/**
		 * торговая марка
		 */
		long TRAD = 0x1L << SHIFTBIT << 10;
		/**
		 * возможна субстантивация
		 */
		long SUBX = 0x1L << SHIFTBIT << 11;
		/**
		 * превосходная степень
		 */
		long SUPR = 0x1L << SHIFTBIT << 12;
		/**
		 * качественное
		 */
		long QUAL = 0x1L << SHIFTBIT << 13;
		/**
		 * форма на _ею
		 */
		long V_EY = 0x1L << SHIFTBIT << 14;
		/**
		 * форма на _ою
		 */
		long V_OY = 0x1L << SHIFTBIT << 15;
		/**
		 * сравнительная степень на по_
		 */
		long CMP2 = 0x1L << SHIFTBIT << 16;
		/**
		 * форма компаратива на _ей
		 */
		long V_EJ = 0x1L << SHIFTBIT << 17;
		/**
		 * литературный вариант
		 */
		long LITR = 0x1L << SHIFTBIT << 18;
		/**
		 * опечатка
		 */
		long ERRO = 0x1L << SHIFTBIT << 19;
		/**
		 * искажение
		 */
		long DIST = 0x1L << SHIFTBIT << 20;
		/**
		 * вопросительное
		 */
		long QUES = 0x1L << SHIFTBIT << 21;
		/**
		 * указательное
		 */
		long DMNS = 0x1L << SHIFTBIT << 22;
		/**
		 * вводное слово
		 */
		long PRNT = 0x1L << SHIFTBIT << 23;
		/**
		 * может выступать в роли предикатива
		 */
		long PRDX = 0x1L << SHIFTBIT << 24;
		/**
		 * счётная форма
		 */
		long COUN = 0x1L << SHIFTBIT << 25;
		/**
		 * форма после предлога
		 */
		long AF_P = 0x1L << SHIFTBIT << 26;
		/**
		 * может использоваться как одуш. / неодуш.
		 */
		long INMX = 0x1L << SHIFTBIT << 27;
		/**
		 * Вариант предлога ( со, подо, ...)
		 */
		long VPRE = 0x1L << SHIFTBIT << 28;
		/**
		 * может выступать в роли прилагательного
		 */
		long ADJX = 0x1L << SHIFTBIT << 29;
		/**
		 * разговорное
		 */
		long INFR = 0x1L << SHIFTBIT << 30;
		/**
		 * жаргонное
		 */
		long SLNG = 0x1L << SHIFTBIT << 31;
		/**
		 * устаревшее
		 */
		long ARCH = 0x1L << SHIFTBIT << 32; //todo трабла

		long IDENTIFIER = 0xFFFFFFFFL << SHIFTBIT;
	}

	/**
	 * Часть речи
	 */
	interface TypeOfSpeech {

		byte NOUN = 0x11; //17

		//группа прилагательных
		byte ADJECTIVE_FULL = 0x12;
		byte ADJECTIVE_SHORT = 0x13; //13

		byte VERB = 0x14;   //14
		byte INFINITIVE = 0x15;

		//группа причастий
		byte PARTICIPLE_FULL = 0x16;
		byte PARTICIPLE_SHORT = 0x17; //23

		//группа деепричастий
		byte GERUND = 0x19;
		byte GERUND_IMPERFECT = 0x1A;
		byte GERUND_SHI = 0x1B;

		byte NUMERAL = 0x1C;
		byte COLLECTIVE_NUMERAL = 0x1D;

		byte NOUN_PRONOUN = 0x1E; //30 местоимеение
		byte ANAPHORICPRONOUN = 0x1F;

		byte ADVERB = 0x9;
		byte COMPARATIVE = 0xA;
		byte PREDICATE = 0xB;
		byte PRETEXT = 0xC;
		byte UNION = 0xD;
		byte PARTICLE = 0xE;
		byte INTERJECTION = 0xF;

		byte UNFAMILIAR = 0x1;

		byte IDENTIFIER = 0x1F;
	}
}
