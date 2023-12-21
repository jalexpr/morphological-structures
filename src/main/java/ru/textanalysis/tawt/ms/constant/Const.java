package ru.textanalysis.tawt.ms.constant;

public interface Const {

    int LAVAL_COMPRESS = 3;
    int BUFFER_SIZE_FOR_INSERT = 10_000;
    String START_INSERT = "INSERT INTO 'Form' ('id','StringForm') VALUES ";
    String CONTINUED_INSERT = "(%d, '%s')";

    String TAB_SEPARATOR = "\t";
    String COMMA_SEPARATOR = ",";
    String SEMICOLON_SEPARATOR = ";";

    String TAB_AND_COMMA_REGEX = "[,;]";

    Integer DEFAULT_SLEEP_TIME = 1750;
    Integer DEFAULT_REQUEST_TIME_OUT = 22500;

    int MAX_PREFIX_LENGTH = 5;
    int MIN_WORD_ROOT_LENGTH = 1;
}
