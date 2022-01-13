package ru.textanalysis.tawt.ms.model.jmorfsdk;

import ru.textanalysis.tawt.ms.model.IEnumWithLongValue;

public enum TypeForms implements IEnumWithLongValue<Integer> {
    UNFAMILIAR(1),
    INITIAL(2),
    NUMBER(3),
    DERIVATIVE(4);

    private final Integer id;

    TypeForms(Integer id) {
        this.id = id;
    }

    public Integer getId() {
        return id;
    }
}
