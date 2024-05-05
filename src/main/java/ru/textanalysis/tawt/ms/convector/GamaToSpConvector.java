package ru.textanalysis.tawt.ms.convector;

import ru.textanalysis.tawt.ms.model.sp.BearingPhrase;
import ru.textanalysis.tawt.ms.model.sp.Sentence;
import ru.textanalysis.tawt.ms.model.sp.Word;

import java.util.List;
import java.util.stream.Collectors;

public class GamaToSpConvector {

	public Sentence convert(ru.textanalysis.tawt.ms.model.gama.Sentence sentence) {
		List<BearingPhrase> bearingPhrases = sentence.getBearingPhrases().stream()
			.map(this::convert)
			.filter(bearingPhrase -> !bearingPhrase.getWords().isEmpty())
			.toList();
		return new Sentence(bearingPhrases);
	}


	public BearingPhrase convert(ru.textanalysis.tawt.ms.model.gama.BearingPhrase bearingPhrase) {
		List<Word> words = bearingPhrase.getWords().stream()
			.map(this::convert)
			.filter(word -> !word.getForms().isEmpty())
			.toList();
		return new BearingPhrase(words);
	}

	public Word convert(ru.textanalysis.tawt.ms.model.gama.Word word) {
		return new Word(word.getOmoForms());
	}
}
