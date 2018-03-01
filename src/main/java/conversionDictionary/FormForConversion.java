package conversionDictionary;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class FormForConversion {

    private boolean isInitialForm;
    private byte partOfSpeech;
    private int keyInBd;
    private long morfCharacteristics;
    private String stringFrom;

    public void steIsInitialForm(boolean isInitialForm) {
        this.isInitialForm = isInitialForm;
    }

    public void setPartOfSpeec(byte partOfSpeech) {
        this.partOfSpeech = partOfSpeech;
    }

    public void setKeyInBd(int keyInBd) {
        this.keyInBd = keyInBd;
    }

    public void setMorfCharacteristics(long morfCharacteristics) {
        this.morfCharacteristics = morfCharacteristics;
    }

    public void setStringName(String stringFrom, boolean isInitialForm) {
        this.stringFrom = stringFrom;
        this.isInitialForm = isInitialForm;
    }

    public boolean getIsInitialForm() {
        return isInitialForm;
    }

    public String getStringFrom() {
        return stringFrom;
    }

    public byte[] getByteFileFormat() {
        List<Byte> byteList = new ArrayList<>();
        byteList.addAll(Arrays.asList(getBytes(keyInBd)));
        byteList.addAll(Arrays.asList(getBytes(stringFrom.hashCode())));
        byteList.addAll(Arrays.asList(partOfSpeech));
        byteList.addAll(Arrays.asList(getBytes(morfCharacteristics)));
        return conversionByte(byteList);
    }

    public byte[] conversionByte(List<Byte> byteList) {
        byte[] bytes = new byte[byteList.size()];
        for(int i = 0; i < byteList.size(); i++) {
            bytes[i] = byteList.get(i);
        }
        return bytes;
    }

    public static Byte[] getBytes(int value) {
        Byte[] bytes = new Byte[]{
                (byte) (value >> 24),
                (byte) (value >> 16),
                (byte) (value >> 8),
                (byte) (value)
        };
        return bytes;
    }

    public static byte[] getPrimitiveBytes(int value) {
        byte[] bytes = new byte[]{
                (byte) (value >> 24),
                (byte) (value >> 16),
                (byte) (value >> 8),
                (byte) (value)
        };
        return bytes;
    }


    public static Byte[] getBytes(long value) {
        Byte[] bytes = new Byte[]{
                (byte) (value >> 56),
                (byte) (value >> 48),
                (byte) (value >> 40),
                (byte) (value >> 32),
                (byte) (value >> 24),
                (byte) (value >> 16),
                (byte) (value >> 8),
                (byte) (value)
        };
        return bytes;
    }

}