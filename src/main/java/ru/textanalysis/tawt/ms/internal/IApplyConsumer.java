package ru.textanalysis.tawt.ms.internal;

import java.util.function.Consumer;

public interface IApplyConsumer<T> {
    void applyConsumer(Consumer<T> consumer);
}
