package ru.textanalysis.tawt.ms.exceptions;

import ru.textanalysis.common.exception.RuTextanalysisException;

public class TawtException extends RuTextanalysisException {
    public TawtException() {
    }

    public TawtException(String s) {
        super(s);
    }

    public TawtException(String s, Throwable throwable) {
        super(s, throwable);
    }

    public TawtException(Throwable throwable) {
        super(throwable);
    }
}
