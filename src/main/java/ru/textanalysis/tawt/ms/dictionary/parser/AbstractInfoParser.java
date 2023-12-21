package ru.textanalysis.tawt.ms.dictionary.parser;

import lombok.Getter;
import lombok.Setter;

import java.util.List;

import static ru.textanalysis.tawt.ms.constant.Const.*;

@Getter
public abstract class AbstractInfoParser {

	private final List<String> lemmas;

	@Setter
	protected Integer sleepTime;
	@Setter
	protected Integer requestTimeOut;

	protected AbstractInfoParser(List<String> lemmas) {
		this.lemmas = lemmas;
		this.sleepTime = DEFAULT_SLEEP_TIME;
		this.requestTimeOut = DEFAULT_REQUEST_TIME_OUT;
	}

	protected abstract <T> InfoParserResponse<T> getInfo(String word);

	protected boolean tryAdd(String initialForm) {
		synchronized (lemmas) {
			if (!lemmas.contains(initialForm)) {
				lemmas.add(initialForm);
				return true;
			}
			return false;
		}
	}

	protected String changeSpecialCharacter(String word) {
		word = word.replaceAll("́", "").replaceAll("́ѐ", "е").replaceAll("́о̀", "о")
			.replaceAll("а̀", "а").replaceAll("ѐ", "е")
			.replaceAll("ѝ", "и").replaceAll("о̀", "о")
			.replaceAll("у̀", "у").replaceAll("ё̀", "ё")
			.replaceAll("э̀", "э").replaceAll(COMMA_SEPARATOR, "")
			.replaceAll("ѝ", "и").replaceAll("я̀", "я")
			.replaceAll("о̀", "о").replaceAll("о̀", "о")
			.replaceAll("у̀", "у").replaceAll("я̀", "я")
			.replaceAll("ы̀", "ы").replaceAll("ю̀", "ю");
		return word;
	}
}
