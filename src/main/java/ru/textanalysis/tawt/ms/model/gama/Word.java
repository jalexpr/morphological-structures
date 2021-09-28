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
package ru.textanalysis.tawt.ms.model.gama;

import ru.textanalysis.tawt.ms.model.jmorfsdk.Form;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Predicate;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class Word {

	private List<Form> forms;

	public Word(List<Form> forms) {
		this.forms = forms;
	}

	public void applyFilter(Predicate<? super Form> predicate) {
		forms = forms.stream().filter(predicate).collect(Collectors.toList());
	}

	public List<Form> getByFilter(Predicate<? super Form> predicate) {
		return forms.stream().filter(predicate).collect(Collectors.toList());
	}

	public List<Form> copy() {
		return new ArrayList<>(forms);
	}

	public Stream<Form> stream() {
		return forms.stream();
	}

	public int size() {
		return forms.size();
	}

	public boolean isSingleValuedForm() {
		return size() == 1;
	}

	public List<Form> getOmoForms() {
		return forms;
	}

	@Override
	public String toString() {
		String content;
		if (forms.isEmpty()) {
			content = "empty";
		} else {
			content = forms.get(0).getInitialFormString() + ":" +
				System.lineSeparator() +
				forms.stream()
					.map(Form::toString)
					.collect(Collectors.joining(System.lineSeparator()));
		}
		return "\tWord:" + System.lineSeparator() + "\t\t - " + content;
	}
}
