package ru.textanalysis.tfwwt.morphological.structures.internal.sp;

import ru.textanalysis.tfwwt.morphological.structures.internal.ref.RefOmoForm;

import java.util.LinkedList;
import java.util.List;

public class OmoFormSP {
    private final RefOmoForm currencyOmoForm;
    private List<CursorToFormInWord> mainCursors = new LinkedList<>();
    private List<CursorToFormInWord> dependentCursors = new LinkedList<>();

    public OmoFormSP(RefOmoForm currencyOmoForm) {
        this.currencyOmoForm = currencyOmoForm;
    }

    public RefOmoForm getCurrencyOmoForm() {
        return currencyOmoForm;
    }

    public void addMainCursors(CursorToFormInWord mainCursor) {
        this.mainCursors.add(mainCursor);
    }

    public void addDependentCursors(CursorToFormInWord dependentCursor) {
        this.dependentCursors.add(dependentCursor);
    }

    public byte getToS() {
        return currencyOmoForm.getTypeOfSpeech();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        OmoFormSP omoFormSP = (OmoFormSP) o;
        return currencyOmoForm.equals(omoFormSP.currencyOmoForm);
    }

    @Override
    public int hashCode() {
        return currencyOmoForm.hashCode();
    }
}
