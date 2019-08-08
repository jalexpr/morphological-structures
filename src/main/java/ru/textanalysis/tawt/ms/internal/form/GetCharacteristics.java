package ru.textanalysis.tawt.ms.internal.form;

public interface GetCharacteristics {
    byte getTypeOfSpeech();

    String getInitialFormString();

    int getInitialFormKey();

    boolean isInitialForm();

    GetCharacteristics getInitialForm();
}
