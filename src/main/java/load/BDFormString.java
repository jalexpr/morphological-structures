package load;

import java.io.File;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.logging.Level;
import java.util.logging.Logger;
import morphologicalstructures.Property;

import static load.FileHelper.deleteFile;
import static load.Lzma2FileHelper.ARCHIVE_EXPANSION;
import static load.Lzma2FileHelper.compressionFile;
import static load.Lzma2FileHelper.deCompressionFile;

public class BDFormString {

    public final static String PATH_BD_INITIAL_FORM = Property.PATH_BD_INITIAL_FORM;
    public final static String PATH_BD_WORD_FORM = Property.PATH_BD_WORD_FORM;
    public final static BDSqlite BD_INITIAL_FORM_STRING;
    public final static BDSqlite BD_WORD_FORM_STRING;
    public final static int START_ID_WORD_FORM = Property.START_ID_WORD_FORM;

    static {
        get();
        BD_INITIAL_FORM_STRING = new BDSqlite(PATH_BD_INITIAL_FORM);
        BD_WORD_FORM_STRING = new BDSqlite(PATH_BD_WORD_FORM);
    }

    public static String getStringById(int idKey) {
        if(idKey < START_ID_WORD_FORM) {
            return getStringById(idKey, true);
        } else {
            return getStringById(idKey, false);
        }
    }

    public static String getStringById(int idKey, boolean isInitialForm) {
        try {
            ResultSet resultSet;
            String executeString = String.format("SELECT * FROM  'Form' where id = %d", idKey);
            if (isInitialForm) {
                resultSet = BD_INITIAL_FORM_STRING.executeQuery(executeString);
            } else {
                resultSet = BD_WORD_FORM_STRING.executeQuery(executeString);
            }
            return (String) resultSet.getObject("StringForm");
        } catch (NullPointerException ex) {
            if (isInitialForm) {
                Logger.getLogger(BDFormString.class.getName()).log(Level.SEVERE, String.format("БД \"%s\" не найдена", Property.PATH_BD_INITIAL_FORM), ex);
            } else {
                Logger.getLogger(BDFormString.class.getName()).log(Level.SEVERE, String.format("БД \"%s\" не найдена", Property.PATH_BD_WORD_FORM), ex);
            }
        } catch (SQLException ex) {
            if (isInitialForm) {
                Logger.getLogger(BDFormString.class.getName()).log(Level.SEVERE, String.format("Ошибкуа запроса, проверте актуальность версии БД \"%s\"", Property.PATH_BD_INITIAL_FORM), ex);
            } else {
                Logger.getLogger(BDFormString.class.getName()).log(Level.SEVERE, String.format("Ошибкуа запроса, проверте актуальность версии БД \"%s\"", Property.PATH_BD_WORD_FORM), ex);
            }
        }
        return null;
    }

    public static void printSumme(boolean isInitialForm) {

        try {
            String executeString = String.format("SELECT Count(*) FROM  'Form'");
            if (isInitialForm) {
                System.out.println(BD_INITIAL_FORM_STRING.executeQuery(executeString).getObject(1));
            } else {
                System.out.println(BD_WORD_FORM_STRING.executeQuery(executeString));
            }
        } catch (NullPointerException ex) {
            if (isInitialForm) {
                Logger.getLogger(BDFormString.class.getName()).log(Level.SEVERE, String.format("БД \"%s\" не найдена", Property.PATH_BD_INITIAL_FORM), ex);
            } else {
                Logger.getLogger(BDFormString.class.getName()).log(Level.SEVERE, String.format("БД \"%s\" не найдена", Property.PATH_BD_WORD_FORM), ex);
            }
        } catch (SQLException ex) {
            if (isInitialForm) {
                Logger.getLogger(BDFormString.class.getName()).log(Level.SEVERE, String.format("Ошибкуа запроса, проверте актуальность версии БД \"%s\"", Property.PATH_BD_INITIAL_FORM), ex);
            } else {
                Logger.getLogger(BDFormString.class.getName()).log(Level.SEVERE, String.format("Ошибкуа запроса, проверте актуальность версии БД \"%s\"", Property.PATH_BD_WORD_FORM), ex);
            }
        }
    }

    public static void compressionBd() {
        compressionBd(BD_INITIAL_FORM_STRING, PATH_BD_INITIAL_FORM);
        compressionBd(BD_WORD_FORM_STRING, PATH_BD_WORD_FORM);
    }

    public static void compressionBd(BDSqlite bds, String pathFile) {
        bds.closeDB();
        compressionFile(pathFile);
        deleteFile(pathFile);
    }

    public static void get() {
        File file = new File(PATH_BD_INITIAL_FORM);
        file.exists();
        deCompressionFile(PATH_BD_INITIAL_FORM + ARCHIVE_EXPANSION, Integer.MAX_VALUE / 2, PATH_BD_INITIAL_FORM);
    }

    public static void main(String[] args) {

    }

}
