package ru.textanalysis.tawt.ms.internal.sp;

import ru.textanalysis.tawt.ms.grammeme.MorfologyParameters;
import ru.textanalysis.tawt.ms.internal.IApplyConsumer;
import ru.textanalysis.tawt.ms.internal.ref.RefOmoFormList;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Consumer;
import java.util.function.Predicate;
import java.util.stream.Collectors;

public class WordSP implements IApplyConsumer<OmoFormSP> {
    private final Map<Integer, OmoFormSP> omoForms = new HashMap<>();

    public WordSP(RefOmoFormList forms) {
        forms.copy().forEach(omoForm -> omoForms.put(omoForm.hashCode(), new OmoFormSP(omoForm)));
    }

    public List<OmoFormSP> getByFilter(Predicate<? super OmoFormSP> predicate) {
        return omoForms.values().stream().filter(predicate).collect(Collectors.toList());
    }

    public boolean haveContainsBearingForm() {
        return omoForms.values().stream().anyMatch(OmoFormSP::haveBearingForm);
    }

    @Override
    public void applyConsumer(Consumer<OmoFormSP> consumer) {
        omoForms.values().forEach(consumer);
    }

    public boolean applyPredicate(Predicate<OmoFormSP> predicate) {
        return omoForms.values().stream().allMatch(predicate);
    }

    public boolean isOneTos() {
        byte tos = omoForms.values().iterator().next().getToS();
        return omoForms.values().stream().allMatch(omoForm -> omoForm.getToS() == tos);
    }

    public boolean isMonoSemantic() {
        return omoForms.size() == 1;
    }

    public void cleanNotRelation() {
        omoForms.values()
                .stream()
                .filter(OmoFormSP::haveNotRelation)
                .map(OmoFormSP::hashCode)
                .collect(Collectors.toList())
                .forEach(omoForms::remove);
    }

    @Override
    public String toString() {
        return "\n\t\tWordSP=" + omoForms.values();
    }

    public String getOmoFormStringByKey(int hashCode) {
        return omoForms.get(hashCode).toStringCurrencyOmoForm();
    }

    public boolean haveAlreadyRelationWith(WordSP wordSP) {
        return this.omoForms.values().stream()
                .anyMatch(omoFormSP -> omoFormSP.haveRelation(wordSP));
    }

    public boolean havePretext() {
        return omoForms.values().stream().anyMatch(omoForm -> omoForm.getToS() == MorfologyParameters.TypeOfSpeech.PRETEXT
        || omoForm.getToS() == MorfologyParameters.TypeOfSpeech.PARTICLE);
    }

    public boolean haveMain() {
        return omoForms.values().stream().anyMatch(OmoFormSP::haveMain);
    }
}
