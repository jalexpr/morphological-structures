package grammeme;

import java.lang.reflect.Field;
import java.util.*;
import java.util.logging.Level;
import java.util.logging.Logger;
import grammeme.MorfologyParameters.*;

public final class MorfologyParametersHelper {

    public final static Map<Long, String> PARAMETERS_STRING = new HashMap<>();
    public final static Map<String, Long> PARAMETERS_STRING_LONG = new HashMap<>();
    public final static Map<Byte, String> TYPE_OF_SPEECH_STRING = new HashMap<>();
    public final static Map<String, Byte> TYPE_OF_SPEECH_STRING_BYTE = new HashMap<>();
    private final static Map<String, Long> IDENTIFIER_PARAMETERS_BY_CLASS = new HashMap<>();
    private final static Map<String, String> STRING_STRING_MAP = createMap();

    static {
        initIdentifierParametersMap();
        initStringParameters();
        iniStringParameters();
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

    private static void iniStringParameters() {
        TYPE_OF_SPEECH_STRING_BYTE.putAll(createStringParameters(TYPE_OF_SPEECH_STRING));
        PARAMETERS_STRING_LONG.putAll(createStringParameters(PARAMETERS_STRING));
        //т.к. значения совпадают, необходимо добить вручную
        PARAMETERS_STRING_LONG.put("ms-f", 0L);
    }

    private static <T extends Number> Map<String, T> createStringParameters(Map<T, String> mapSourcesParemeters) {
        Map<String, T> mapParemeters = new HashMap<>();
        for(T key : mapSourcesParemeters.keySet()) {
            String nameParameterInFile = mapSourcesParemeters.get(key);
            mapParemeters.put(STRING_STRING_MAP.get(nameParameterInFile), key);
        }
        return mapParemeters;
    }

    private static <T extends Number> void initIdentifiearByParameterClass(
            Map<T, String> parametersString, Class<?> parameterClass) {
        try {
            for (Field characteristic : parameterClass.getFields()) {
                if (!"IDENTIFIER".equals(characteristic.getName()) && !"SHIFTBIT".equals(characteristic.getName())) {
                    parametersString.put((T) characteristic.get(null), characteristic.getName());
                }
            }
        } catch (IllegalArgumentException | IllegalAccessException ex) {
            Logger.getLogger(MorfologyParametersHelper.class.getName()).log(Level.SEVERE,
                    String.format("class: %s", parameterClass.getSimpleName()), ex);
        }
    }

    public static long getParameter(String parameter) throws Exception {
        try {
            return PARAMETERS_STRING_LONG.get(parameter);
        } catch (NullPointerException ex) {
            throw new Exception(ex);
        }
    }

    public static byte getTypeOfSpeech(String parameter) throws Exception{
        try {
            return TYPE_OF_SPEECH_STRING_BYTE.get(parameter);
        } catch (NullPointerException ex){
            throw new Exception(ex);
        }
    }

    public static Map<String, String> createMap() {
        Map<String, String> stringStringMap = new HashMap<>();
        String[] arrFile = {"noun", "adjf",          "adjs",           "comp",        "verb", "infn",       "prtf",           "prts",       "grnd",   "numr",    "advb",   "npro",        "pred",      "prep",    "conj",  "prcl",     "intj",         "anim",    "inan",      "masc", "femn",    "neut",   "ms-f",   "sing",     "plur",   "sgtm", "pltm", "fixd", "nomn",       "gent",     "datv",   "accs",       "ablt",    "loct",         "voct",    "gen1",      "gen2",      "acc2",        "loc1",          "loc2",          "abbr",         "name", "surn", "patr", "geox", "orgn", "trad", "subx", "supr", "qual", "apro", "anum", "poss", "v-ey", "v-oy", "cmp2", "v-ej", "perf",    "impf",      "tran", "intr", "impe", "impx", "mult", "refl", "1per", "2per", "3per", "pres",    "past", "futr",   "indc",       "impr",       "incl",      "excl",      "actv",   "pssv",    "infr", "slng", "arch", "litr", "erro", "dist", "ques", "dmns", "prnt", "v-be", "v-en", "v-ie", "v-bi", "fimp",            "prdx", "coun", "coll",              "v-sh",       "af-p", "inmx", "vpre", "anph",             "init", "adjx", "gndr"};
        String[] arrProg = {"NOUN", "ADJECTIVEFULL", "ADJECTIVESHORT", "COMPARATIVE", "VERB", "INFINITIVE", "PARTICIPLEFULL", "PARTICIPLE", "GERUND", "NUMERAL", "ADVERB", "NOUNPRONOUN", "PREDICATE", "PRETEXT", "UNION", "PARTICLE", "INTERJECTION", "ANIMATE", "INANIMATE", "MANS", "FEMININ", "NEUTER", "COMMON", "SINGULAR", "PLURAL", "SGTM", "PLTM", "FIXD", "NOMINATIVE", "GENITIVE", "DATIVE", "ACCUSATIVE", "ABLTIVE", "PREPOSITIONA", "VOATIVE", "GENITIVE1", "GENITIVE2", "ACCUSATIVE2", "PREPOSITIONA1", "PREPOSITIONA2", "ABBREVIATION", "NAME", "SURN", "PART", "GEOX", "ORGN", "TRAD", "SUBX", "SUPR", "QUAL", "APRO", "ANUM", "POSS", "V_EY", "V_OY", "CMP2", "V_EJ", "PERFECT", "IMPERFECT", "TRAN", "INTR", "IMPE", "IMPX", "MULT", "REFL", "PER1", "PER2", "PER3", "PRESENT", "PAST", "FUTURE", "INDICATIVE", "IMPERATIVE", "INCLUSIVE", "EXCLUSIVE", "ACTIVE", "PASSIVE", "INFR", "SLNG", "ARCH", "LITR", "ERRO", "DIST", "QUES", "DMNS", "PRNT", "V_BE", "V_EN", "V_IE", "V_BI", "GERUNDIMPERFECT", "PRDX", "COUN", "COLLECTIVENUMERAL", "GERUND_SHI", "AF_P", "INMX", "VPRE", "ANAPHORICPRONOUN", "INIT", "ADJX", "UNCLEARGENDER"};
        if(arrFile.length != arrProg.length) {
            throw new RuntimeException();
        }
        for(int i = 0; i < arrFile.length; i++) {
            stringStringMap.put(arrProg[i], arrFile[i]);
        }
        return stringStringMap;
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

        System.err.print("");
    }

}
