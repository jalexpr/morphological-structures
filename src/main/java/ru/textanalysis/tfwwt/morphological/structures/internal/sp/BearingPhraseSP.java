package ru.textanalysis.tfwwt.morphological.structures.internal.sp;

import ru.textanalysis.tfwwt.morphological.structures.grammeme.BearingForm;
import ru.textanalysis.tfwwt.morphological.structures.internal.IApplyConsumer;
import ru.textanalysis.tfwwt.morphological.structures.internal.IApplyFunction;
import ru.textanalysis.tfwwt.morphological.structures.storage.ref.RefWordList;

import java.util.LinkedList;
import java.util.List;
import java.util.function.Consumer;
import java.util.function.Function;

public class BearingPhraseSP implements IApplyFunction<List<WordSP>>, IApplyConsumer<List<WordSP>> {
    private final List<OmoFormSP> mainOmoForms;
    private final List<WordSP> words;

    public BearingPhraseSP(RefWordList refWordList) {
        this.words = new LinkedList<>();
        refWordList.forEach(refOmoFormList -> words.add(new WordSP(refOmoFormList)));
        this.mainOmoForms = new LinkedList<>();
    }

    public void searchMainOmoForm() {
        words.forEach(word -> mainOmoForms.addAll(word.getByFilter(omoForm -> !omoForm.haveMain() && BearingForm.contains(omoForm.getToS()))));
    }

    @Override
    public void applyFunction(Function<List<WordSP>, Boolean> function) {
        function.apply(words);
    }

    @Override
    public void applyConsumer(Consumer<List<WordSP>> consumer) {
        consumer.accept(words);
    }

    @Override
    public String toString() {
        return "BearingPhraseSP{" +
                "words=" + words +
                ", mainOmoForm=" + mainOmoForms +
                '}';
    }
}
