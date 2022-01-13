package ru.textanalysis.tawt.ms.model.jmorfsdk;

import lombok.Builder;

import java.util.Collections;
import java.util.List;

/**
 * Начальная форма слова (лемма)
 */
public class InitialForm extends Form {

	private final byte typeOfSpeech;
	private final List<DerivativeForm> derivativeForms;

	@Builder
	protected InitialForm(int formKey, byte typeOfSpeech, long morfCharacteristics, List<DerivativeForm> derivativeForms) {
		super(formKey, morfCharacteristics);
		this.typeOfSpeech = typeOfSpeech;
		this.derivativeForms = Collections.unmodifiableList(derivativeForms);
		derivativeForms.forEach(form -> form.setInitialForm(this));
	}

	@Override
	public String getInitialFormString() {
		return getMyString();
	}

	@Override
	public int getInitialFormKey() {
		return getMyFormKey();
	}

	@Override
	public byte getTypeOfSpeech() {
		return typeOfSpeech;
	}

	@Override
	public boolean isInitialForm() {
		return true;
	}

	@Override
	public TypeForms getTypeForm() {
		return TypeForms.INITIAL;
	}

	public List<DerivativeForm> getDerivativeForms() {
		return derivativeForms;
	}

	@Override
	public InitialForm getInitialForm() {
		return this;
	}
}
