package ru.textanalysis.tawt.ms.interfaces.rfc;

import ru.textanalysis.tawt.ms.interfaces.InitializationModule;
import ru.textanalysis.tawt.ms.internal.sp.WordSP;

public interface IRulesForCompatibility extends InitializationModule {
    boolean establishRelation(int distance, WordSP leftWord, WordSP rightWord);

    //пробегаемся по всем словам, которые стоят правея
    boolean establishRelationForPretext(WordSP pretext, WordSP word);
}
