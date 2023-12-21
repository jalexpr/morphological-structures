package ru.textanalysis.tawt.ms.model.jmorfsdk;

import lombok.Builder;

public class PredictedDerivativeForm extends Form {

	private final String word;
	private PredictedInitialForm initialForm;

	@Builder
	public PredictedDerivativeForm(String word, long morfCharacteristics, long link, PredictedInitialForm initialForm) {
		super(word.hashCode(), morfCharacteristics, link);
		this.word = word;
		this.initialForm = initialForm;
	}

	@Override
	public String getMyString() {
		return this.word;
	}

	@Override
	public String getInitialFormString() {
		return initialForm.getInitialFormString();
	}

	@Override
	public int getInitialFormKey() {
		return initialForm.getInitialFormKey();
	}

	@Override
	public byte getTypeOfSpeech() {
		return initialForm.getTypeOfSpeech();
	}

	@Override
	public boolean isInitialForm() {
		return false;
	}

	@Override
	public InitialForm getInitialForm() {
		return initialForm;
	}

	@Override
	public TypeForms getTypeForm() {
		return TypeForms.PREDICTED_DERIVATIVE;
	}
}
