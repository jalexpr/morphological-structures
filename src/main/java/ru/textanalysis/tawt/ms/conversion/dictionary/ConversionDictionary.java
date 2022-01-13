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

import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import ru.textanalysis.tawt.ms.loader.DatabaseFactory;
import ru.textanalysis.tawt.ms.loader.DatabaseLemmas;
import ru.textanalysis.tawt.ms.loader.DatabaseStrings;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Locale;
import java.util.regex.Pattern;

@Slf4j
public class ConversionDictionary {

    private final DatabaseStrings databaseStrings = DatabaseFactory.getInstanceDatabaseStrings();
    private final DatabaseLemmas databaseLemmas = DatabaseFactory.getInstanceDatabaseLemmas();

    public static void main(String[] args) {
        ConversionDictionary conversionDictionary = new ConversionDictionary();
        conversionDictionary.conversionDictionary("dict.opcorpora.txt", StandardCharsets.UTF_8);
    }

    //  todo: йо
    public void conversionDictionary(String sourceDictionaryPath, Charset encoding) {
        List<List<FormForConversion>> lemmas = convertLemmasFromInitDictionary(sourceDictionaryPath, encoding);
        databaseLemmas.recreate(lemmas);
        databaseStrings.recreate(lemmas);

        databaseStrings.compression();
        databaseLemmas.compression();
    }

    private List<List<FormForConversion>> convertLemmasFromInitDictionary(String sourceDictionaryPath, Charset encoding) {
        List<List<FormForConversion>> lemmas = new ArrayList<>();
        try (BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(new FileInputStream(sourceDictionaryPath), encoding))) {
            while (bufferedReader.ready()) {
                String initForm = bufferedReader.readLine();
                if (StringUtils.isNotBlank(initForm) && !Pattern.matches("\\d+", initForm)) { //todo
                    List<FormForConversion> lemma = new LinkedList<>();
                    FormForConversion initialForm = createForm(initForm, true);
                    lemma.add(initialForm);
                    while (bufferedReader.ready()) {
                        String derivativeForm = bufferedReader.readLine();
                        if (StringUtils.isBlank(derivativeForm)) {
                            break;
                        }
                        lemma.add(createForm(derivativeForm, false));
                    }
                    lemmas.add(lemma);
                }
            }
        } catch (IOException ex) {
            log.error("Ошибка при чтении файла. Файл: {}", sourceDictionaryPath, ex);
        }
        return lemmas;
    }

    private FormForConversion createForm(String line, boolean isInitialForm) {
        String[] parameters = line.toLowerCase(Locale.ROOT).split("\t");
        FormForConversion form = new FormForConversion(parameters[0], isInitialForm);
        if (parameters.length > 1) {
            form.setCharacteristics(parameters[1].split("[, ]"));
        } else {
            System.out.println("error"); //todo
        }
        return form;
    }
}
