package ru.textanalysis.tawt.ms.model.sp;

import lombok.Getter;
import lombok.Setter;

import java.util.List;
import java.util.function.Consumer;

@Getter
@Setter
public class Sentence {

	protected List<Word> mainWord;
	protected final List<BearingPhrase> bearingPhrases;

	public Sentence(List<BearingPhrase> bearingPhrases) {
		this.bearingPhrases = bearingPhrases;
	}

	public void applyForEachBearingPhrases(Consumer<BearingPhrase> consumer) {
		bearingPhrases.forEach(consumer);
	}

	@Override
	public String toString() {
		return "\"Sentence\" = {\n" +
			"mainWord=" + mainWord + ",\n" +
			"bearingPhrases=" + bearingPhrases +
			'}';
	}
}
