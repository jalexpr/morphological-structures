package ru.textanalysis.tawt.ms.internal.ref;

import ru.textanalysis.tawt.ms.internal.TypeForms;
import ru.textanalysis.tawt.ms.internal.form.Form;
import ru.textanalysis.tawt.ms.internal.form.GetCharacteristics;

import java.util.Objects;

public class RefOmoForm implements GetCharacteristics {
    protected final Form form;

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

    @Override
    public TypeForms getTypeForm() {
        return form.getTypeForm();
    }

    protected Form getForm() {
        return form;
    }

    @Override
    public int hashCode() {
        return form.getOrder();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        RefOmoForm that = (RefOmoForm) o;
        return Objects.equals(form.getMyFormKey(), that.form.getMyFormKey());
    }

    @Override
    public String toString() {
        return form.toString();
    }

    public long getMorfCharacteristic(long morfologyParameter) {
        return form.getMorphCharacteristics() & morfologyParameter;
    }

    public RefOmoForm copy() {
        return new RefOmoForm(form);
    }
}
