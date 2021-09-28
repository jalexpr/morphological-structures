package ru.textanalysis.tawt.ms.exceptions;

import ru.textanalysis.common.exception.RuTextanalysisRuntimeException;

public class TawtRuntimeException extends RuTextanalysisRuntimeException {

	public TawtRuntimeException() {
	}

	public TawtRuntimeException(String s) {
		super(s);
	}

	public TawtRuntimeException(String s, Throwable throwable) {
		super(s, throwable);
	}

	public TawtRuntimeException(Throwable throwable) {
		super(throwable);
	}
}
