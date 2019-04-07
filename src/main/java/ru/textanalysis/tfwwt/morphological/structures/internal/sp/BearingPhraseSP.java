package ru.textanalysis.tfwwt.morphological.structures.internal.sp;

import ru.textanalysis.tfwwt.morphological.structures.internal.IApplyConsumer;
import ru.textanalysis.tfwwt.morphological.structures.internal.IApplyFunction;
import ru.textanalysis.tfwwt.morphological.structures.storage.ref.RefWordList;

import java.util.LinkedList;
import java.util.List;
import java.util.function.Consumer;
import java.util.function.Function;

public class BearingPhraseSP implements IApplyFunction<List<WordSP>>, IApplyConsumer<List<WordSP>> {
    private final List<WordSP> words;

    public BearingPhraseSP(RefWordList refWordList) {
        this.words = new LinkedList<>();
        refWordList.forEach(refOmoFormList -> words.add(new WordSP(refOmoFormList)));
    }

    @Override
    public void applyFunction(Function<List<WordSP>, Boolean> function) {
        function.apply(words);
    }

    @Override
    public void applyConsumer(Consumer<List<WordSP>> consumer) {
        consumer.accept(words);
    }
}
