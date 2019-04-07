package ru.textanalysis.tfwwt.morphological.structures.internal.sp;

import ru.textanalysis.tfwwt.morphological.structures.internal.IApplyConsumer;
import ru.textanalysis.tfwwt.morphological.structures.internal.ref.RefOmoFormList;

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

    public boolean isGetBearingForm() {
        //todo
        return false;
    }

    @Override
    public void applyConsumer(Consumer<OmoFormSP> consumer) {
        omoForms.values().forEach(consumer);
    }
}
