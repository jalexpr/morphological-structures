package ru.textanalysis.tfwwt.morphological.structures.internal.ref;

import ru.textanalysis.tfwwt.morphological.structures.internal.form.Form;
import ru.textanalysis.tfwwt.morphological.structures.internal.form.GetCharacteristics;

import java.util.Objects;

public class RefOmoForm implements GetCharacteristics {
    private final Form form;

    public RefOmoForm(Form form) {
        this.form = form;
    }

    @Override
    public byte getTypeOfSpeech() {
        return form.getTypeOfSpeech();
    }

    @Override
    public String getInitialFormString() {
        return form.getInitialFormString();
    }

    @Override
    public int getInitialFormKey() {
        return form.getInitialFormKey();
    }

    @Override
    public boolean isInitialForm() {
        return form.isInitialForm();
    }

    @Override
    public GetCharacteristics getInitialForm() {
        if (form.getInitialForm() instanceof Form) {
            return new RefOmoForm((Form) form.getInitialForm());
        } else {
//            log.warn("Wrong conversion!");todo
            return form.getInitialForm();
        }
    }

    protected Form getForm() {
        return form;
    }

    @Override
    public int hashCode() {
        return form.getMyFormKey();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        RefOmoForm that = (RefOmoForm) o;
        return Objects.equals(form.getMyFormKey(), that.form.getMyFormKey());
    }
}
