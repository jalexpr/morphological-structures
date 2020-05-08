package ru.textanalysis.tawt.ms.internal.jmorfsdk;

import ru.textanalysis.tawt.ms.internal.TypeForms;
import ru.textanalysis.tawt.ms.internal.form.Form;

public final class WordForm extends Form {

    private final InitialForm initialForm;

    public WordForm(int formKey, long morfCharacteristics, InitialForm initialForm) {
        super(morfCharacteristics, formKey);
        this.initialForm = initialForm;
        initialForm.addWordfFormInList(this);
    }

    @Override
    public String getInitialFormString() {
        return initialForm.getInitialFormString();
    }

    @Override
    public int getInitialFormKey() {
        return initialForm.getInitialFormKey();
    }

    @Override
    public byte getTypeOfSpeech() {
        return initialForm.getTypeOfSpeech();
    }

    @Override
    public boolean isInitialForm(){
        return false;
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
        final WordForm other = (WordForm) obj;
        return this.getMyFormKey() == other.getMyFormKey();
    }

    @Override
    public int hashCode() {
        return getMyFormKey();
    }

    @Override
    public Form getInitialForm() {
        return initialForm;
    }

    @Override
    public TypeForms getTypeForm() {
        return TypeForms.WORD;
    }
}
