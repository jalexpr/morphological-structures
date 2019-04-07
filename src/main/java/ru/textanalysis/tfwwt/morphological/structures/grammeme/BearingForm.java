package ru.textanalysis.tfwwt.morphological.structures.grammeme;

import ru.textanalysis.tfwwt.morphological.structures.grammeme.MorfologyParameters.TypeOfSpeech;

public enum BearingForm {
    NOUN(TypeOfSpeech.NOUN);

    private final byte value;

    BearingForm(byte value) {
        this.value = value;
    }

    public boolean equals(BearingForm bearingForm) {
        return this.value == bearingForm.value;
    }

    public boolean equals(byte value) {
        return this.value == value;
    }

    public static boolean contains(byte value) {
        for (BearingForm bearingForm : BearingForm.values()) {
            if (bearingForm.equals(value)) {
                return true;
            }
        }
        return false;
    }
}
