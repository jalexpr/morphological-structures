package ru.textanalysis.tawt.ms.grammeme;

public enum ShortBearingForm {
	VERB(MorfologyParameters.TypeOfSpeech.VERB),
	ADJECTIVE_FULL(MorfologyParameters.TypeOfSpeech.ADJECTIVE_FULL),
	GERUND(MorfologyParameters.TypeOfSpeech.GERUND),
	GERUND_IMPERFECT(MorfologyParameters.TypeOfSpeech.GERUND_IMPERFECT),
	GERUND_SHI(MorfologyParameters.TypeOfSpeech.GERUND_SHI);


	private final byte value;

	ShortBearingForm(byte value) {
		this.value = value;
	}

	public boolean equals(ShortBearingForm bearingForm) {
		return this.value == bearingForm.value;
	}

	public boolean equals(byte value) {
		return this.value == value;
	}

	public static boolean contains(byte value) {
		for (ShortBearingForm bearingForm : ShortBearingForm.values()) {
			if (bearingForm.equals(value)) {
				return true;
			}
		}
		return false;
	}
}
