package conversionDictionary;

import load.BDFormString;
import load.BDSqlite;

public class BDSqliteForConversion {

    private static int nextKeyInBdWordForm = 1;
    private static int nextKeyInBdInitialForm = BDFormString.START_ID_INITIAL_SAVE;
    private static final BDSqlite BD_INITIAL_FORM_STRING = BDFormString.BD_INITIAL_FORM_STRING;
    private static final BDSqlite BD_WORD_FORM_STRING = BDFormString.BD_WORD_FORM_STRING;

    static {
        createDd(BD_INITIAL_FORM_STRING);
        createDd(BD_WORD_FORM_STRING);
    }

    private static void createDd(BDSqlite bds) {
        addDataInBd(bds);
        createTables(bds);
        bds.execute("BEGIN TRANSACTION");
    }

    private static void addDataInBd(BDSqlite bds) {
    }

    private static void createTables(BDSqlite bds) {
        bds.execute("CREATE TABLE if not exists 'Form' ('id' INTEGER NOT NULL, 'StringForm' TEXT NOT NULL, PRIMARY KEY('id'))");
        bds.execute("CREATE TABLE if not exists 'Property' ('id' INTEGER NOT NULL, 'Attribute' TEXT NOT NULL, 'Value' TEXT NOT NULL, PRIMARY KEY('id'))");
    }

    public static void saveInBD(int key, String stringFrom, boolean isInitialForm) {
        if(!isInitialForm) {
            saveInBD(BD_WORD_FORM_STRING,  key, stringFrom);
        } else {
            throw new RuntimeException("Попытка запись начальную форму в не ту БД!");
        }
    }

    public static int saveInBD(String stringFrom, boolean isInitialForm) {
        int keyInBb;
        if(isInitialForm) {
            keyInBb = nextKeyInBdInitialForm++;
            saveInBD(BD_INITIAL_FORM_STRING, keyInBb, stringFrom);
        } else {
            keyInBb = nextKeyInBdWordForm++;
            saveInBD(BD_WORD_FORM_STRING,  keyInBb, stringFrom);
        }
        return keyInBb;
    }

    private static void saveInBD(BDSqlite outDBWordFormString, int key, String stringForm) {
        outDBWordFormString.execute(String.format("INSERT INTO 'Form' ('id','StringForm') VALUES (%d, '%s'); ", key, stringForm));
    }

    public static void closeBDs() {
        closeBD(BD_INITIAL_FORM_STRING);
        closeBD(BD_WORD_FORM_STRING);
    }

    private static void closeBD(BDSqlite bDSqlite) {
        bDSqlite.execute("END TRANSACTION");
        bDSqlite.closeDB();
    }

}
