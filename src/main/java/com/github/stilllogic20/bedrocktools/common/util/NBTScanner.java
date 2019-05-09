package com.github.stilllogic20.bedrocktools.common.util;

import javax.annotation.Nonnull;
import java.lang.reflect.Field;
import java.util.Arrays;
import java.util.Objects;

public class NBTScanner {

    @Nonnull
    public static NBTScanner scan(@Nonnull Object instance) {
        Objects.requireNonNull(instance);
        return scan(instance.getClass());
    }

    @Nonnull
    public static NBTScanner scan(@Nonnull Class<?> type) {
        Objects.requireNonNull(type);
        Class<?> superClass = type.getSuperclass();
        NBTScanner parent = null;
        if (superClass != null && superClass != Object.class) {
            parent = scan(superClass);
        }
        Field[] fields = type.getDeclaredFields();
        for (Field field : fields) {
            NBT nbt = field.getAnnotation(NBT.class);
            if (nbt == null)
                continue;
            final String key = nbt.parent() + nbt.key();
            final Class<?> fieldType = field.getType();
            Reflection.Getter<?> getter = Reflection.getterOf(field);
            Reflection.Setter<?> setter = Reflection.setterOf(field);
        }
        throw new UnsupportedOperationException("Not implemented");
    }

}
