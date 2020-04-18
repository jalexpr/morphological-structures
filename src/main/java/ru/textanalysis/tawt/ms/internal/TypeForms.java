package ru.textanalysis.tawt.ms.internal;

public enum TypeForms implements IEnumWithLongValue<Integer> {
    UNFAMILIAR(1),
    INITIAL(2),
    NUMBER(3),
    WORD(4);

    private final Integer id;

    TypeForms(Integer id) {
        this.id = id;
    }

    public Integer getId() {
        return id;
    }
}
