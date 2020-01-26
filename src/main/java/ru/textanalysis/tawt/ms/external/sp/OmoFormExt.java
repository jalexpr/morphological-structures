package ru.textanalysis.tawt.ms.external.sp;

import ru.textanalysis.tawt.ms.grammeme.BearingForm;
import ru.textanalysis.tawt.ms.internal.ref.RefOmoForm;

import java.util.List;

public class OmoFormExt {
    private final RefOmoForm currencyOmoForm;
    private OmoFormExt mainWord;
    private List<OmoFormExt> dependentWords;

    public OmoFormExt(RefOmoForm currencyOmoForm, OmoFormExt main, List<OmoFormExt> dependents) {
        this.currencyOmoForm = currencyOmoForm;
        this.mainWord = main;
        this.dependentWords = dependents;
    }

    public RefOmoForm getCurrencyOmoForm() {
        return currencyOmoForm;
    }

    public byte getToS() {
        return currencyOmoForm.getTypeOfSpeech();
    }

    public boolean haveMain() {
        return mainWord != null;
    }

    public boolean haveDep() {
        return !dependentWords.isEmpty();
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
        OmoFormExt omoFormSP = (OmoFormExt) o;
        return currencyOmoForm.equals(omoFormSP.currencyOmoForm);
    }

    @Override
    public int hashCode() {
        return currencyOmoForm.hashCode();
    }

    @Override
    public String toString() {
        return "currencyForm=" + currencyOmoForm +
                ", main=" + (mainWord != null ? mainWord.getCurrencyOmoForm() : " null ") +
                ", dependents=" + dependentWords;
    }

    public String toStringCurrencyOmoForm() {
        return currencyOmoForm.toString();
    }

    public long getMorf(long mask) {
        return getCurrencyOmoForm().getMorfCharacteristic(mask);
    }

    public boolean haveBearingForm() {
        return BearingForm.contains(this.getToS());
    }

    public OmoFormExt getMainWord() {
        return mainWord;
    }

    public List<OmoFormExt> getDependentWords() {
        return dependentWords;
    }
}
