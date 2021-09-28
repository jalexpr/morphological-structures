package ru.textanalysis.tawt.ms.interfaces.sp;

import ru.textanalysis.tawt.ms.interfaces.InitializationModule;
import ru.textanalysis.tawt.ms.model.sp.BearingPhrase;

import java.util.List;

public interface ISyntaxParser extends InitializationModule {

	List<BearingPhrase> getTreeSentence(String text);
}
