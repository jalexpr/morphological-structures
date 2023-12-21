package ru.textanalysis.tawt.ms.dictionary.non.dictionary.words.postfix;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.List;

@Getter
@AllArgsConstructor
public class PostfixInfo {

	private final PostfixMorphologicalCharacteristics initialFormPostfix;
	private final List<PostfixMorphologicalCharacteristics> allFormsPostfix;
}