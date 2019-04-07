package ru.textanalysis.tfwwt.morphological.structures.internal.sp;

public class CursorToFormInWord {
    private final WordSP wordSP;
    private final int hashCode;

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
}
