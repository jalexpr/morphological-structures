package ru.textanalysis.tawt.ms.loader;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public class DatabaseFactory {

    private static DatabaseStrings databaseStrings;
    private static DatabaseLemmas databaseLemmas;
    private static final String path = System.getProperty("java.io.tmpdir");

    private DatabaseFactory() {
    }

    public synchronized static DatabaseStrings getInstanceDatabaseStrings() {
        if (databaseStrings == null) {
            databaseStrings = new DatabaseStrings(path);
        }
        return databaseStrings;
    }

    public synchronized static DatabaseLemmas getInstanceDatabaseLemmas() {
        if (databaseLemmas == null) {
            databaseLemmas = new DatabaseLemmas(path);
        }
        return databaseLemmas;
    }
}
