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
}
