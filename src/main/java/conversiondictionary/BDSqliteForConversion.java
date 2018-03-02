package conversionDictionary;

import load.BDFormString;
import load.BDSqlite;
import conversionDictionary.ConversionDictionary.Form;

public class BDSqliteForConversion {

    private final BDSqlite BD_INITIAL_FORM_STRING = BDFormString.BD_INITIAL_FORM_STRING;
    private final BDSqlite BD_WORD_FORM_STRING = BDFormString.BD_WORD_FORM_STRING;

    public BDSqliteForConversion() {
        createDd(BD_INITIAL_FORM_STRING);
        createDd(BD_WORD_FORM_STRING);
    }

    private void createDd(BDSqlite bds) {
        createTables(bds);
        bds.execute("BEGIN TRANSACTION");
    }

    private void createTables(BDSqlite bds) {
        bds.execute("DROP TABLE Form;");
        bds.execute("CREATE TABLE if not exists 'Form' ('id' INTEGER NOT NULL, 'StringForm' TEXT NOT NULL, PRIMARY KEY('id'));");
        bds.execute("DROP TABLE Property;");
        bds.execute("CREATE TABLE if not exists 'Property' ('id' INTEGER NOT NULL, 'Attribute' TEXT NOT NULL, 'Value' TEXT NOT NULL, PRIMARY KEY('id'));");
    }

    public void saveInBD(Form form) {
        if(form.isInitialForm()) {
            saveInBD(BD_INITIAL_FORM_STRING, form);
        } else {
            saveInBD(BD_WORD_FORM_STRING, form);
        }
    }

    private void saveInBD(BDSqlite outDBWordFormString, Form form) {
        outDBWordFormString.execute(String.format("INSERT INTO 'Form' ('id','StringForm') VALUES (%d, '%s');", form.getKey(), form.getStringName()));
    }

    public void closeBDs() {
        closeBD(BD_INITIAL_FORM_STRING);
        closeBD(BD_WORD_FORM_STRING);
    }

    private void closeBD(BDSqlite bDSqlite) {
        bDSqlite.execute("END TRANSACTION");
        bDSqlite.closeDB();
    }

}
