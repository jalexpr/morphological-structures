package ru.textanalysis.tawt.ms.internal.jmorfsdk;

import ru.textanalysis.tawt.ms.internal.TypeForms;
import ru.textanalysis.tawt.ms.internal.form.Form;
import ru.textanalysis.tawt.ms.loader.BDFormString;

import java.util.ArrayList;
import java.util.List;

public class InitialForm extends Form {
    private final byte typeOfSpeech;
    private final ArrayList<Form> wordFormList = new ArrayList<>();

    public InitialForm(int formKey, byte typeOfSpeech, long morfCharacteristics) {
        super(morfCharacteristics, formKey);
        this.typeOfSpeech = typeOfSpeech;
    }

    @Override
    public String getInitialFormString() {
        return BDFormString.getStringById(getMyFormKey(), true);
    }

    @Override
    public int getInitialFormKey() {
        return getMyFormKey();
    }

    @Override
    public byte getTypeOfSpeech() {
        return typeOfSpeech;
    }

    @Override
    public boolean isInitialForm(){
        return true;
    }

    @Override
    public String getMyString() {
        return BDFormString.getStringById(getMyFormKey(), true);
    }

    @Override
    public TypeForms getTypeForm() {
        return TypeForms.INITIAL;
    }

    public void addWordfFormInList(WordForm wordform) {
        wordFormList.add(wordform);
    }

    public void trimToSize() {
        wordFormList.trimToSize();
    }

    public List<Form> getWordFormList() {
        return wordFormList;
    }

    @Override
    public int hashCode() {
        return getMyFormKey();
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
        final InitialForm other = (InitialForm) obj;
        return this.getMyFormKey() == other.getMyFormKey();
    }

    @Override
    public Form getInitialForm() {
        return this;
    }
}
