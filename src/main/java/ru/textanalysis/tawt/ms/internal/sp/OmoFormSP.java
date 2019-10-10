package ru.textanalysis.tawt.ms.internal.sp;

import ru.textanalysis.tawt.ms.grammeme.BearingForm;
import ru.textanalysis.tawt.ms.internal.ref.RefOmoForm;

import java.util.LinkedList;
import java.util.List;

public class OmoFormSP {
    private final RefOmoForm currencyOmoForm;
    private CursorToFormInWord mainCursors;
    private List<CursorToFormInWord> dependentCursors = new LinkedList<>();

    public OmoFormSP(RefOmoForm currencyOmoForm) {
        this.currencyOmoForm = currencyOmoForm;
    }

    public RefOmoForm getCurrencyOmoForm() {
        return currencyOmoForm;
    }

    public void setMainCursors(CursorToFormInWord mainCursor) {
        this.mainCursors = mainCursor;
    }

    public void addDependentCursors(CursorToFormInWord dependentCursor) {
        this.dependentCursors.add(dependentCursor);
    }

    public byte getToS() {
        return currencyOmoForm.getTypeOfSpeech();
    }

    public boolean haveMain() {
        return mainCursors != null;
    }

    public boolean haveDep() {
        return !dependentCursors.isEmpty();
    }

    public boolean haveRelation() {
        return haveMain() || haveDep();
    }

    public boolean haveNotRelation() {
        return !haveRelation();
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

    @Override
    public String toString() {
        return "currencyForm=" + currencyOmoForm +
                ", main=" + mainCursors +
                ", dependents=" + dependentCursors;
    }

    public String toStringCurrencyOmoForm() {
        return currencyOmoForm.toString();
    }

    public boolean haveRelation(WordSP wordSP) {
        return mainCursors != null && mainCursors.getHashCode() == wordSP.hashCode()
                || dependentCursors.stream().anyMatch(cursorToFormInWord -> cursorToFormInWord.getHashCode() == wordSP.hashCode());
    }

    public long getMorf(long mask) {
        return getCurrencyOmoForm().getMorfCharacteristic(mask);
    }

    public boolean haveBearingForm() {
        return BearingForm.contains(this.getToS());
    }
}
