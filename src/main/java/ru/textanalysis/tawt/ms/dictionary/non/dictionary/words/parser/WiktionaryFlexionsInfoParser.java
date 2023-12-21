package ru.textanalysis.tawt.ms.dictionary.non.dictionary.words.parser;

import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import ru.textanalysis.tawt.ms.dictionary.non.dictionary.words.convertor.TagsForNonDictionaryWordConversion;
import ru.textanalysis.tawt.ms.dictionary.non.dictionary.words.convertor.TagsToWordFlexionsInfoConverter;
import ru.textanalysis.tawt.ms.dictionary.non.dictionary.words.convertor.WordFlexions;
import ru.textanalysis.tawt.ms.dictionary.non.dictionary.words.postfix.PostfixInfo;
import ru.textanalysis.tawt.ms.dictionary.non.dictionary.words.postfix.PostfixMorphologicalCharacteristics;
import ru.textanalysis.tawt.ms.dictionary.non.dictionary.words.postfix.PostfixMorphologicalCharacteristicsInfo;
import ru.textanalysis.tawt.ms.dictionary.non.dictionary.words.prefix.PrefixInfo;
import ru.textanalysis.tawt.ms.dictionary.non.dictionary.words.prefix.PrefixMorphologicalCharacteristicsChangesInfo;
import ru.textanalysis.tawt.ms.dictionary.parser.AbstractInfoParser;
import ru.textanalysis.tawt.ms.dictionary.parser.InfoParserResponse;
import ru.textanalysis.tawt.ms.dictionary.wiktionary.WiktionaryParsabilityChecker;

import java.net.SocketTimeoutException;
import java.util.*;

import static java.lang.Thread.sleep;
import static ru.textanalysis.tawt.ms.constant.Const.DEFAULT_REQUEST_TIME_OUT;
import static ru.textanalysis.tawt.ms.constant.Const.DEFAULT_SLEEP_TIME;
import static ru.textanalysis.tawt.ms.constant.TypeOfSpeechs.*;

/**
 * Парсер информации о тегах слова из Wiktionary
 */
@Slf4j
public class WiktionaryFlexionsInfoParser extends AbstractInfoParser {

	private final TagsForNonDictionaryWordConversion tagsForNonDictionaryWordConversion;
	private final TagsToWordFlexionsInfoConverter tagsToWordFormConverter;
	private WiktionaryParsabilityChecker parsabilityChecker;
	@Getter
	private final PrefixMorphologicalCharacteristicsChangesInfo prefixMorphologicalCharacteristicsChangesInfo;
	@Getter
	private final PostfixMorphologicalCharacteristicsInfo postfixMorphologicalCharacteristicsInfo;

	public WiktionaryFlexionsInfoParser(TagsForNonDictionaryWordConversion tagsForNonDictionaryWordConversion, List<String> lemmas) {
		super(lemmas);
		this.tagsForNonDictionaryWordConversion = tagsForNonDictionaryWordConversion;
		this.tagsToWordFormConverter = new TagsToWordFlexionsInfoConverter(this.tagsForNonDictionaryWordConversion);
		this.prefixMorphologicalCharacteristicsChangesInfo = new PrefixMorphologicalCharacteristicsChangesInfo();
		this.postfixMorphologicalCharacteristicsInfo = new PostfixMorphologicalCharacteristicsInfo();
	}

	@Override
	public InfoParserResponse<Boolean> getInfo(String word) {
		WordFlexions wordFlexions = getWordsTags(word, sleepTime, requestTimeOut);
		WordFlexions previousWordFlexions = null;
		boolean noPrefix = false;
		if (wordFlexions != null) {
			if (wordFlexions.getPrefixInfo() != null && wordFlexions.getPrefixInfo().getWord() != null && wordFlexions.getPrefixInfo().getPrefix() != null) {
				if (!Objects.equals(wordFlexions.getPrefixInfo().getPrefix(), "")) {
					do {
						prefixMorphologicalCharacteristicsChangesInfo.addPrefix(wordFlexions.getPrefixInfo());
						previousWordFlexions = wordFlexions;
						if (previousWordFlexions.getPostfixInfo() == null || previousWordFlexions.getPrefixInfo().getWord() == null || previousWordFlexions.getPrefixInfo().getPrefix() == null) {
							return InfoParserResponse.<Boolean>builder()
								.response(Boolean.FALSE)
								.build();
						}
						wordFlexions = getWordsTags(previousWordFlexions.getPrefixInfo().getWord().replace(previousWordFlexions.getPrefixInfo().getPrefix(), ""), sleepTime, requestTimeOut);
						if (wordFlexions != null && wordFlexions.getPostfixInfo() != null) {
							addPostfixCharacteristics(wordFlexions.getPostfixInfo());
						}
						if (wordFlexions == null || wordFlexions.getPrefixInfo() == null || Objects.equals(wordFlexions.getPrefixInfo().getPrefix(), "")) {
							noPrefix = true;
						}
					} while (!noPrefix);
				}
				if (previousWordFlexions != null && wordFlexions != null && wordFlexions.getPrefixInfo() != null && previousWordFlexions.getPrefixInfo() != null) {
					prefixMorphologicalCharacteristicsChangesInfo.add(wordFlexions.getPrefixInfo(), previousWordFlexions.getPrefixInfo());
					if (wordFlexions.getPrefixInfo().toString().contains(INFN)) {
						List<String> wordWithPrefixTags = new ArrayList<>(wordFlexions.getPrefixInfo().getTags());
						for (int i = 0; i < wordWithPrefixTags.size(); i++) {
							if (Objects.equals(wordWithPrefixTags.get(i), INFN)) {
								wordWithPrefixTags.set(i, VERB);
								break;
							}
						}
						PrefixInfo prefixInfo = new PrefixInfo(wordFlexions.getPrefixInfo().getPrefix(), wordFlexions.getPrefixInfo().getWord(), wordWithPrefixTags);
						wordWithPrefixTags = new ArrayList<>(previousWordFlexions.getPrefixInfo().getTags());
						for (int i = 0; i < wordWithPrefixTags.size(); i++) {
							if (Objects.equals(wordWithPrefixTags.get(i), INFN)) {
								wordWithPrefixTags.set(i, VERB);
								break;
							}
						}
						PrefixInfo previousPrefixInfo = new PrefixInfo(previousWordFlexions.getPrefixInfo().getPrefix(), previousWordFlexions.getPrefixInfo().getWord(), wordWithPrefixTags);
						prefixMorphologicalCharacteristicsChangesInfo.add(prefixInfo, previousPrefixInfo);
					}
					if (wordFlexions.getPostfixInfo() != null) {
						addPostfixCharacteristics(wordFlexions.getPostfixInfo());
					}
				} else if (wordFlexions != null && wordFlexions.getPostfixInfo() != null) {
					addPostfixCharacteristics(wordFlexions.getPostfixInfo());
				} else {
					return InfoParserResponse.<Boolean>builder()
						.response(Boolean.FALSE)
						.build();
				}
			} else if (wordFlexions.getPostfixInfo() != null) {
				addPostfixCharacteristics(wordFlexions.getPostfixInfo());
			} else {
				return InfoParserResponse.<Boolean>builder()
					.response(Boolean.FALSE)
					.build();
			}
		} else {
			return InfoParserResponse.<Boolean>builder()
				.response(Boolean.FALSE)
				.build();
		}

		return InfoParserResponse.<Boolean>builder()
			.response(Boolean.TRUE)
			.build();
	}

	private void addPostfixCharacteristics(List<PostfixInfo> postfixInfoList) {
		for (PostfixInfo postfixInfo : postfixInfoList) {
			postfixMorphologicalCharacteristicsInfo.add(postfixInfo);
		}
	}

	/**
	 * Получение информации о слове и его тегах с Wiktionary
	 *
	 * @param word           исследуемое слово
	 * @param sleepTime      время между запросами
	 * @param requestTimeOut время ожидания подключения
	 *
	 * @return the words tags
	 */
	private WordFlexions getWordsTags(String word, int sleepTime, int requestTimeOut) {
		List<PostfixMorphologicalCharacteristics> postfixChars = new ArrayList<>();
		List<PostfixInfo> postfixInfo = new ArrayList<>();
		StringBuilder stem = new StringBuilder();
		PrefixInfo lemma = null;
		if (getLemmas().contains(word.toLowerCase(Locale.ROOT))) {
			return null;
		}
		if (word.isEmpty()) {
			return null;
		}
		if (!word.matches("[а-яА-ЯёЁ-]+")) {
			return null;
		}
		try {
			sleep(sleepTime);
			boolean isConnected = checkConnection(word, sleepTime, requestTimeOut);
			if (!isConnected) {
				return null;
			}

			Element table = null;
			boolean isFoundRussianBlock = false;
			boolean isFoundWordTags = false;
			StringBuilder tagsStr = new StringBuilder();
			Element mainElement = parsabilityChecker.getDoc().getElementsByClass("mw-parser-output").first();
			Elements mainElements = mainElement != null ? mainElement.getAllElements() : null;
			if (mainElements == null) {
				return null;
			}
			for (int i = 0; i < mainElements.size(); i++) {
				if (mainElements.get(i).tagName().equals("h1")) {
					if (isFoundRussianBlock) {
						break;
					}
					if (Objects.requireNonNull(mainElements.get(i).getElementsByClass("mw-headline").first()).text().equals("Русский")) {
						isFoundRussianBlock = true;
						continue;
					}
				}
				if (isFoundRussianBlock) {
					if (mainElements.get(i).tagName().equals("h3")) {
						if (Objects.requireNonNull(mainElements.get(i).getElementsByClass("mw-headline").first()).text().equals("Морфологические и синтаксические свойства")) {
							isFoundWordTags = true;
							continue;
						}
					}
					if (isFoundWordTags) {
						if (mainElements.get(i).tagName().equals("p")) {
							tagsStr.append(mainElements.get(i).text().toLowerCase(Locale.ROOT));
							tagsStr.append(",");
							if (mainElements.get(i).text().toLowerCase(Locale.ROOT).contains("корень") &&
								!mainElements.get(i).text().toLowerCase(Locale.ROOT).contains("корень: --")) {
								String[] p = mainElements.get(i).text().toLowerCase(Locale.ROOT).split(";");
								if (mainElements.get(i).text().toLowerCase(Locale.ROOT).contains("приставк") ||
									mainElements.get(i).text().toLowerCase(Locale.ROOT).contains("префиксоид")) {
									if (p[0].contains("приставк") || p[0].contains("префиксоид")) {
										String[] p1 = p[0].split(":");
										String pref = p1[1].trim().split("-")[0];
										lemma = parsePrefixInfo(pref, tagsStr.toString(), parsabilityChecker.getInitialForm().text().toLowerCase(Locale.ROOT));
									}
								} else {
									lemma = parsePrefixInfo("", tagsStr.toString(), parsabilityChecker.getInitialForm().text().toLowerCase(Locale.ROOT));
								}
								int lastRoot = 0;
								for (int k = p.length - 1; k >= 0; k--) {
									if (p[k].contains("корен")) {
										lastRoot = k;
										break;
									}
								}
								for (int k = 0; k <= lastRoot; k++) {
									stem.append(p[k].trim().split(" ")[1].replaceAll("[.,-]$|^[.,-]", "").replaceAll("-", ""));
								}
							}
						}
						if (mainElements.get(i).tagName().equals("table")) {
							table = mainElements.get(i);
						} else if (mainElements.get(i).tagName().equals("h3") || mainElements.get(i).tagName().equals("h4")) {
							if (!Objects.equals(parsabilityChecker.getInitialForm().text().toLowerCase(Locale.ROOT).replace(stem.toString(), ""), parsabilityChecker.getInitialForm().text().toLowerCase(Locale.ROOT))) {
								List<PostfixMorphologicalCharacteristics> info = parsePostfixInfo(table, tagsStr.toString(), stem.toString());
								if (info == null) {
									return null;
								}
								if (!info.isEmpty()) {
									postfixChars.addAll(info);
									ListIterator<PostfixMorphologicalCharacteristics> iter = postfixChars.listIterator();
									Integer prtfRecordNumber = -1;
									Integer prtsRecordNumber = -1;
									PostfixMorphologicalCharacteristics prtfInitial = null;
									PostfixMorphologicalCharacteristics prtsInitial = null;
									List<PostfixMorphologicalCharacteristics> prtfPostfixChars = new ArrayList<>();
									List<PostfixMorphologicalCharacteristics> prtsPostfixChars = new ArrayList<>();
									while (iter.hasNext()) {
										PostfixMorphologicalCharacteristics postfixChar = iter.next();
										switch (postfixChar.getPartOfSpeech()) {
											case GRND:
												addPostfixInfo(postfixInfo, new PostfixMorphologicalCharacteristics(postfixChar), List.of(postfixChar));
												iter.remove();
												break;
											case PRTF:
												if (prtfInitial == null) {
													prtfRecordNumber = postfixChar.getRecordNumber();
													prtfInitial = new PostfixMorphologicalCharacteristics(postfixChar);
												} else if (!Objects.equals(prtfRecordNumber, postfixChar.getRecordNumber())) {
													addPostfixInfo(postfixInfo, prtfInitial, prtfPostfixChars);
													prtfPostfixChars = new ArrayList<>();
													prtfRecordNumber = postfixChar.getRecordNumber();
													prtfInitial = new PostfixMorphologicalCharacteristics(postfixChar);
												}
												prtfPostfixChars.add(postfixChar);
												iter.remove();
												break;
											case PRTS:
												if (prtsInitial == null) {
													prtsRecordNumber = postfixChar.getRecordNumber();
													prtsInitial = new PostfixMorphologicalCharacteristics(postfixChar);
												} else if (!Objects.equals(prtsRecordNumber, postfixChar.getRecordNumber())) {
													addPostfixInfo(postfixInfo, prtsInitial, prtsPostfixChars);
													prtsPostfixChars = new ArrayList<>();
													prtsRecordNumber = postfixChar.getRecordNumber();
													prtsInitial = new PostfixMorphologicalCharacteristics(postfixChar);
												}
												prtsPostfixChars.add(postfixChar);
												iter.remove();
												break;
										}
									}
									if (!postfixChars.isEmpty()) {
										addPostfixInfo(postfixInfo, new PostfixMorphologicalCharacteristics(postfixChars.get(0)), postfixChars);
									}
									if (prtfInitial != null && !prtfPostfixChars.isEmpty()) {
										addPostfixInfo(postfixInfo, prtfInitial, prtfPostfixChars);
									}
									if (prtsInitial != null && !prtsPostfixChars.isEmpty()) {
										addPostfixInfo(postfixInfo, prtsInitial, prtsPostfixChars);
									}
								}
								postfixChars = new ArrayList<>();
							}
							isFoundWordTags = false;
							table = null;
							stem = new StringBuilder();
							tagsStr = new StringBuilder();
						}
					}
				}
			}

			if (!tryAdd(parsabilityChecker.getInitialForm().text().toLowerCase(Locale.ROOT))) {
				return null;
			}

			return new WordFlexions(lemma, postfixInfo);
		} catch (SocketTimeoutException exc) {
			String messages = "";
			if (Objects.equals(exc.getMessage(), "Неудачное повторное соединение.")) {
				messages = "https://ru.wiktionary.org/wiki/" + word.toLowerCase(Locale.ROOT) + ". Неудачное повторное соединение.";
			} else {
				messages = "https://ru.wiktionary.org/wiki/" + word.toLowerCase(Locale.ROOT) + ". Не удалось установить соединиение.";
			}
			log.error(messages, exc);
			return null;
		} catch (NullPointerException e) {
			String messages = "Не удалось разобрать страницу.";
			log.error(messages, e);
			return null;
		} catch (Exception e) {
			log.error(e.getMessage(), e);
			return null;
		}
	}

	private void addPostfixInfo(List<PostfixInfo> postfixInfoList, PostfixMorphologicalCharacteristics postfixMorphologicalCharacteristicsInitial, List<PostfixMorphologicalCharacteristics> postfixMorphologicalCharacteristicsList) {
		PostfixInfo postfixInfo = new PostfixInfo(
			postfixMorphologicalCharacteristicsInitial,
			postfixMorphologicalCharacteristicsList);
		postfixInfoList.add(postfixInfo);
	}

	private PrefixInfo parsePrefixInfo(String prefix, String tagsStr, String word) {
		return tagsToWordFormConverter.convertImmutableToPrefixInfo(prefix.toLowerCase(Locale.ROOT) + "\t" + word.toLowerCase(Locale.ROOT) + "\t" + tagsStr);
	}

	private List<PostfixMorphologicalCharacteristics> parsePostfixInfo(Element table, String tagsStr, String stem) {
		List<PostfixMorphologicalCharacteristics> postfixChars = new ArrayList<>();
		String initialForm = parsabilityChecker.getInitialForm().text().toLowerCase(Locale.ROOT);
		if (table == null) {
			try {
				if (tagsStr.contains("наречие") || tagsStr.contains("союз") || tagsStr.contains("предлог")
					|| tagsStr.contains("частица") || tagsStr.contains("междометие") || tagsStr.contains("неизменяем")) {
					postfixChars.add(tagsToWordFormConverter.convertImmutableToPostfixInfo(initialForm.toLowerCase(Locale.ROOT) + "\t" + tagsStr));
				} else {
					log.error("Нет таблицы словоизменения для слова " + initialForm);
					return null;
				}
			} catch (Exception e) {
				log.error(e.getMessage(), e);
				return new ArrayList<>();
			}
		} else {
			Elements tags = table.getElementsByTag("tr");

			if (tagsStr.contains("глагол")) {
				postfixChars.add(tagsToWordFormConverter.convertImmutableToPostfixInfo(parsabilityChecker.getInitialForm().text().toLowerCase(Locale.ROOT).replace(stem, "") + "\t" + tagsStr.replace("глагол", "инфинитив")));
			}
			List<String> tokenTags = new ArrayList<>();
			for (int tag = 0; tag < tags.size(); tag++) {
				Elements child = tags.get(tag).children();
				int counter = 0;
				int columnNumber = 0;
				StringBuilder rowTokenTags = new StringBuilder();
				boolean repeatingForm = false;
				if (child.size() > tokenTags.size() || child.get(0).tagName().equals("th")) {
					for (int j = 0; j < child.size(); j++) {
						if (tag == 0 && j == 0 && child.get(j).tagName().equals("th")) {
							continue;
						}
						int colSpan;
						if (child.get(j).tagName().equals("th")) {
							if (child.get(j).hasAttr("colspan")) {
								colSpan = Integer.parseInt(child.get(j).attr("colspan"));
							} else {
								colSpan = 1;
							}
							if (tag == 0) {
								for (int k = 0; k < colSpan; k++) {
									tokenTags.add(child.get(j).text().toLowerCase(Locale.ROOT));
								}
							} else {
								for (int k = 0; k < colSpan; k++) {
									tokenTags.set(counter, tokenTags.get(counter) + "," + child.get(counter).text().toLowerCase(Locale.ROOT));
									counter++;
								}
							}
						} else if (child.get(j).tagName().equals("td")) {
							if (child.get(j).childrenSize() != 0 && !child.get(j).children().get(0).tagName().equals("br")) {
								if (rowTokenTags.length() == 0) {
									rowTokenTags = new StringBuilder(child.get(j).getElementsByTag("a").text().toLowerCase(Locale.ROOT));
								} else {
									rowTokenTags.append(",");
									rowTokenTags.append(child.get(j).getElementsByTag("a").text().toLowerCase(Locale.ROOT));
								}
							} else {
								String[] tex = child.get(j).text().split(" ");
								for (int p = 0; p < tex.length; p++) {
									if (tex.length > 1 && changeSpecialCharacter(tex[0].trim()).equals(changeSpecialCharacter(tex[1].trim()))) {
										repeatingForm = true;
									}
									if (!(p > 0 && repeatingForm)) {
										tex[p] = changeSpecialCharacter(tex[p]).trim();
										if (!tex[p].equals("—")) {
											if (Objects.equals(tex[p], tex[p].replace(stem, ""))) {
												continue;
											}
											String token = tex[p].replace(stem, "") + "\t" + tagsStr + "," + tokenTags.get(columnNumber) + "," + rowTokenTags;
											if (token.contains("он она оно") && tex.length > 1) {
												if (p == 0) {
													token += ",муж. р.";
												}
												if (p == 1) {
													token += ",жен. р.";
												}
												if (p == 2) {
													token += ",ср. р.";
												}
											}
											if (token.contains(",м.")) {
												continue;
											}
											if ((rowTokenTags.toString().contains("я") || rowTokenTags.toString().contains("ты") || rowTokenTags.toString().contains("вы")
												|| rowTokenTags.toString().contains("они")) && tokenTags.get(columnNumber).contains("прош.")) {
												continue;
											} else if ((rowTokenTags.toString().contains("он она оно") || rowTokenTags.toString().contains("мы"))
												&& tokenTags.get(columnNumber).contains("прош.")) {
												token = token.replaceAll("он она оно", "").replaceAll(",мы", "");
											}
											postfixChars.add(tagsToWordFormConverter.convertTableFormToPostfixInfo(token.toLowerCase(Locale.ROOT)));
										}
									}
								}
								columnNumber++;
							}
						}
					}
				} else {
					for (int j = 0; j < child.size(); j++) {
						if (child.get(j).tagName().equals("td")) {
							if (j == 0) {
								if (child.get(j).childrenSize() != 0 && !child.get(j).children().get(0).tagName().equals("br")) {
									rowTokenTags = new StringBuilder(child.get(j).getElementsByTag("a").text().toLowerCase(Locale.ROOT));
								}
							} else {
								String[] tex = child.get(j).text().split(",");
								for (int p = 0; p < tex.length; p++) {
									String token = null;
									tex[p] = changeSpecialCharacter(tex[p]).trim();
									if (!tex[p].equals("—")) {
										if (Objects.equals(tex[p], tex[p].replace(stem, ""))) {
											continue;
										}
										if (rowTokenTags.toString().contains("деепр.")) {
											if (rowTokenTags.toString().contains("наст.")) {
												if (tex[p].endsWith("сь")) {
													if (tagsStr.contains("совершенный вид") || tagsStr.contains("совершенного вида")) {
														token = tex[p].replace(stem, "") + "\t" + "возвратное деепричастие,совершенный вид,наст.";
													} else if (tagsStr.contains("несовершенный вид") || tagsStr.contains("несовершенного вида")) {
														token = tex[p].replace(stem, "") + "\t" + "возвратное деепричастие,несовершенный вид,наст.";
													}
												} else {
													if (tagsStr.contains("совершенный вид") || tagsStr.contains("совершенного вида")) {
														token = tex[p].replace(stem, "") + "\t" + "невозвратное деепричастие,совершенный вид,наст.";
													} else if (tagsStr.contains("несовершенный вид") || tagsStr.contains("несовершенного вида")) {
														token = tex[p].replace(stem, "") + "\t" + "невозвратное деепричастие,несовершенный вид,наст.";
													}
												}
											} else if (rowTokenTags.toString().contains("прош.")) {
												if (tex[p].endsWith("сь")) {
													if (tagsStr.contains("совершенный вид") || tagsStr.contains("совершенного вида")) {
														token = tex[p].replace(stem, "") + "\t" + "возвратное деепричастие,совершенный вид,прош.";
													} else if (tagsStr.contains("несовершенный вид") || tagsStr.contains("несовершенного вида")) {
														token = tex[p].replace(stem, "") + "\t" + "возвратное деепричастие,несовершенный вид,прош.";
													}
												} else {
													if (tagsStr.contains("совершенный вид") || tagsStr.contains("совершенного вида")) {
														token = tex[p].replace(stem, "") + "\t" + "невозвратное деепричастие,совершенный вид,прош.";
													} else if (tagsStr.contains("несовершенный вид") || tagsStr.contains("несовершенного вида")) {
														token = tex[p].replace(stem, "") + "\t" + "невозвратное деепричастие,несовершенный вид,прош.";
													}
												}
											}
										} else if (rowTokenTags.toString().contains("пр.")) {
											List<PostfixMorphologicalCharacteristics> v1 = parseSubFormTableInfo(tex[p], stem, DEFAULT_SLEEP_TIME, DEFAULT_REQUEST_TIME_OUT);
											if (!v1.isEmpty()) {
												for (PostfixMorphologicalCharacteristics chars : v1) {
													chars.setRecordNumber(tag);
												}
												postfixChars.addAll(v1);
											}
										}
									}
									if (token != null) {
										postfixChars.add(tagsToWordFormConverter.convertTableFormToPostfixInfo(token.toLowerCase(Locale.ROOT)));
									}
								}
							}
						}
					}
				}
			}
		}
		return postfixChars;
	}

	private List<PostfixMorphologicalCharacteristics> parseSubFormTableInfo(String word, String stem, int sleepTime, int requestTimeOut) {
		if (word.isEmpty()) {
			return new ArrayList<>();
		}
		if (!word.matches("[а-яА-ЯёЁ-]+")) {
			return new ArrayList<>();
		}
		try {
			sleep(sleepTime);
			boolean isConnected = checkConnection(word, sleepTime, requestTimeOut);
			if (!isConnected) {
				return new ArrayList<>();
			}

			Element table = null;
			boolean isFoundRussianBlock = false;
			boolean isFoundWordTags = false;
			StringBuilder tagsStr = new StringBuilder();
			Element mainElement = parsabilityChecker.getDoc().getElementsByClass("mw-parser-output").first();
			Elements mainElements = mainElement != null ? mainElement.getAllElements() : null;
			for (int i = 0; i < Objects.requireNonNull(mainElements).size(); i++) {
				if (mainElements.get(i).tagName().equals("h1")) {
					if (isFoundRussianBlock) {
						break;
					}
					if (Objects.requireNonNull(mainElements.get(i).getElementsByClass("mw-headline").first()).text().equals("Русский")) {
						isFoundRussianBlock = true;
						continue;
					}
				}
				if (isFoundRussianBlock) {
					if (mainElements.get(i).tagName().equals("h3")) {
						if (Objects.requireNonNull(mainElements.get(i).getElementsByClass("mw-headline").first()).text().equals("Морфологические и синтаксические свойства")) {
							isFoundWordTags = true;
							continue;
						}
					}
					if (isFoundWordTags) {
						if (mainElements.get(i).tagName().equals("p")) {
							tagsStr.append(mainElements.get(i).text().toLowerCase(Locale.ROOT));
							tagsStr.append(",");
						}
						if (mainElements.get(i).tagName().equals("table")) {
							table = mainElements.get(i);
						} else if (mainElements.get(i).tagName().equals("h3") || mainElements.get(i).tagName().equals("h4")) {
							if (!Objects.equals(parsabilityChecker.getInitialForm().text().toLowerCase(Locale.ROOT).replace(stem, ""), parsabilityChecker.getInitialForm().text().toLowerCase(Locale.ROOT))) {
								List<PostfixMorphologicalCharacteristics> info = parsePostfixInfo(table, tagsStr.toString(), stem);
								if (info == null) {
									return new ArrayList<>();
								}
								if (!info.isEmpty()) {
									return info;
								}
							}
						}
					}
				}
			}

			if (!tryAdd(parsabilityChecker.getInitialForm().text().toLowerCase(Locale.ROOT))) {
				return new ArrayList<>();
			}

			return new ArrayList<>();
		} catch (SocketTimeoutException exc) {
			if (Objects.equals(exc.getMessage(), "Неудачное повторное соединение.")) {
				String messages = "https://ru.wiktionary.org/wiki/" + word.toLowerCase(Locale.ROOT) + ". Неудачное повторное соединение.";
				log.error(messages, exc);
			} else {
				String messages = "https://ru.wiktionary.org/wiki/" + word.toLowerCase(Locale.ROOT) + ". Не удалось установить соединиение.";
				log.error(messages, exc);
			}
			return new ArrayList<>();
		} catch (NullPointerException e) {
			String messages = "Не удалось разобрать страницу.";
			log.error(messages, e);
			return new ArrayList<>();
		} catch (Exception e) {
			log.error(e.getMessage(), e);
			return new ArrayList<>();
		}
	}

	private boolean checkConnection(String word, int sleepTime, int requestTimeOut) throws SocketTimeoutException {
		try {
			parsabilityChecker = new WiktionaryParsabilityChecker(word.toLowerCase(Locale.ROOT), requestTimeOut);
			if (parsabilityChecker.getDoc() == null) {
				sleep(sleepTime / 2);
				boolean isSuccessful = parsabilityChecker.tryRepeatConnection(word.toLowerCase(Locale.ROOT), 2 * requestTimeOut);
				if (!isSuccessful) {
					throw new SocketTimeoutException("Неудачное повторное соединение.");
				}
			}
			if (getLemmas().contains(parsabilityChecker.getInitialForm().text().toLowerCase(Locale.ROOT))) {
				return false;
			}
			return parsabilityChecker.checkParsability(getLemmas());
		} catch (IllegalArgumentException | InterruptedException e) {
			log.error(e.getMessage(), e);
			return false;
		}
	}
}