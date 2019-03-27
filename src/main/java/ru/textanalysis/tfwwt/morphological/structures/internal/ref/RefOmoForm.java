package ru.textanalysis.tfwwt.morphological.structures.internal.ref;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ru.textanalysis.tfwwt.morphological.structures.internal.form.Form;
import ru.textanalysis.tfwwt.morphological.structures.internal.form.GetCharacteristics;

public class RefOmoForm implements GetCharacteristics {
    private final Logger log = LoggerFactory.getLogger(getClass());

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
            log.warn("Wrong conversion!");
            return form.getInitialForm();
        }
    }

    protected Form getForm() {
        return form;
    }
}
