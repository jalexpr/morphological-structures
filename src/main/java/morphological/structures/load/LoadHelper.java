package morphological.structures.load;

import morphological.structures.internal.Property;
import template.wrapper.hash.CityHash;

public class LoadHelper {

    public final static byte CONTROL_OFFSET = Property.CONTROL_OFFSET;

    public static int getHashCode(String str) {
        return (byte) CityHash.cityHash64(str);
    }

    public static int createControlHash(String str) {
        return ((int) getControlHashCode(str)) << 24;
    }

    public static byte getControlHashCode(String str) {
        return (byte)(str.hashCode() >> CONTROL_OFFSET);
    }

    public static int createKeyWithControlCode(int oldKey, String str) throws Exception {
        return createKeyWithControlCode(oldKey, getControlHashCode(str));
    }

    private static int createKeyWithControlCode(int oldKey, byte controlHash) throws Exception {
        //Проверка, что первые значения ноль
        if(((byte)oldKey >> 24)  == 0) {
            return ((int)controlHash) << 24 | oldKey;
        } else {
            throw new Exception(String.format("Ключе: %d имеет не постуые 8 последних битов",oldKey));
        }
    }

}
