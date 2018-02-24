package grammeme;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;
import grammeme.MorfologyParameters.*;
import java.util.Collection;

public final class MorfologyParametersHelper {

    public final static Map<Long, String> PARAMETERS_STRING = new HashMap<Long, String>();
    public final static Map<Byte, String> TYPE_OF_SPEECH_STRING = new HashMap<Byte, String>();
    private final static Map<String, Long> IDENTIFIER_PARAMETERS_BY_CLASS = new HashMap<>();

    static {
        initIdentifierParametersMap();
        initStringParameters();
    }

    private MorfologyParametersHelper() {}

    private static void initIdentifierParametersMap() {
        for (Class<?> parameterClass : MorfologyParameters.class.getDeclaredClasses()) {
            for (Field field : parameterClass.getFields()) {
                if("IDENTIFIER".equals(field.getName())) {
                    try {
                        IDENTIFIER_PARAMETERS_BY_CLASS.put(parameterClass.getSimpleName(), field.getLong(field));
                    } catch (IllegalArgumentException | IllegalAccessException ex) {
                        Logger.getLogger(MorfologyParametersHelper.class.getName()).log(Level.SEVERE, null, ex);
                    }
                }
            }
        }
    }

    private static void initStringParameters() {
        for (Class<?> parameterClass : MorfologyParameters.class.getDeclaredClasses()) {
            String parameterClassName = parameterClass.getSimpleName();
            if (!TypeOfSpeech.class.getSimpleName().equals(parameterClassName)) {
                initIdentifiearByParameterClass(PARAMETERS_STRING, parameterClass);
            } else {
                initIdentifiearByParameterClass(TYPE_OF_SPEECH_STRING, parameterClass);
            }
        }
    }

    private static <T extends Number> void initIdentifiearByParameterClass(
            Map<T, String> parametersString, Class<?> parameterClass) {
        try {
            for (Field characteristic : parameterClass.getFields()) {
                if (!"IDENTIFIER".equals(characteristic.getName())) {
                    parametersString.put((T) characteristic.get(null), characteristic.getName());
                }
            }
        } catch (IllegalArgumentException | IllegalAccessException ex) {
            Logger.getLogger(MorfologyParametersHelper.class.getName()).log(Level.SEVERE,
                    String.format("class: %s", parameterClass.getSimpleName()), ex);
        }
    }

    public static String getParametersName(Long longs) {
        return PARAMETERS_STRING.get(longs);
    }

    public static long identifierParametersByClass(Class clazz) {
        return identifierParametersByClass(clazz.getSimpleName());
    }

    public static long identifierParametersByClass(String className) {
        return IDENTIFIER_PARAMETERS_BY_CLASS.get(className);
    }

    public static Collection<Long> getIdentifiers() {
        return IDENTIFIER_PARAMETERS_BY_CLASS.values();
    }

}
