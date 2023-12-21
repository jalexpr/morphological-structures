package ru.textanalysis.tawt.ms.model.jmorfsdk;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class PostfixMorfCharacteristics {

	protected final String initialFormPostfix;
	protected final byte typeOfSpeech;
	protected final long postfixFormTags;
	protected final long initialFormTags;
}
