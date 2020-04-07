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
package ru.textanalysis.tawt.ms.internal;

import ru.textanalysis.tawt.ms.grammeme.MorfologyParametersHelper;
import ru.textanalysis.tawt.ms.internal.form.Form;
import ru.textanalysis.tawt.ms.loader.BDFormString;

import java.util.LinkedList;

public class OmoForm implements IOmoForm {
    protected final int initialFormKey;
    protected final int myFormKey;
    protected final byte typeOfSpeech;
    protected final long morfCharacteristics;
    protected final LinkedList<OmoForm> myDependent = new LinkedList<>();
    protected final LinkedList<OmoForm> myMain = new LinkedList<>();

    public OmoForm() {
        this.initialFormKey = -1;
        this.typeOfSpeech = -1;
        this.morfCharacteristics = -1;
        this.myFormKey = -1;
    }

    public OmoForm(Form form) {
        this.initialFormKey = form.getInitialFormKey();
        this.typeOfSpeech = form.getTypeOfSpeech();
        this.morfCharacteristics = form.getMorphCharacteristics();
        this.myFormKey = form.getMyFormKey();
    }

    public OmoForm(int initialFormKey, int myFormKey, byte typeOfSpeech, long morfCharacteristics) {
        this.initialFormKey = initialFormKey;
        this.typeOfSpeech = typeOfSpeech;
        this.morfCharacteristics = morfCharacteristics;
        this.myFormKey = myFormKey;
    }

    /**
     * Получить String в начальной форме
     * @return возращает String в начальной форме
     */
    @Override
    public String getInitialFormString() {
        return BDFormString.getStringById(initialFormKey, true);
    }

    @Override
    public String getMyFormString() {
        return BDFormString.getStringById(myFormKey, false);
    }

    /**
     * Получить часть речи
     * @return возращает часть речи закодированную в byte
     */
    @Override
    public byte getTypeOfSpeech() {
        return typeOfSpeech;
    }

    /**
     * Получить все морф. характеристики, кроме части речи
     * @return возвращает все морф. характеристики слова, кроме части речи закадированные в long
     */
    @Override
    public long getAllMorfCharacteristics() {
        return morfCharacteristics;
    }

    @Override
    public long getTheMorfCharacteristics(Long...identifiers) {
        long mask = 0;
        for(long identifier : identifiers) {
            mask |= identifier;
        }
        return morfCharacteristics & mask;
    }

    @Override
    public long getTheMorfCharacteristics(Class...clazzes) {
        long mask = 0;
        for(Class clazz : clazzes) {
            mask |= MorfologyParametersHelper.identifierParametersByClass(clazz);
        }
        return getAllMorfCharacteristicsByMask(mask);
    }

    private long getAllMorfCharacteristicsByMask(Long mask) {
        return morfCharacteristics & mask;
    }

    @Override
    public String toString() {
        return String.format("initialFormString = %s, typeOfSpeech = %d, morfCharacteristics = %d", getInitialFormString(), typeOfSpeech, morfCharacteristics);
    }

    @Override
    public boolean haveMainForm() {
        return !myMain.isEmpty();
    }

    @Override
    public boolean haveDependentForm() {
        return !myDependent.isEmpty();
    }

    @Override
    public boolean haveCommunication() {
        return haveMainForm() || haveDependentForm();
    }

    @Override
    public void addDependentForm(OmoForm mainForm) {
        myMain.add(mainForm);
        mainForm.addMainForm(this);
    }

    private void addMainForm(OmoForm dependentForm) {
        myDependent.add(dependentForm);
    }

    @Override
    public int getMyFormKey() {
        return myFormKey;
    }

    @Override
    public int getInitialFormKey() {
        return initialFormKey;
    }

    @Override
    public boolean isInitialForm() {
        return initialFormKey == myFormKey;
    }

    @Override
    public boolean isContainsTypeOfSpeech(byte typeOfSpeech) {
        return getTypeOfSpeech() == typeOfSpeech;
    }

    @Override
    public boolean isContainsMorphCharacteristic(Class clazz, long morphCharacteristic) {
        return getTheMorfCharacteristics(clazz) == morphCharacteristic;
    }

    @Override
    public boolean isNumber() {
        return false;
    }
}
