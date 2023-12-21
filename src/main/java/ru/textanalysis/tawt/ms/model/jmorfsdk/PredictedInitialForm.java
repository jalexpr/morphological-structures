package ru.textanalysis.tawt.ms.model.jmorfsdk;

import java.util.List;

/**
 * Начальная форма слова (лемма)
 */
public class PredictedInitialForm extends InitialForm {

	private final String word;

	public PredictedInitialForm(String word, byte typeOfSpeech, long morfCharacteristics, long link) {
		super(word.hashCode(), typeOfSpeech, morfCharacteristics, link, List.of());
		this.word = word;
	}

	@Override
	public String getInitialFormString() {
		return this.word;
	}

	@Override
	public TypeForms getTypeForm() {
		return TypeForms.PREDICTED_INITIAL;
	}

	@Override
	public InitialForm getInitialForm() {
		return this;
	}

	@Override
	public String toString() {
		return "{" +
			"TF=" + getTypeForm() + "," +
			"isInit=" + isInitialForm() + "," +
			"hash=" + hashCode() + "," +
			"str='" + getInitialFormString() + "'," +
			"ToS=" + getTypeOfSpeech() + "," +
			"morf=" + morphCharacteristics +
			"}";
	}
}
