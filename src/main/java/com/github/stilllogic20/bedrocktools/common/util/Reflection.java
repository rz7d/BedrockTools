package com.github.stilllogic20.bedrocktools.common.util;

import org.apache.commons.logging.LogFactory;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.lang.reflect.Field;
import java.text.MessageFormat;
import java.util.Arrays;
import java.util.Objects;

public class Reflection {

    @Nonnull
    public static <T> Getter<T> getterOf(Field field) {
        String fieldName = field.getName();
        Class<?> container = field.getDeclaringClass();
        @SuppressWarnings("unchecked")
        Getter<T> getter = (Getter<T>) Arrays.stream(container.getDeclaredMethods())
            .filter(method -> method.getParameterCount() == 0)
            .filter(method -> {
                final String name = method.getName();
                if (name == null || name.isEmpty())
                    return false;
                if (Objects.equals(name, fieldName))
                    return true;
                if (fieldName.length() > 1) {
                    char upperFirst = Character.toUpperCase(fieldName.charAt(0));
                    String getterName = upperFirst + fieldName.substring(1);
                    return Objects.equals(getterName, fieldName);
                }
                return false;
            })
            .limit(1)
            .map(method -> getterOfInternal(instance -> method.invoke(instance)))
            .findAny()
            .orElseGet(() -> getterOfInternal(field::get));
        return getter;
    }

    @Nonnull
    public static <T> Setter<T> setterOf(Field field) {
        String fieldName = field.getName();
        Class<?> container = field.getDeclaringClass();
        @SuppressWarnings("unchecked")
        Setter<T> setter = (Setter<T>) Arrays.stream(container.getDeclaredMethods())
            .filter(method -> method.getParameterCount() == 1)
            .filter(method -> {
                final String name = method.getName();
                if (name == null || name.isEmpty())
                    return false;
                if (Objects.equals(name, fieldName))
                    return true;
                if (fieldName.length() > 1) {
                    char upperFirst = Character.toUpperCase(fieldName.charAt(0));
                    String getterName = upperFirst + fieldName.substring(1);
                    return Objects.equals(getterName, fieldName);
                }
                return false;
            })
            .limit(1)
            .map(method -> setterOfInternal((instance, value) -> {
                Object result = method.invoke(instance, value);
                if (result != null) {
                    LogFactory.getLog("ReflectionHelper").info(MessageFormat.format(
                        "Ignoring returned value from setter '{0}': {1}",
                        method.toString(), result));
                }
            }))
            .findAny()
            .orElseGet(() -> setterOfInternal(field::set));
        return setter;
    }

    private static <T> Getter<T> getterOfInternal(ThrowableFunction<Object, Object> consumer) {
        return instance -> {
            try {
                @SuppressWarnings("unchecked") final T result = (T) consumer.apply(instance);
                return result;
            } catch (RuntimeException exception) {
                throw exception;
            } catch (Exception exception) {
                throw new RuntimeException(exception);
            }
        };
    }

    private static <T> Setter<T> setterOfInternal(ThrowableBiConsumer<Object, T> consumer) {
        return (instance, value) -> {
            try {
                consumer.accept(instance, value);
            } catch (RuntimeException exception) {
                throw exception;
            } catch (Exception exception) {
                throw new RuntimeException(exception);
            }
        };
    }

    @FunctionalInterface
    public interface ThrowableFunction<T, R> {
        R apply(T t) throws Exception;
    }

    @FunctionalInterface
    public interface ThrowableBiConsumer<T, U> {
        void accept(T t1, U t2) throws Exception;
    }

    public interface Getter<T> {
        @Nullable
        T get(@Nullable Object instance);
    }

    public interface Setter<T> {
        void set(@Nullable Object instance, @Nullable T value);
    }

}
