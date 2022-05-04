package ru.textanalysis.tawt.ms.model.jmorfsdk;

import ru.textanalysis.tawt.ms.grammeme.MorfologyParametersHelper;
import ru.textanalysis.tawt.ms.loader.DatabaseFactory;
import ru.textanalysis.tawt.ms.loader.DatabaseStrings;

import static ru.textanalysis.tawt.ms.loader.LoadHelper.getControlHashCode;
import static ru.textanalysis.tawt.ms.loader.LoadHelper.getControlValue;

/**
 * Родительский класс формы слова.
 */
public abstract class Form {

	protected final static DatabaseStrings DATABASE_FORM_STRINGS = DatabaseFactory.getInstanceDatabaseStrings();
	protected static int formCount = 0;

	protected final long morphCharacteristics;
	protected final long Link;
	protected final int formKeyInBD;
	protected final int order;

	protected Form(int formKey, long morphCharacteristics, long Link) {
		this.morphCharacteristics = morphCharacteristics;
		this.Link = Link;
		this.formKeyInBD = formKey;
		formCount++;
		order = formCount;
	}

	public int getMyFormKey() {
		return formKeyInBD;
	}

	public long getLink() {
		return Link;
	}

	public String getMyString() {
		return DATABASE_FORM_STRINGS.getLiteralById(getMyFormKey());
	}

	public boolean isFormSameByControlHash(String string) {
		return getMyControlValue() == getControlHashCode(string);
	}

	private int getMyControlValue() {
		return getControlValue(getMyFormKey());
	}

	public long getMorphCharacteristics() {
		return morphCharacteristics;
	}

	public long getMorfCharacteristicsByIdentifier(Long... identifiers) {
		long mask = 0;
		for (long identifier : identifiers) {
			mask |= identifier;
		}
		return getMorfCharacteristicsByMask(mask);
	}

	public long getMorfCharacteristicsByIdentifier(Class<?>... clazzes) {
		long mask = 0;
		for (Class<?> clazz : clazzes) {
			mask |= MorfologyParametersHelper.identifierParametersByClass(clazz);
		}
		return getMorfCharacteristicsByMask(mask);
	}

	public long getMorfCharacteristicsByMask(Long mask) {
		return morphCharacteristics & mask;
	}

	public boolean isContainsMorphCharacteristic(Class<?> clazz, long morphCharacteristic) {
		return getMorfCharacteristicsByIdentifier(clazz) == morphCharacteristic;
	}

	public boolean isContainsTypeOfSpeech(byte typeOfSpeech) {
		return getTypeOfSpeech() == typeOfSpeech;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj) {
			return true;
		}
		if (obj == null) {
			return false;
		}
		if (getClass() != obj.getClass()) {
			return false;
		}
		final Form other = (Form) obj;
		return this.getMyFormKey() == other.getMyFormKey();
	}

	@Override
	public int hashCode() {
		return getMyFormKey();
	}

	@Override
	public String toString() {
		return "{" +
			"TF=" + getTypeForm() + "," +
			"isInit=" + isInitialForm() + "," +
			"hash=" + hashCode() + "," +
			"str='" + getMyString() + "'," +
			"ToS=" + getTypeOfSpeech() + "," +
			"morf=" + morphCharacteristics +
			"}";
	}

	public int getOrder() {
		return order;
	}

	public abstract String getInitialFormString();

	public abstract int getInitialFormKey();

	public abstract byte getTypeOfSpeech();

	public abstract boolean isInitialForm();

	public abstract InitialForm getInitialForm();

	public abstract TypeForms getTypeForm();
}
