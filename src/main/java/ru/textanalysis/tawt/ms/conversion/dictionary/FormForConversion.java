package ru.textanalysis.tawt.ms.conversion.dictionary;

import ru.textanalysis.tawt.ms.grammeme.MorfologyParameters;
import ru.textanalysis.tawt.ms.grammeme.MorfologyParametersHelper;

import java.util.*;
import java.util.logging.Level;
import java.util.logging.Logger;

import static ru.textanalysis.tawt.ms.loader.LoadHelper.createKeyWithControlCode;
import static ru.textanalysis.tawt.ms.loader.LoadHelper.getHashCode;
import static ru.textanalysis.tawt.ms.model.MorphologicalStructuresProperty.START_ID_DERIVATIVE_FORM;
import static ru.textanalysis.tawt.ms.model.MorphologicalStructuresProperty.START_ID_INITIAL_FORM;
import static template.wrapper.conversion.Bytes.getBytes;
import static template.wrapper.conversion.Bytes.plusByte;

public class FormForConversion {

	private static final Map<String, Integer> STRING_INTEGER_WORD_FORM_MAP = new HashMap<>();
	private static final Map<String, Integer> STRING_INTEGER_INITIAL_FORM_MAP = new HashMap<>();

	private final String stringName;
	private final int key;
	private byte partOfSpeech;
    private byte[] morfCharacteristics;
	private byte[] link;
	private boolean isFirstKey;

	protected FormForConversion(String stringName, boolean isInitialForm) {
        this.stringName = stringName.toLowerCase(Locale.ROOT);
        key = createKey(isInitialForm);
		long link = 0;
		this.link = getBytes(link);
	}

    public String getStringName() {
        return stringName;
    }

	protected void setCharacteristics(String[] characteristics) {
		if (characteristics.length > 0) {
			List<String> parameters = new ArrayList<>(Arrays.asList(characteristics));
			setPartOfSpeech(conversionPartOfSpeech(parameters));
			setMorfCharacteristics(getBytes(conversionMorfCharacteristics(parameters)));
		} else {
			setPartOfSpeech((byte) 0);
			long chars = 0;
			setMorfCharacteristics(getBytes(chars));
		}
	}

	private void setPartOfSpeech(byte partOfSpeech) {
		this.partOfSpeech = partOfSpeech;
	}

	protected void setLink(int hash, int key) {
		long morphLink = hash;
		morphLink = morphLink << 32;
		morphLink += key;
		this.link = getBytes(morphLink);
	}

	private byte getPartOfSpeech() {
		return partOfSpeech;
	}

	private static byte conversionPartOfSpeech(List<String> parameters) {
		byte partOfSpeech;
		int indexEnd = parameters.size() - 1;
		try {
			partOfSpeech = MorfologyParametersHelper.getTypeOfSpeech(parameters.get(indexEnd));
			parameters.remove(indexEnd);
			if (indexEnd != 0) {
				parameters.remove(0);
			}
			return partOfSpeech;
		} catch (Exception ex) {
			try {
				partOfSpeech = MorfologyParametersHelper.getTypeOfSpeech(parameters.get(0));
				parameters.remove(0);
				if (partOfSpeech == MorfologyParameters.TypeOfSpeech.NUMERAL && parameters.size() > 0 && parameters.get(0).equals("coll")) {
					partOfSpeech = MorfologyParametersHelper.getTypeOfSpeech(parameters.get(0));
					parameters.remove(0);
				}
				if (partOfSpeech == MorfologyParameters.TypeOfSpeech.ADJECTIVE_SHORT || partOfSpeech == MorfologyParameters.TypeOfSpeech.ADJECTIVE_FULL || partOfSpeech == MorfologyParameters.TypeOfSpeech.NOUN_PRONOUN) {
					if (parameters.size() > 3 && parameters.get(3).equals("anph")) {
						partOfSpeech = MorfologyParametersHelper.getTypeOfSpeech(parameters.get(3));
						parameters.remove(3);
					}
					if (parameters.size() > 2 && parameters.get(2).equals("anph")) {
						partOfSpeech = MorfologyParametersHelper.getTypeOfSpeech(parameters.get(2));
						parameters.remove(2);
					}
					if (parameters.size() > 1 && parameters.get(1).equals("anph")) {
						partOfSpeech = MorfologyParametersHelper.getTypeOfSpeech(parameters.get(1));
						parameters.remove(1);
					}
					if (parameters.size() > 0 && parameters.get(0).equals("anph")) {
						partOfSpeech = MorfologyParametersHelper.getTypeOfSpeech(parameters.get(0));
						parameters.remove(0);
					}
				}
				return partOfSpeech;
			} catch (Exception exc) {
				String messages = String.format("Часть речи не найдена: %s и %s", parameters.get(0), parameters.get(parameters.size() - 1));
				Logger.getLogger(FormForConversion.class.getName()).log(Level.SEVERE, messages, exc);
				return 0;
			}
		}
	}

	private static long conversionMorfCharacteristics(List<String> parameters) {
		long numberParameters = 0;
		for (String parameter : parameters) {
			try {
				numberParameters |= MorfologyParametersHelper.getParameter(parameter);
			} catch (Exception ex) {
				String messages = "Характеристика не найдена: " + parameter;
				Logger.getLogger(FormForConversion.class.getName()).log(Level.SEVERE, messages, ex);
			}
		}
		return numberParameters;
	}

	private void setMorfCharacteristics(byte[] morfCharacteristics) {
		this.morfCharacteristics = morfCharacteristics;
    }

    private byte[] getMorfCharacteristics() {
        return morfCharacteristics;
    }

	private byte[] getLink() {
		return link;
	}

    public int getKey() {
        return key;
    }

    public boolean isFirstKey() {
        return isFirstKey;
    }

    private int createKey(boolean isInitialForm) {
        if (isInitialForm) {
            return createKey(STRING_INTEGER_INITIAL_FORM_MAP, START_ID_INITIAL_FORM);
        } else {
            return createKey(STRING_INTEGER_WORD_FORM_MAP, START_ID_DERIVATIVE_FORM);
        }
    }

	private int createKey(Map<String, Integer> stringFormMap, int startKey) {
        isFirstKey = stringFormMap.containsKey(getStringName());
        if (isFirstKey) {
            return stringFormMap.get(getStringName());
        } else {
            int newKey = 0;
            try {
                newKey = createKeyWithControlCode(startKey, stringFormMap.size(), getStringName());
                stringFormMap.put(getStringName(), newKey);
            } catch (Exception ex) {
                Logger.getLogger(FormForConversion.class.getName()).log(Level.SEVERE, ex.getMessage(), ex);
            }
            return newKey;
		}
	}

    public boolean isInitialForm() {
        return getKey() < START_ID_DERIVATIVE_FORM;
    }

    public byte[] getByteFileFormat() {
        byte[] hashCode = getBytes(getHashCode(getStringName()));
        byte[] bytesFormat = plusByte(hashCode, getBytes(getKey()));
        if (isInitialForm()) {
            bytesFormat = plusByte(bytesFormat, getPartOfSpeech());
        }
        bytesFormat = plusByte(bytesFormat, getMorfCharacteristics());
		bytesFormat = plusByte(bytesFormat, getLink());
        return bytesFormat;
    }

	@Override
	public int hashCode() {
		return getHashCode(getStringName());
	}

	@Override
	public boolean equals(Object obj) {
		return getStringName().equals(((FormForConversion) obj).getStringName());
	}
}
