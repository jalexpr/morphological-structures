package ru.textanalysis.tawt.ms.internal.sp;

import ru.textanalysis.tawt.ms.external.sp.OmoFormExt;

public class CursorToFormInWord {
    public static final int NOT_HAVE_EXACT_RELATION = -1;

    protected final WordSP wordSP;
    protected final int hashCode;

    public CursorToFormInWord(WordSP wordSP, int hashCode) {
        this.wordSP = wordSP;
        this.hashCode = hashCode;
    }

    public WordSP getWordSP() {
        return wordSP;
    }

    public int getHashCode() {
        return hashCode;
    }

    @Override
    public String toString() {
        return wordSP.getOmoFormStringByKey(hashCode);
    }

    public OmoFormExt toExt(OmoFormExt main) {
        return wordSP.toExtByKey(hashCode, main);
    }
}
