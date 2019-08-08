package ru.textanalysis.tawt.ms.internal.sp;

public class CursorToFormInWord {
    public static final int NOT_HAVE_EXACT_RELATION = -1;

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

    @Override
    public String toString() {
        return "CursorToFormInWord{" +
                "omoForm=" + wordSP.getOmoFormStringByKey(hashCode) +
                '}';
    }
}
