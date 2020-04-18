package ru.textanalysis.tawt.ms.internal.form;

import ru.textanalysis.tawt.ms.internal.TypeForms;

public interface GetCharacteristics {
    byte getTypeOfSpeech();

    String getInitialFormString();

    int getInitialFormKey();

    boolean isInitialForm();

    GetCharacteristics getInitialForm();

     TypeForms isTypeForm();
}
