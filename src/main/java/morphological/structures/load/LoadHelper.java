package morphological.structures.load;

import morphological.structures.internal.Property;
import template.wrapper.hash.CityHash;

import static morphological.structures.internal.Property.KEY_OFFSET;

public class LoadHelper {

    public final static byte CONTROL_OFFSET = Property.CONTROL_OFFSET;

    public static int getHashCode(String str) {
        return (int)CityHash.cityHash64(str);
    }

    public static int createControlHash(String str) {
        return ((int) getControlHashCode(str)) << 24;
    }

    public static byte getControlHashCode(String str) {
        return (byte)(str.hashCode() >> CONTROL_OFFSET);
    }

    public static int createKeyWithControlCode(int start, int oldKey, String str) throws Exception {
        return createKeyWithControlCode(start + (oldKey << KEY_OFFSET), getControlHashCode(str));
    }

    private static int createKeyWithControlCode(int oldKey, byte controlHash) throws Exception {
        //Проверка, что последнии значения ноль
//        if(((byte)(oldKey >> 24)) == 0) {
//            return ((int)controlHash) + (oldKey << 8);
//        } else {
//            throw new Exception(String.format("Ключ: %d имеет не пустые 8 последнии битов", oldKey));
//        }
        return oldKey;
    }

    public static byte getControlValue(int key) {
        return (byte)key;
    }

}
