package ru.textanalysis.tawt.ms.model.jmorfsdk.command;

import lombok.Builder;

@Builder
public class PredictedFormCreateCommand {

	String derivativeFormString;
	Long derivativeFormMorphCharacteristics;
	String initialFormString;
	Byte typeOfSpeech;
	Long initialFormMorphCharacteristics;

	public String getDerivativeFormString() {
		return derivativeFormString;
	}

	public Long getDerivativeFormMorphCharacteristics() {
		return derivativeFormMorphCharacteristics;
	}

	public String getInitialFormString() {
		return initialFormString;
	}

	public Byte getTypeOfSpeech() {
		return typeOfSpeech;
	}

	public Long getInitialFormMorphCharacteristics() {
		return initialFormMorphCharacteristics;
	}
}
