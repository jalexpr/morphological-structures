package ru.textanalysis.tawt.ms.internal.sp;

import ru.textanalysis.tawt.ms.internal.IApplyConsumer;
import ru.textanalysis.tawt.ms.internal.IApplyFunction;
import ru.textanalysis.tawt.ms.storage.ref.RefBearingPhraseList;

import java.util.LinkedList;
import java.util.List;
import java.util.function.Consumer;
import java.util.function.Function;

public class SentenceSP implements IApplyFunction<BearingPhraseSP>, IApplyConsumer<BearingPhraseSP> {
    protected final List<BearingPhraseSP> bearingPhrases;

    public SentenceSP(RefBearingPhraseList refBearingPhraseList) {
        this.bearingPhrases = new LinkedList<>();
        refBearingPhraseList.forEach(refWordList -> bearingPhrases.add(new BearingPhraseSP(refWordList)));
    }

    //todo log
    @Override
    public void applyFunction(Function<BearingPhraseSP, Boolean> function) {
        bearingPhrases.forEach(function::apply);
    }

    //todo log
    @Override
    public void applyConsumer(Consumer<BearingPhraseSP> consumer) {
        bearingPhrases.forEach(consumer);
    }

    @Override
    public String toString() {
        return "\"sentenceSP\" = {" +
                "bearingPhrases=" + bearingPhrases +
                '}';
    }
}
