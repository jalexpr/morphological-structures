package grammeme;

import java.lang.reflect.Field;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

public class MorfologyParametersHelper {

    private final static Map<Long, String> PARAMETERS_STRING = new HashMap<Long, String>();
    private final static Map<Byte, String> TYPE_OF_SPEECH = new HashMap<Byte, String>();

    static {
        for (Class<?> parameterClass : MorfologyParameters.class.getDeclaredClasses()) {
            String parameterClassName = parameterClass.getSimpleName();
            if (!MorfologyParameters.IdentifierMorfParameters.class.getSimpleName().equals(parameterClassName)) {
                try {
                    if (!MorfologyParameters.TypeOfSpeech.class.getSimpleName().equals(parameterClassName)) {
                        for (Field characteristic : parameterClass.getFields()) {
                            if (!"IDENTIFIER".equals(characteristic.getName())) {
                                PARAMETERS_STRING.put((Long) characteristic.get(null), characteristic.getName());
                            }
                        }
                    } else {
                        for (Field characteristic : parameterClass.getFields()) {
                            if (!"IDENTIFIER".equals(characteristic.getName())) {
                                TYPE_OF_SPEECH.put((Byte) characteristic.get(null), characteristic.getName());
                            }
                        }
                    }
                } catch (IllegalArgumentException | IllegalAccessException ex) {
                    Logger.getLogger(MorfologyParametersHelper.class.getName()).log(Level.SEVERE,
                            String.format("class: %s", parameterClass.getSimpleName()), ex);
                }
            }
        }
        System.err.println("");
    }

    private MorfologyParametersHelper() {
    }

    public static String getParametersName(Long longs) {
        return PARAMETERS_STRING.get(longs);
    }

    public static void main(String[] args) {

    }

}
