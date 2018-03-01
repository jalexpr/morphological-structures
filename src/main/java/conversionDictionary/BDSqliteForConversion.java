package conversionDictionary;

import load.BDSqlite;

public class BDSqliteForConversion {

    private static int nextKeyInBdInitialForm;
    private static int nextKeyInBdWordForm;
    private static BDSqlite bdInitialForm = new BDSqlite(PropertyForConversion.PATH_BD_INITIAL_FORM);
    private static BDSqlite bdWordForm = new BDSqlite(PropertyForConversion.PATH_BD_WORD_FORM);

    static {
        bdInitialForm.execute("BEGIN TRANSACTION");
        bdWordForm.execute("BEGIN TRANSACTION");
    }



    public static int saveInBD(String stringFrom, boolean isInitialForm) {
        int key_in_db;
        if(isInitialForm) {
            key_in_db = nextKeyInBdInitialForm++;
        } else {
            key_in_db = nextKeyInBdWordForm++;
        }
        outBD.execute("CREATE TABLE if not exists 'Form' ('id' INTEGER NOT NULL, 'StringForm' TEXT NOT NULL, PRIMARY KEY('id'))");
        saveStringAndIdInBD(stringFormAndId, outBD);
        outBD.closeDB();
        return key_in_db;
    }

    private void saveInBD(HashMap<Integer, IdAndString> stringFormAndId, BDSqlite outDBWordFormString) {

        for (Object obj : stringFormAndId.values()) {
            IdAndString idAndString = (IdAndString) obj;
            outDBWordFormString.execute(String.format("INSERT INTO 'Form' ('id','StringForm') VALUES (%d, '%s'); ", idAndString.myId, idAndString.myString));
        }
        outDBWordFormString.execute("END TRANSACTION");
    }

    public static void closeDB() {
        bdInitialForm.closeDB();
        bdWordForm.closeDB();
    }

}
