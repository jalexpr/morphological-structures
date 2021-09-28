package ru.textanalysis.tawt.ms.model.sp;

import lombok.Getter;
import lombok.Setter;

import java.util.List;
import java.util.function.Consumer;
import java.util.function.Function;

@Getter
@Setter
public class BearingPhrase {

	/**
	 * Главное слово опорного оборота
	 */
	protected Word mainWord; //todo расширить до списка

	/**
	 * Список всех слов в опорном обороте
	 */
	protected final List<Word> words;

	public BearingPhrase(List<Word> words) {
		this.words = words;
	}

	public <T> T applyFunction(Function<List<Word>, T> function) {
		return function.apply(words);
	}

	public void applyConsumer(Consumer<List<Word>> consumer) {
		consumer.accept(words);
	}

	@Override
	public String toString() {
		return "BearingPhrase{" +
			"\n\twords=" + words +
			",\n\tmainOmoForm=" + mainWord +
			"\n}";
	}
}
