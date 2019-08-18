package com.github.stilllogic20.bedrocktools.common.compat;

import com.github.stilllogic20.bedrocktools.common.util.Magic;
import net.minecraft.item.ItemStack;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.lang.reflect.Method;

public final class XUCompat {

    private static final Logger LOGGER = LogManager.getLogger();

    public static final XUCompat.ItemFakeCopy ItemFakeCopy;
    public static final Class<?> FAKE_COPY_CLASS;

    static {
        Class<?> classItemFakeCopy = null;
        try {
            classItemFakeCopy = Class.forName("com.rwtema.extrautils2.items.ItemFakeCopy");
        } catch (ClassNotFoundException exception) {
            LOGGER.warn("Unable to resolve class 'com.rwtema.extrautils2.items.ItemFakeCopy'", exception);
        }
        FAKE_COPY_CLASS = classItemFakeCopy;

        XUCompat.ItemFakeCopy itemFakeCopy = null;
        if (classItemFakeCopy != null) {
            try {
                Method getOriginalStack = classItemFakeCopy.getDeclaredMethod("getOriginalStack", int.class);
                itemFakeCopy = Magic.classFactory().bridge(XUCompat.ItemFakeCopy.class, getOriginalStack);
            } catch (NoSuchMethodException exception) {
                LOGGER.warn("Unable to resolve method 'getOriginalStack(I)Lnet/minecraft/item/ItemStack;'", exception);
            }
        }
        ItemFakeCopy = itemFakeCopy == null ? __ -> null : itemFakeCopy;
    }

    @FunctionalInterface
    public interface ItemFakeCopy {
        ItemStack getOriginalStack(int itemDamage);
    }

}
