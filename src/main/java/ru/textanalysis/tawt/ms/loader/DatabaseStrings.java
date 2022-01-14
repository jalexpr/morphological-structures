package ru.textanalysis.tawt.ms.loader;

import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import ru.textanalysis.tawt.ms.conversion.dictionary.FormForConversion;
import template.wrapper.classes.BDSqlite;

import java.io.File;
import java.net.URL;
import java.nio.file.Paths;
import java.util.Collection;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import static ru.textanalysis.tawt.ms.constant.Const.*;
import static ru.textanalysis.tawt.ms.model.Property.*;
import static template.wrapper.classes.Lzma2FileHelper.*;

@Slf4j
public class DatabaseStrings {

    private final File dbInitialFormStringFile;
    private final File dbDerivativeFormStringFile;
    private final BDSqlite dbInitialFormString;
    private final BDSqlite dbDerivativeFormString;

    protected DatabaseStrings(String path) {
        this.dbInitialFormStringFile = Paths.get(path, FOLDER, VERSION, NAME_BD_INITIAL_FORM).toFile();
        this.dbDerivativeFormStringFile = Paths.get(path, FOLDER, VERSION, NAME_BD_DERIVATIVE_FORM).toFile();
        this.dbInitialFormString = new BDSqlite(dbInitialFormStringFile.getAbsolutePath());
        this.dbDerivativeFormString = new BDSqlite(dbDerivativeFormStringFile.getAbsolutePath());
    }

    public void decompressDd() {
        this.decompressDd(dbInitialFormStringFile);
        this.decompressDd(dbDerivativeFormStringFile);
    }

    public String getLiteralById(int id) {
        if (id < START_ID_DERIVATIVE_FORM) {
            return getLiteralById(id, true);
        } else {
            return getLiteralById(id, false);
        }
    }

    private String getLiteralById(int idKey, boolean isInitialForm) {
        String str = null;
        try {
            String executeString = String.format("SELECT * FROM 'Form' where id = %d", idKey);
            if (isInitialForm) {
                str = dbInitialFormString.executeQuery(executeString, "StringForm");
            } else {
                str = dbDerivativeFormString.executeQuery(executeString, "StringForm");
            }
            if (StringUtils.isBlank(str)) {
                if (isInitialForm) {
                    log.warn("Ошибка запроса, проверьте актуальность версии БД '{}'", NAME_BD_INITIAL_FORM);
                } else {
                    log.warn("Ошибка запроса, проверьте актуальность версии БД '{}'", NAME_BD_DERIVATIVE_FORM);
                }
            }
        } catch (NullPointerException ex) {
            if (isInitialForm) {
                log.warn("БД '{}' не найдена", NAME_BD_INITIAL_FORM);
                Logger.getLogger(DatabaseStrings.class.getName()).log(Level.SEVERE, String.format("БД \"%s\" не найдена", NAME_BD_INITIAL_FORM), ex);
            } else {
                log.warn("БД '{}' не найдена", NAME_BD_DERIVATIVE_FORM);
                Logger.getLogger(DatabaseStrings.class.getName()).log(Level.SEVERE, String.format("БД \"%s\" не найдена", NAME_BD_DERIVATIVE_FORM), ex);
            }
        }
        return str;
    }

    public void decompressDd(File file) {
        boolean needDecompress;
        if (file.exists()) {
            //todo проверка, что версия старая
            needDecompress = false;
        } else {
            needDecompress = true;
        }
        if (needDecompress) {
            System.out.println("Decompress DB. Please wait a few minutes");
            File dir = file.getParentFile();
            if (!dir.mkdirs()) {
                log.debug("Not create dir '{}' for decompress", dir.getAbsolutePath());
            }
            String nameExp = file.getName() + ARCHIVE_EXPANSION;
            String path = FOLDER + nameExp;
            URL pathZip = getClass().getClassLoader().getResource(path);
            if (pathZip != null) {
                deCompressionFile(path, file);
            } else {
                log.info("Not create file '{}' for decompress", path);
            }
        }
    }

    public void recreate(List<List<FormForConversion>> forms) {
        this.dbInitialFormStringFile.getParentFile().mkdirs();
        recreate(dbInitialFormString);
        recreate(dbDerivativeFormString);

        StringBuffer multipleInsertInitial = new StringBuffer(START_INSERT);
        StringBuffer multipleInsertDerivative = new StringBuffer(START_INSERT);
        forms.stream()
                .flatMap(Collection::stream)
                .filter(form -> !form.isFirstKey())
                .forEach(form -> {
                    int id = form.getKey();
                    String name = form.getStringName();
                    String query = String.format(CONTINUED_INSERT, id, name);
                    if (form.isInitialForm()) {
                        insert(multipleInsertInitial, query, dbInitialFormString);
                    } else {
                        insert(multipleInsertDerivative, query, dbDerivativeFormString);
                    }
                });

        multipleInsertInitial.setLength(multipleInsertInitial.length() - 1);
        multipleInsertDerivative.setLength(multipleInsertDerivative.length() - 1);
        dbInitialFormString.execute(multipleInsertInitial + SEMICOLON_SEPARATOR);
        dbDerivativeFormString.execute(multipleInsertDerivative + SEMICOLON_SEPARATOR);
    }

    private void insert(StringBuffer multipleInsert, String query, BDSqlite dbInitialFormString) {
        multipleInsert.append(query);
        if (multipleInsert.length() > BUFFER_SIZE_FOR_INSERT) {
            dbInitialFormString.execute(multipleInsert + SEMICOLON_SEPARATOR);
            multipleInsert.setLength(START_INSERT.length());
        } else {
            multipleInsert.append(COMMA_SEPARATOR);
        }
    }

    private void recreate(BDSqlite db) {
//        db.execute("DROP TABLE Form;");
        db.execute("CREATE TABLE if not exists 'Form' ('id' INTEGER NOT NULL, 'StringForm' TEXT NOT NULL, PRIMARY KEY('id'));");
//        db.execute("DROP TABLE Property;");
        db.execute("CREATE TABLE if not exists 'Property' ('id' INTEGER NOT NULL, 'Attribute' TEXT NOT NULL, 'Value' TEXT NOT NULL, PRIMARY KEY('id'));");
    }

    public void compression() {
        compressionFile(dbInitialFormStringFile.getAbsolutePath(), LAVAL_COMPRESS);
        compressionFile(dbDerivativeFormStringFile.getAbsolutePath(), LAVAL_COMPRESS);
    }
}
