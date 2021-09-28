package ru.textanalysis.tawt.ms.grammeme;

public enum BearingForm {
	NOUN(MorfologyParameters.TypeOfSpeech.NOUN),
	VERB(MorfologyParameters.TypeOfSpeech.VERB),
	PARTICIPLE(MorfologyParameters.TypeOfSpeech.PARTICIPLE_SHORT),
	ADJECTIVE_SHORT(MorfologyParameters.TypeOfSpeech.ADJECTIVE_SHORT),
	GERUND(MorfologyParameters.TypeOfSpeech.GERUND),
	GERUND_IMPERFECT(MorfologyParameters.TypeOfSpeech.GERUND_IMPERFECT),
	GERUND_SHI(MorfologyParameters.TypeOfSpeech.GERUND_SHI),
	PARTICIPLE_FULL(MorfologyParameters.TypeOfSpeech.PARTICIPLE_FULL),
	INFINITIVE(MorfologyParameters.TypeOfSpeech.INFINITIVE);


	private final byte value;

	BearingForm(byte value) {
		this.value = value;
	}

	public boolean equals(BearingForm bearingForm) {
		return this.value == bearingForm.value;
	}

	public boolean equals(byte value) {
		return this.value == value;
	}

	public static boolean contains(byte value) {
		for (BearingForm bearingForm : BearingForm.values()) {
			if (bearingForm.equals(value)) {
				return true;
			}
		}
		return false;
	}
}
