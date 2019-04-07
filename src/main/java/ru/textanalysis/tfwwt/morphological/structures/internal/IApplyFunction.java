package ru.textanalysis.tfwwt.morphological.structures.internal;

import java.util.function.Function;

public interface IApplyFunction<T> {
    void applyFunction(Function<T, Boolean> function);
}
