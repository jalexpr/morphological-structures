package ru.textanalysis.tawt.ms.interfaces.gama;

import ru.textanalysis.tawt.ms.interfaces.InitializationModule;
import ru.textanalysis.tawt.ms.internal.ref.RefOmoFormList;
import ru.textanalysis.tawt.ms.storage.ref.RefBearingPhraseList;
import ru.textanalysis.tawt.ms.storage.ref.RefParagraphList;
import ru.textanalysis.tawt.ms.storage.ref.RefSentenceList;
import ru.textanalysis.tawt.ms.storage.ref.RefWordList;

public interface GamaAccessInterface extends InitializationModule {

    RefOmoFormList getMorphWord(String word) throws Exception;

    RefWordList getMorphBearingPhrase(String bearingPhrase);

    RefBearingPhraseList getMorphSentence(String sentence);

    RefSentenceList getMorphParagraph(String paragraph);

    RefParagraphList getMorphText(String text);

}
