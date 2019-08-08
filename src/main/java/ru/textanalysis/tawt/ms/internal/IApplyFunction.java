package ru.textanalysis.tawt.ms.internal;

import java.util.function.Function;

public interface IApplyFunction<T> {
    void applyFunction(Function<T, Boolean> function);
}
