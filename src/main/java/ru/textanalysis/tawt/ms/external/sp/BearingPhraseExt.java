package ru.textanalysis.tawt.ms.external.sp;

import java.util.List;

/**
 *
 */
public class BearingPhraseExt {
    private final List<OmoFormExt> mainOmoForms;

    public BearingPhraseExt(List<OmoFormExt> mainOmoForms) {
        this.mainOmoForms = mainOmoForms;
    }

    public List<OmoFormExt> getMainOmoForms() {
        return mainOmoForms;
    }
}
