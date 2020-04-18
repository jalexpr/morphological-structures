package ru.textanalysis.tawt.ms.internal;

import java.io.Serializable;
import java.util.Objects;

public interface IEnumWithLongValue<R> extends Serializable {
    R getId();

    default boolean equalsById(R id) {
        return id != null && getId().equals(id);
    }

    /**
     * Проверяет что среди данного набора Enum, присутствует элемент с данным id
     * id может быть null
     */
    static <T extends Enum<T> & IEnumWithLongValue> boolean contains(Object id, T... enumSet) {
        for (T e : enumSet) {
            if (Objects.equals(id, e.getId()))
                return true;
        }
        return false;
    }

    static <T extends Enum<T> & IEnumWithLongValue> boolean contains(T e, T... enumSet) {
        for (T enumEl : enumSet) {
            if (enumEl.equals(e))
                return true;
        }
        return false;
    }

    /**
     * Проверяет что среди всех Enum данного класса, присутствует элемент с данным id
     * id может быть null
     */
    static <T extends Enum<T> & IEnumWithLongValue<R>, R> boolean contains(Class<T> enumClass, R id) {
        for (T e : enumClass.getEnumConstants()) {
            if (Objects.equals(id, e.getId()))
                return true;
        }
        return false;
    }

    /**
     * Возвращает Enum по id, если в заданном классе такой определен
     * id может быть null
     *
     * @return Enum если нашел, иначе <tt>null</tt>
     */
    static <T extends Enum<T> & IEnumWithLongValue<R>, R> T getEnumById(Class<T> enumClass, R id) {
        for (T e : enumClass.getEnumConstants()) {
            if (Objects.equals(id, e.getId()))
                return e;
        }
        return null;
    }

    static <R> Object getIdOrNull(IEnumWithLongValue<R> enumVal) {
        return enumVal == null ? null : enumVal.getId();
    }
}