package ru.textanalysis.tawt.ms.model.jmorfsdk;

/**
 * Производная форма
 */
public final class DerivativeForm extends Form {

	private InitialForm initialForm;

	public DerivativeForm(int formKey, long morfCharacteristics) {
		super(formKey, morfCharacteristics);
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
		return TypeForms.DERIVATIVE;
	}

	void setInitialForm(InitialForm initialForm) {
		this.initialForm = initialForm;
	}
}
