package ru.textanalysis.tawt.ms.model.jmorfsdk.command;

import lombok.Builder;
import lombok.Getter;

@Builder
@Getter
public class PredictedFormCreateCommand {

	String derivativeFormString;
	Long derivativeFormMorphCharacteristics;
	String initialFormString;
	Byte typeOfSpeech;
	Long initialFormMorphCharacteristics;
}
