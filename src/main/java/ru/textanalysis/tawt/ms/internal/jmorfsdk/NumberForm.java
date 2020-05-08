package ru.textanalysis.tawt.ms.internal.jmorfsdk;

import ru.textanalysis.tawt.ms.grammeme.MorfologyParameters.TypeOfSpeech;
import ru.textanalysis.tawt.ms.internal.TypeForms;
import ru.textanalysis.tawt.ms.internal.form.Form;

public class NumberForm extends Form {
    private final String strNumber;

    public NumberForm(String strNumber) {
        super(0, 0);
        this.strNumber = strNumber;
    }

    @Override
    public byte getTypeOfSpeech() {
        return TypeOfSpeech.NUMERAL;
    }

    @Override
    public String getInitialFormString() {
        return strNumber;
    }

    @Override
    public int getInitialFormKey() {
        return getMyFormKey();
    }

    @Override
    public boolean isInitialForm() {
        return true;
    }

    @Override
    public Form getInitialForm() {
        return this;
    }

    @Override
    public String toString() {
        return "NumberForm{" +
                "strNumber='" + strNumber + '\'' +
                ", morphCharacteristics=" + morphCharacteristics +
                ", formKeyInBD=" + formKeyInBD +
                '}';
    }

    @Override
    public TypeForms getTypeForm() {
        return TypeForms.NUMBER;
    }
}
