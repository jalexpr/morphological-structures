package ru.textanalysis.tawt.ms.dictionary.parser;

import lombok.Builder;
import lombok.Getter;

@Builder
@Getter
public class InfoParserResponse<T> {

	T response;
}
