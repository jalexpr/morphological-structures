package load;

import java.sql.SQLException;
import java.util.logging.Level;
import java.util.logging.Logger;
import morphologicalstructures.Property;

public class BDInitialFormString {

    private final static BDSqlite BDINITIALFORMSTRING = new BDSqlite(Property.PATH_BD_INITIAL_FORM);
    private final static BDSqlite BDWORDFORMSTRING = new BDSqlite(Property.PATH_BD_WORD_FORM);

    public static String getStringById(int id, boolean isInitialForm) {

        try {
            if (isInitialForm) {
                return (String) BDINITIALFORMSTRING.executeQuery(String.format("SELECT * FROM  'Form' where id = %d", id)).getObject("StringForm");
            } else {
                return (String) BDWORDFORMSTRING.executeQuery(String.format("SELECT * FROM  'Form' where id = %d", id)).getObject("StringForm");
            }
        } catch (NullPointerException ex) {
            if (isInitialForm) {
                Logger.getLogger(BDInitialFormString.class.getName()).log(Level.SEVERE, String.format("БД \"%s\" не найдена", Property.PATH_BD_INITIAL_FORM), ex);
            } else {
                Logger.getLogger(BDInitialFormString.class.getName()).log(Level.SEVERE, String.format("БД \"%s\" не найдена", Property.PATH_BD_WORD_FORM), ex);
            }
        } catch (SQLException ex) {
            if (isInitialForm) {
                Logger.getLogger(BDInitialFormString.class.getName()).log(Level.SEVERE, String.format("Ошибкуа запроса, проверте актуальность версии БД \"%s\"", Property.PATH_BD_INITIAL_FORM), ex);
            } else {
                Logger.getLogger(BDInitialFormString.class.getName()).log(Level.SEVERE, String.format("Ошибкуа запроса, проверте актуальность версии БД \"%s\"", Property.PATH_BD_WORD_FORM), ex);
            }
        }
        return null;
    }
    
    public static void printSumme(boolean isInitialForm) {

        try {
            if (isInitialForm) {
                System.out.println(BDINITIALFORMSTRING.executeQuery(String.format("SELECT Count(*) FROM  'Form'")).getObject(1));
            } else {
                System.out.println(BDWORDFORMSTRING.executeQuery(String.format("SELECT Count(*) FROM  'Form'")));
            }
        } catch (NullPointerException ex) {
            if (isInitialForm) {
                Logger.getLogger(BDInitialFormString.class.getName()).log(Level.SEVERE, String.format("БД \"%s\" не найдена", Property.PATH_BD_INITIAL_FORM), ex);
            } else {
                Logger.getLogger(BDInitialFormString.class.getName()).log(Level.SEVERE, String.format("БД \"%s\" не найдена", Property.PATH_BD_WORD_FORM), ex);
            }
        } catch (SQLException ex) {
            if (isInitialForm) {
                Logger.getLogger(BDInitialFormString.class.getName()).log(Level.SEVERE, String.format("Ошибкуа запроса, проверте актуальность версии БД \"%s\"", Property.PATH_BD_INITIAL_FORM), ex);
            } else {
                Logger.getLogger(BDInitialFormString.class.getName()).log(Level.SEVERE, String.format("Ошибкуа запроса, проверте актуальность версии БД \"%s\"", Property.PATH_BD_WORD_FORM), ex);
            }
        }
    }
}
