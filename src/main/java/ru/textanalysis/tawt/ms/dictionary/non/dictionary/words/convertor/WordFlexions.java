package ru.textanalysis.tawt.ms.dictionary.non.dictionary.words.convertor;

import lombok.AllArgsConstructor;
import lombok.Getter;
import ru.textanalysis.tawt.ms.dictionary.non.dictionary.words.postfix.PostfixInfo;
import ru.textanalysis.tawt.ms.dictionary.non.dictionary.words.prefix.PrefixInfo;

import java.util.List;

@AllArgsConstructor
@Getter
public class WordFlexions {

	private PrefixInfo prefixInfo;
	private List<PostfixInfo> postfixInfo;
}