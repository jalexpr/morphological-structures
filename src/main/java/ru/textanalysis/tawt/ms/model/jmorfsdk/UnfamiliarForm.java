package ru.textanalysis.tawt.ms.model.jmorfsdk;

import ru.textanalysis.tawt.ms.grammeme.MorfologyParameters;

import java.util.List;

/**
 * Неизвестное слово
 */
public final class UnfamiliarForm extends InitialForm {

	private final String literal;

	public UnfamiliarForm(String literal) {
		super(literal.hashCode(), MorfologyParameters.TypeOfSpeech.UNFAMILIAR, 0, List.of());
		this.literal = literal;
	}

	@Override
	public String getMyString() {
		return literal;
	}

	@Override
	public TypeForms getTypeForm() {
		return TypeForms.UNFAMILIAR;
	}
}
