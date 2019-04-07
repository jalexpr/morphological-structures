package ru.textanalysis.tfwwt.morphological.structures.internal.sp;

import ru.textanalysis.tfwwt.morphological.structures.internal.IApplyFunction;
import ru.textanalysis.tfwwt.morphological.structures.storage.ref.RefBearingPhraseList;

import java.util.LinkedList;
import java.util.List;
import java.util.function.Function;

public class SentenceSP implements IApplyFunction<BearingPhraseSP> {
    private final List<BearingPhraseSP> bearingPhrases;

    public SentenceSP(RefBearingPhraseList refBearingPhraseList) {
        this.bearingPhrases = new LinkedList<>();
        refBearingPhraseList.forEach(refWordList -> bearingPhrases.add(new BearingPhraseSP(refWordList)));
    }

    //todo log
    @Override
    public void applyFunction(Function<BearingPhraseSP, Boolean> function) {
        bearingPhrases.forEach(function::apply);
    }
}
