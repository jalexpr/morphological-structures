package ru.textanalysis.tfwwt.morphological.structures.internal.form;

public interface GetCharacteristics {
    byte getTypeOfSpeech();

    String getInitialFormString();

    int getInitialFormKey();

    boolean isInitialForm();

    GetCharacteristics getInitialForm();
}
