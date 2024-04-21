package ru.textanalysis.tawt.ms.loader;

import ru.textanalysis.tawt.ms.model.MorphologicalStructuresProperty;
import template.wrapper.hash.CityHash;

public class LoadHelper {

	public final static byte CONTROL_OFFSET = MorphologicalStructuresProperty.CONTROL_OFFSET;

	public static int getHashCode(String str) {
		return (int) CityHash.cityHash64(str);
	}

	public static int getHashCode(byte[] str) {
		return (int) CityHash.cityHash64(str);
	}

	public static int getControlHashCode(String str) {
		return (str.hashCode() >> CONTROL_OFFSET) & 255;
	}

	public static int createKeyWithControlCode(int start, int oldIndex, String str) throws Exception {
		return createKeyWithControlCode(start + (oldIndex << MorphologicalStructuresProperty.KEY_OFFSET), getControlHashCode(str));
	}

	private static int createKeyWithControlCode(int index, int controlHash) throws Exception {
		if (((byte) index) == 0) {
			return controlHash | index;
		} else {
			throw new Exception(String.format("Ключ: %d имеет не пустые 8 последнии битов", index));
		}
	}

	public static int getControlValue(int key) {
		return key & 255;
	}
}
