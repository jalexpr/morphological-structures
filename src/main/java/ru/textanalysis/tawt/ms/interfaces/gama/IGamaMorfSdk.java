package ru.textanalysis.tawt.ms.interfaces.gama;

import ru.textanalysis.tawt.ms.internal.ref.RefOmoFormList;

import java.util.List;

public interface IGamaMorfSdk {
    void init();
    RefOmoFormList getMorphWord(String word);

    List<String> getMorphWord(String word, long morfCharacteristics) throws Exception;
}
