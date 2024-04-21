package ru.textanalysis.tawt.ms.dictionary.non.dictionary.words.conversion.dictionary;

import lombok.extern.slf4j.Slf4j;
import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import ru.textanalysis.tawt.ms.constant.OpenCorporaDictionaryConst;
import ru.textanalysis.tawt.ms.dictionary.non.dictionary.words.file.postfix.PostfixBinaryFile;
import ru.textanalysis.tawt.ms.dictionary.non.dictionary.words.postfix.PostfixInfo;
import ru.textanalysis.tawt.ms.dictionary.non.dictionary.words.postfix.PostfixMorphologicalCharacteristics;
import ru.textanalysis.tawt.ms.dictionary.non.dictionary.words.postfix.PostfixMorphologicalCharacteristicsInfo;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import java.io.File;
import java.util.*;
import java.util.stream.Collectors;

import static ru.textanalysis.tawt.ms.constant.Const.COMMA_SEPARATOR;
import static ru.textanalysis.tawt.ms.constant.Const.TAB_SEPARATOR;

@Slf4j
public class OpenCorporaXmlPostfixDictionaryBuilder {

	private static final PostfixMorphologicalCharacteristicsInfo postfixMorphologicalCharacteristicsInfo = new PostfixMorphologicalCharacteristicsInfo();

	private static final HashMap<String, List<String>> wordMapper = new HashMap<>();

	public static void main(String[] args) {
		OpenCorporaXmlPostfixDictionaryBuilder openCorporaXmlPostfixDictionaryBuilder = new OpenCorporaXmlPostfixDictionaryBuilder();
		openCorporaXmlPostfixDictionaryBuilder.conversionDictionary("dict.opcorpora.xml");
	}

	public void conversionDictionary(String sourceDictionaryPath, String... additionalDictionaryPaths) {
		convertLemmasFromInitDictionary(sourceDictionaryPath, additionalDictionaryPaths);
	}

	private void convertLemmasFromInitDictionary(String sourceDictionaryPath, String... additionalDictionaryPaths) {
		List<String> dictionaryPaths = new ArrayList<>();
		dictionaryPaths.add(sourceDictionaryPath);
		dictionaryPaths.addAll(Arrays.asList(additionalDictionaryPaths));

		try {
			DocumentBuilder documentBuilder = DocumentBuilderFactory.newInstance().newDocumentBuilder();
			for (String dictionaryPath : dictionaryPaths) {
				Document document = documentBuilder.parse(new File(dictionaryPath).toURI().toString());

				Node dictionary = document.getDocumentElement();
				NodeList dictionaryProps = dictionary.getChildNodes();
				for (int i = 0; i < dictionaryProps.getLength(); i++) {
					Node node = dictionaryProps.item(i);
					if (node.getNodeType() != Node.TEXT_NODE && (node.getNodeName().equals("grammemes") || node.getNodeName().equals("restrictions") || node.getNodeName().equals("link_types"))) {
						while (node.hasChildNodes()) {
							node.removeChild(node.getFirstChild());
						}
					}
				}
				for (int i = 0; i < dictionaryProps.getLength(); i++) {
					Node lemmata = dictionaryProps.item(i);
					if (lemmata.getNodeType() != Node.TEXT_NODE && lemmata.getNodeName().equals(OpenCorporaDictionaryConst.LEMMATA)) {
						NodeList lemmataProps = lemmata.getChildNodes();
						for (int j = 0; j < lemmataProps.getLength(); j++) {
							Node lemma = lemmataProps.item(j);
							if (lemma.getNodeType() != Node.TEXT_NODE && lemma.getNodeName().equals(OpenCorporaDictionaryConst.LEMMA)) {
								String id = lemma.getAttributes().getNamedItem(OpenCorporaDictionaryConst.ID).getNodeValue();
								String commonCharacteristics = "";
								NodeList lemmaProps = lemma.getChildNodes();
								List<String> lemmaWords = new ArrayList<>();
								for (int k = 0; k < lemmaProps.getLength(); k++) {
									Node value = lemmaProps.item(k);
									if (value.getNodeType() != Node.TEXT_NODE && value.getNodeName().equals("l")) {
										NodeList valueProps = value.getChildNodes();
										for (int m = 0; m < valueProps.getLength(); m++) {
											Node characteristic = valueProps.item(m);
											if (characteristic.getNodeType() != Node.TEXT_NODE && characteristic.getNodeName().equals("g")) {
												commonCharacteristics += characteristic.getAttributes().getNamedItem("v").getNodeValue();
												commonCharacteristics += COMMA_SEPARATOR;
											}
										}
										if (commonCharacteristics.length() > 0) {
											commonCharacteristics = commonCharacteristics.substring(0, commonCharacteristics.length() - 1);
										}
									} else if (value.getNodeType() != Node.TEXT_NODE && value.getNodeName().equals("f")) {
										StringBuilder formCharacteristics = new StringBuilder(value.getAttributes().getNamedItem("t").getNodeValue());
										formCharacteristics.append(TAB_SEPARATOR);
										formCharacteristics.append(commonCharacteristics);
										NodeList valueProps = value.getChildNodes();
										for (int m = 0; m < valueProps.getLength(); m++) {
											Node characteristic = valueProps.item(m);
											if (characteristic.getNodeType() != Node.TEXT_NODE && characteristic.getNodeName().equals("g")) {
												formCharacteristics.append(COMMA_SEPARATOR);
												formCharacteristics.append(characteristic.getAttributes().getNamedItem("v").getNodeValue());
											}
										}
										lemmaWords.add(formCharacteristics.toString());
									}
								}
								wordMapper.put(id, new ArrayList<>(lemmaWords));
								lemmaWords.clear();
							}
						}
					} else if (lemmata.getNodeType() != Node.TEXT_NODE && lemmata.getNodeName().equals(OpenCorporaDictionaryConst.LINKS)) {
						NodeList lemmataProps = lemmata.getChildNodes();
						for (int j = 0; j < lemmataProps.getLength(); j++) {
							Node lemma = lemmataProps.item(j);
							if (lemma.getNodeType() != Node.TEXT_NODE && lemma.getNodeName().equals(OpenCorporaDictionaryConst.LINK)) {
								if (Objects.equals(lemma.getAttributes().getNamedItem("type").getNodeValue(), "3") ||
										Objects.equals(lemma.getAttributes().getNamedItem("type").getNodeValue(), "21") ||
										Objects.equals(lemma.getAttributes().getNamedItem("type").getNodeValue(), "23") ||
										Objects.equals(lemma.getAttributes().getNamedItem("type").getNodeValue(), "27")) {
									continue;
								}
								String fromId = lemma.getAttributes().getNamedItem(OpenCorporaDictionaryConst.FROM).getNodeValue();
								String toId = lemma.getAttributes().getNamedItem(OpenCorporaDictionaryConst.TO).getNodeValue();
								if (wordMapper.containsKey(fromId) && wordMapper.containsKey(toId)) {
									wordMapper.get(fromId).addAll(wordMapper.get(toId));
									wordMapper.remove(toId);
								}
							}
						}
					}
				}
			}
			wordMapper.forEach((key, value) -> {
						List<String> lemmaList = value.stream().map(lemma -> lemma.split("\t")[0].replace("ё", "е")).toList();
						List<String> posList = value.stream().map(lemma -> lemma.split("\t")[1].split(",")[0]).toList();
						List<String> tagsList = value.stream().map(lemma -> Arrays.stream(lemma.split("\t")[1].split(","))
								.skip(1)
								.collect(Collectors.joining(","))).toList();
						String stem = getStem(lemmaList);
						List<String> prefixes = lemmaList.stream().map(lemma1 -> lemma1.substring(0, lemma1.indexOf(stem))).toList();

						List<String> suffixes = new ArrayList<>();
						for (int lemmaId = 0; lemmaId < lemmaList.size(); lemmaId++) {
							if (prefixes.get(lemmaId).length() + stem.length() + 2 >= lemmaList.get(lemmaId).length()) {
								if (prefixes.get(lemmaId).length() + stem.length() > 1) {
									suffixes.add(lemmaList.get(lemmaId).substring(prefixes.get(lemmaId).length() + stem.length() - 2));
								} else if (prefixes.get(lemmaId).length() + stem.length() > 0) {
									suffixes.add(lemmaList.get(lemmaId).substring(prefixes.get(lemmaId).length() + stem.length() - 1));
								} else {
									suffixes.add(lemmaList.get(lemmaId).substring(prefixes.get(lemmaId).length() + stem.length()));
								}
							} else if (prefixes.get(lemmaId).length() + stem.length() + 1 >= lemmaList.get(lemmaId).length()) {
								if (prefixes.get(lemmaId).length() + stem.length() > 0) {
									suffixes.add(lemmaList.get(lemmaId).substring(prefixes.get(lemmaId).length() + stem.length() - 1));
								} else {
									suffixes.add(lemmaList.get(lemmaId).substring(prefixes.get(lemmaId).length() + stem.length()));
								}
							} else {
								suffixes.add(lemmaList.get(lemmaId).substring(prefixes.get(lemmaId).length() + stem.length()));
							}
						}
						List<PostfixMorphologicalCharacteristics> listPost = new ArrayList<>();
						for (int lemmaId = 0; lemmaId < lemmaList.size(); lemmaId++) {
							listPost.add(new PostfixMorphologicalCharacteristics(suffixes.get(lemmaId), posList.get(lemmaId), Arrays.stream(tagsList.get(lemmaId).split(",")).toList()));
						}
						postfixMorphologicalCharacteristicsInfo.add(new PostfixInfo(
								new PostfixMorphologicalCharacteristics(suffixes.get(0), posList.get(0), Arrays.stream(tagsList.get(0).split(",")).toList()),
								listPost
						));
					}
			);
			PostfixBinaryFile postfixBinaryFile = new PostfixBinaryFile(postfixMorphologicalCharacteristicsInfo);
			postfixBinaryFile.recreate();
		} catch (Exception ex) {
			log.error("Ошибка при чтении файла. Файл: {}", sourceDictionaryPath, ex);
		}
	}

	private static String getStem(List<String> lemmaWords) {
		if (lemmaWords == null || lemmaWords.isEmpty()) {
			return "";
		}
		if (lemmaWords.size() == 1) {
			return lemmaWords.get(0);
		}
		String stem = "";
		for (int i = 0; i < lemmaWords.get(0).length(); i++) {
			for (int j = 0; j < lemmaWords.get(0).length() - i + 1; j++) {
				if (j > stem.length() && checkAllContains(lemmaWords, lemmaWords.get(0).substring(i, i + j))) {
					stem = lemmaWords.get(0).substring(i, i + j);
				}
			}
		}
		return stem;
	}

	private static boolean checkAllContains(List<String> lemmaWords, String substring) {
		return lemmaWords.stream().allMatch(lemma -> lemma.contains(substring));
	}
}