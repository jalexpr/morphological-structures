package ru.textanalysis.tawt.ms.interfaces.sp;

import ru.textanalysis.tawt.ms.interfaces.InitializationModule;
import ru.textanalysis.tawt.ms.internal.sp.BearingPhraseSP;

import java.util.List;

public interface ISyntaxParser extends InitializationModule {
    List<BearingPhraseSP> getTreeSentence(String text);
}
