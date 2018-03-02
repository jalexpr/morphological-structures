package grammeme;

import java.lang.reflect.Field;
import java.util.*;
import java.util.logging.Level;
import java.util.logging.Logger;
import grammeme.MorfologyParameters.*;

public final class MorfologyParametersHelper {

    public final static Map<Long, String> PARAMETERS_STRING = new HashMap<>();
    public final static Map<String, Long> PARAMETERS_STRING_SHOT = new HashMap<>();
    public final static Map<Byte, String> TYPE_OF_SPEECH_STRING = new HashMap<>();
    public final static Map<String, Byte> TYPE_OF_SPEECH_STRING_SHOT = new HashMap<>();
    private final static Map<String, Long> IDENTIFIER_PARAMETERS_BY_CLASS = new HashMap<>();

    static {
        initIdentifierParametersMap();
        initStringParameters();
        iniShotStringParameters();
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

    private static void iniShotStringParameters() {
        TYPE_OF_SPEECH_STRING_SHOT.putAll(createShotStringParameters(TYPE_OF_SPEECH_STRING));
        PARAMETERS_STRING_SHOT.putAll(createShotStringParameters(PARAMETERS_STRING));
    }

    private static <T extends Number> Map<String, T> createShotStringParameters(Map<T, String> mapSourcesParemeters) {
        Map<String, T> mapParemeters = new HashMap<>();
        for(T key : mapSourcesParemeters.keySet()) {
            String shotValue = mapSourcesParemeters.get(key).substring(0,4).toLowerCase();
            mapParemeters.put(shotValue, key);
        }
        return mapParemeters;
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

//    @Test
    public static void main(String[] args) {
        List<String> arr = new ArrayList<>(Arrays.asList("noun", "adjf", "adjs", "comp", "verb", "infn", "prtf", "prts",
                "grnd", "numr", "advb", "npro", "pred", "prep", "conj", "prcl", "intj", "anim", "inan", "masc", "femn",
                "neut", "ms-f", "sing", "plur", "sgtm", "pltm", "fixd", "case", "nomn", "gent", "datv", "accs", "ablt",
                "loct", "voct", "gen1", "gen2", "acc2", "loc1", "loc2", "abbr", "name", "surn", "patr", "geox", "orgn",
                "trad", "subx", "supr", "qual", "apro", "anum", "poss", "v-ey", "v-oy", "cmp2", "v-ej", "perf", "impf",
                "tran", "intr", "impe", "impx", "mult", "refl", "1per", "2per", "3per", "pres", "past", "futr", "indc",
                "impr", "incl", "excl", "actv", "pssv", "infr", "slng", "arch", "litr", "erro", "dist", "ques", "dmns",
                "prnt", "v-be", "v-en", "v-ie", "v-bi", "fimp", "prdx", "coun", "coll", "v-sh", "af-p", "inmx", "vpre",
                "anph", "init", "adjx", "ms-f"));
        for(String str : TYPE_OF_SPEECH_STRING_SHOT.keySet()) {
            if(!arr.remove(str)) {
                System.out.println(str);
            }
        }

        for(String str : PARAMETERS_STRING_SHOT.keySet()) {
            if(!arr.remove(str)) {
                System.out.println(str);
            }
        }

    }

}
