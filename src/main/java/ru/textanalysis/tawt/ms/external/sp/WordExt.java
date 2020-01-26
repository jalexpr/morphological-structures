package ru.textanalysis.tawt.ms.external.sp;

import ru.textanalysis.tawt.ms.grammeme.MorfologyParameters;
import ru.textanalysis.tawt.ms.internal.IApplyConsumer;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;
import java.util.function.Predicate;
import java.util.stream.Collectors;

public class WordExt implements IApplyConsumer<OmoFormExt> {
    private final List<OmoFormExt> omoForms;

    public WordExt(OmoFormExt toExt) {
        this.omoForms = new ArrayList<>();
        this.omoForms.add(toExt);
    }

    public WordExt(List<OmoFormExt> omoForms) {
        this.omoForms = omoForms;
    }

    public List<OmoFormExt> getByFilter(Predicate<? super OmoFormExt> predicate) {
        return omoForms.stream().filter(predicate).collect(Collectors.toList());
    }

    public boolean haveContainsBearingForm() {
        return omoForms.stream().anyMatch(OmoFormExt::haveBearingForm);
    }

    @Override
    public void applyConsumer(Consumer<OmoFormExt> consumer) {
        omoForms.forEach(consumer);
    }

    public boolean applyPredicate(Predicate<OmoFormExt> predicate) {
        return omoForms.stream().allMatch(predicate);
    }

    public boolean isOneTos() {
        byte tos = omoForms.iterator().next().getToS();
        return omoForms.stream().allMatch(omoForm -> omoForm.getToS() == tos);
    }

    public boolean isMonoSemantic() {
        return omoForms.size() == 1;
    }

    public void cleanNotRelation() {
        omoForms.stream()
                .filter(OmoFormExt::haveNotRelation)
                .map(OmoFormExt::hashCode)
                .collect(Collectors.toList())
                .forEach(omoForms::remove);
    }

    @Override
    public String toString() {
        return "\n\t\tWordSP=" + omoForms;
    }

    public String getOmoFormStringByKey(int hashCode) {
        return omoForms.get(hashCode).toStringCurrencyOmoForm();
    }

    public boolean havePretext() {
        return omoForms.stream().anyMatch(omoForm -> omoForm.getToS() == MorfologyParameters.TypeOfSpeech.PRETEXT
                || omoForm.getToS() == MorfologyParameters.TypeOfSpeech.PARTICLE);
    }

    public boolean haveMain() {
        return omoForms.stream().anyMatch(OmoFormExt::haveMain);
    }

    public OmoFormExt getFirst() {
        return omoForms.get(0);
    }
}
