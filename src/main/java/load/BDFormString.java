package load;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.logging.Level;
import java.util.logging.Logger;
import morphologicalstructures.Property;

public class BDFormString {

    public final static BDSqlite BD_INITIAL_FORM_STRING = new BDSqlite(Property.PATH_BD_INITIAL_FORM);
    public final static BDSqlite BD_WORD_FORM_STRING = new BDSqlite(Property.PATH_BD_WORD_FORM);
    public final static int START_ID_INITIAL_SAVE = Property.START_ID_INITIAL_SAVE;

    public static String getStringById(int idKey) {
        if(idKey < START_ID_INITIAL_SAVE) {
            return getStringById(idKey, false);
        } else {
            return getStringById(idKey, true);
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
}
