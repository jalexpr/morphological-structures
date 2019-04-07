package ru.textanalysis.tfwwt.morphological.structures.internal;

import java.util.function.Consumer;

public interface IApplyConsumer<T> {
    void applyConsumer(Consumer<T> consumer);
}
