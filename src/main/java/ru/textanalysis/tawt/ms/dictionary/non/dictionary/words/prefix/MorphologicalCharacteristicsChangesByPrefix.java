package ru.textanalysis.tawt.ms.dictionary.non.dictionary.words.prefix;

import lombok.extern.slf4j.Slf4j;
import ru.textanalysis.tawt.ms.dictionary.non.dictionary.words.convertor.TagsForNonDictionaryWordConversion;
import ru.textanalysis.tawt.ms.grammeme.MorfologyParametersHelper;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;

@Slf4j
public class MorphologicalCharacteristicsChangesByPrefix {

	private List<Byte> addCharacteristics;
	private List<Byte> deleteCharacteristics;
	private final TagsForNonDictionaryWordConversion tagsForNonDictionaryWordConversion;

	public MorphologicalCharacteristicsChangesByPrefix(TagsForNonDictionaryWordConversion tagsForNonDictionaryWordConversion) {
		this.addCharacteristics = new ArrayList<>();
		this.deleteCharacteristics = new ArrayList<>();
		this.tagsForNonDictionaryWordConversion = tagsForNonDictionaryWordConversion;
	}

	public void add(List<String> addCharacteristics, List<String> deleteCharacteristics) {
		addChanges(addCharacteristics, true);
		addChanges(deleteCharacteristics, false);

		checkIntersections();
	}

	public List<Byte> getCharacteristicChanges() {
		List<Byte> characteristicChanges = new ArrayList<>();
		characteristicChanges.addAll(addCharacteristics);
		characteristicChanges.addAll(deleteCharacteristics);
		return characteristicChanges;
	}

	private void addChanges(List<String> morphologyCharacteristicsList, boolean isAdding) {
		for (String morphologyCharacteristic : morphologyCharacteristicsList) {
			if (!tagsForNonDictionaryWordConversion.getIgnoredCharacteristics().contains(morphologyCharacteristic)) {
				long morphologyParameter;
				try {
					morphologyParameter = MorfologyParametersHelper.getParameter(morphologyCharacteristic);
				} catch (Exception e) {
					log.error(String.format("Морфологическая характеристика %s не была найдена", morphologyCharacteristic), e);
					return;
				}
				List<Integer> bits = getBitNumbers(morphologyParameter);
				for (Integer bit : bits) {
					byte bitChange = (byte) bit.intValue();
					if (isAdding) {
						bitChange = (byte) (bitChange | 128);
						this.addCharacteristics.add(bitChange);
					} else {
						bitChange = (byte) (bitChange & 63);
						this.deleteCharacteristics.add(bitChange);
					}
				}
			}
		}
	}

	private void checkIntersections() {
		List<Byte> removedBits = new ArrayList<>();
		for (Byte addCharacteristic : addCharacteristics) {
			byte addCharacteristicBit = (byte) (addCharacteristic & 63);
			for (Byte deleteCharacteristic : deleteCharacteristics) {
				byte deleteCharacteristicBit = (byte) (deleteCharacteristic & 63);
				if (addCharacteristicBit == deleteCharacteristicBit) {
					removedBits.add(addCharacteristicBit);
				}
			}
		}
		removedBits = new ArrayList<>(new HashSet<>(removedBits));
		for (int i = addCharacteristics.size() - 1; i >= 0; i--) {
			if (removedBits.contains((byte) (addCharacteristics.get(i) & 63))) {
				addCharacteristics.remove(i);
			}
		}
		for (int i = deleteCharacteristics.size() - 1; i >= 0; i--) {
			if (removedBits.contains((byte) (deleteCharacteristics.get(i) & 63))) {
				deleteCharacteristics.remove(i);
			}
		}

		addCharacteristics = new ArrayList<>(new HashSet<>(addCharacteristics));
		deleteCharacteristics = new ArrayList<>(new HashSet<>(deleteCharacteristics));
	}

	private List<Integer> getBitNumbers(Long characteristics) {
		List<Integer> bitNumbers = new ArrayList<>();
		int position = 0;
		while (characteristics != 0) {
			if ((characteristics & 1) != 0) {
				bitNumbers.add(position);
			}
			position++;
			characteristics = characteristics >>> 1;
		}
		return bitNumbers;
	}
}
