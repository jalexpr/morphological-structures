package ru.textanalysis.tawt.ms.model.jmorfsdk;

import ru.textanalysis.tawt.ms.grammeme.MorfologyParameters.TypeOfSpeech;

import java.util.List;

/**
 * Форма, которая хранит число
 */
public class PunctuationForm extends InitialForm {

	private final String textNumber;

	public PunctuationForm(String textNumber) {
		super(textNumber.hashCode(), TypeOfSpeech.PUNCTUATION, 0, 0, List.of());
		this.textNumber = textNumber;
	}

	@Override
	public String getMyString() {
		return textNumber;
	}

	@Override
	public TypeForms getTypeForm() {
		return TypeForms.PUNCTUATION;
	}
}
