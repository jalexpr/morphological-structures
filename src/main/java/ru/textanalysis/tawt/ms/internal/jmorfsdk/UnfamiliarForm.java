package ru.textanalysis.tawt.ms.internal.jmorfsdk;

import ru.textanalysis.tawt.ms.grammeme.MorfologyParameters;
import ru.textanalysis.tawt.ms.internal.TypeForms;
import ru.textanalysis.tawt.ms.internal.form.Form;
import ru.textanalysis.tawt.ms.internal.form.GetCharacteristics;

public final class UnfamiliarForm extends Form {
    private final String str;

    public UnfamiliarForm(String str) {
        super(0, 0);
        this.str = str;
    }

    @Override
    public byte getTypeOfSpeech() {
        return MorfologyParameters.TypeOfSpeech.UNFAMILIAR;
    }

    @Override
    public String getInitialFormString() {
        return str;
    }

    @Override
    public int getInitialFormKey() {
        return getMyFormKey();
    }

    @Override
    public boolean isInitialForm() {
        return false;
    }

    @Override
    public GetCharacteristics getInitialForm() {
        return this;
    }

    @Override
    public String getMyString() {
        return str;
    }

    @Override
    public int hashCode() {
        return str.hashCode();
    }

    @Override
    public TypeForms getTypeForm() {
        return TypeForms.UNFAMILIAR;
    }
}
