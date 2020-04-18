package ru.textanalysis.tawt.ms.internal;

public enum TypeForms {
    UNFAMILIAR(1),
    INITIAL(2),
    NUMBER(3),
    WORD(4);

    private final int id;

    TypeForms(int id) {
        this.id = id;
    }

    public int getId() {
        return id;
    }
}
