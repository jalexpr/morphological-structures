package ru.textanalysis.tawt.ms.dictionary.non.dictionary.words.convertor;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static ru.textanalysis.tawt.ms.constant.TypeOfSpeechs.*;

/**
 * Хранение преобразованания тегов с Wiktionary в стандарт JMorfSdk
 */
public class TagsForNonDictionaryWordConversion {

	private final Map<String, String> tagsDictionary;
	private final List<String> toSDictionary;
	private final List<String> ignoredCharacteristics;

	public TagsForNonDictionaryWordConversion() {
		tagsDictionary = new HashMap<>();
		tagsDictionary.put("прилагательное", "ADJF");
		tagsDictionary.put("качественное", "qual");
		tagsDictionary.put("ед. ч.", "sing");
		tagsDictionary.put("мн. ч.", "plur");
		tagsDictionary.put("муж. р.", "masc");
		tagsDictionary.put("ср. р.", "neut");
		tagsDictionary.put("жен. р.", "femn");
		tagsDictionary.put("существительное", "NOUN");
		tagsDictionary.put("притяжательное прилагательное", "ADJF,poss");
		tagsDictionary.put("прилагательное (притяжательное)", "ADJF,poss");
		tagsDictionary.put("прилагательное (относительное)", "ADJF");
		tagsDictionary.put("относительное прилагательное", "ADJF");
		tagsDictionary.put("местоименное прилагательное", "ADJF,apro");
		tagsDictionary.put("притяжательное местоимение (местоименное прилагательное)", "NPRO,poss");
		tagsDictionary.put("определительное местоимение (местоименное прилагательное)", "ADJF,apro");
		tagsDictionary.put("общий род (может согласовываться с другими частями речи как мужского, так и женского рода)", "ms-f");
		tagsDictionary.put("им.", "nomn");
		tagsDictionary.put("р.", "gent");
		tagsDictionary.put("д.", "datv");
		tagsDictionary.put("в.", "accs");
		tagsDictionary.put("т.", "ablt");
		tagsDictionary.put("тв.", "ablt");
		tagsDictionary.put("п.", "loct");
		tagsDictionary.put("пр.", "loct");
		tagsDictionary.put("одуш.", "anim");
		tagsDictionary.put("неод.", "inan");
		tagsDictionary.put("глагол", "VERB");
		tagsDictionary.put("одушевлённое", "anim");
		tagsDictionary.put("неодушевлённое", "inan");
		tagsDictionary.put("совершенный вид", "perf");
		tagsDictionary.put("переходный", "tran");
		tagsDictionary.put("несовершенный вид", "impf");
		tagsDictionary.put("непереходный", "intr");
		tagsDictionary.put("будущ.", "futr");
		tagsDictionary.put("прош.", "past");
		tagsDictionary.put("наст.", "pres");
		tagsDictionary.put("повелит.", "impr");
		tagsDictionary.put("средний род", "neut");
		tagsDictionary.put("мужской род", "masc");
		tagsDictionary.put("женский род", "femn");
		tagsDictionary.put("действительное причастие", "PRTF,actv");
		tagsDictionary.put("страдательное причастие", "PRTF,pssv");
		tagsDictionary.put("совершенного вида", "perf");
		tagsDictionary.put("несовершенного вида", "impf");
		tagsDictionary.put("прошедшего времени", "past");
		tagsDictionary.put("настоящего времени", "pres");
		tagsDictionary.put("будущего времени", "futr");
		tagsDictionary.put("невозвратное деепричастие", "GRND");
		tagsDictionary.put("возвратное деепричастие", "GRND,refl");
		tagsDictionary.put("возвратный", "refl");
		tagsDictionary.put("возвратное причастие", "PRTF,refl");
		tagsDictionary.put("числительное", "NUMR");
		tagsDictionary.put("порядковое", "anum");
		tagsDictionary.put("вводное слово", "CONJ,prnt");
		tagsDictionary.put("также вводное слово", "prnt");
		tagsDictionary.put("наречие", "ADVB");
		tagsDictionary.put("предлог", "PREP");
		tagsDictionary.put("союз", "CONJ");
		tagsDictionary.put("частица", "PRCL");
		tagsDictionary.put("междометие", "INTJ");
		tagsDictionary.put("я", "1per");
		tagsDictionary.put("мы", "1per");
		tagsDictionary.put("ты", "2per");
		tagsDictionary.put("вы", "2per");
		tagsDictionary.put("он она оно", "3per");
		tagsDictionary.put("они", "3per");
		tagsDictionary.put("местоимение (притяжательное)", "ADJF,apro");
		tagsDictionary.put("предикатив", "PRED");
		tagsDictionary.put("порядковое числительное (счётное прилагательное)", "ADJF,anum");
		tagsDictionary.put("порядковое числительное", "ADJF,anum");
		tagsDictionary.put("счётное прилагательное", "ADJF,anum");

		toSDictionary = new ArrayList<>();
		toSDictionary.add(ADJF);
		toSDictionary.add(ADJS);
		toSDictionary.add(NOUN);
		toSDictionary.add(PRED);
		toSDictionary.add(INTJ);
		toSDictionary.add(PRCL);
		toSDictionary.add(CONJ);
		toSDictionary.add(PREP);
		toSDictionary.add(ADVB);
		toSDictionary.add(NUMR);
		toSDictionary.add(PRTF);
		toSDictionary.add(PRTS);
		toSDictionary.add(GRND);
		toSDictionary.add(VERB);
		toSDictionary.add(INFN);
		toSDictionary.add(NPRO);

		ignoredCharacteristics = new ArrayList<>();
		ignoredCharacteristics.add("anim");
		ignoredCharacteristics.add("inan");
		ignoredCharacteristics.add("sing");
		ignoredCharacteristics.add("plur");
		ignoredCharacteristics.add("masc");
		ignoredCharacteristics.add("neut");
		ignoredCharacteristics.add("femn");
		ignoredCharacteristics.add("ms-f");
		ignoredCharacteristics.add("tran");
		ignoredCharacteristics.add("intr");
		ignoredCharacteristics.add("refl");
		ignoredCharacteristics.add("prnt");
		ignoredCharacteristics.add("apro");
		ignoredCharacteristics.add("futr");
		ignoredCharacteristics.add("past");
		ignoredCharacteristics.add("pres");
		ignoredCharacteristics.add("impr");
		ignoredCharacteristics.add("qual");
	}

	public Map<String, String> getTags() {
		return tagsDictionary;
	}

	public List<String> getToS() {
		return toSDictionary;
	}

	public List<String> getIgnoredCharacteristics() {
		return ignoredCharacteristics;
	}
}