package com.github.stilllogic20.bedrocktools.common.util;

public final class Magic {

    private static final ClassFactory CLASS_FACTORY = new ClassFactory();

    public static ClassFactory classFactory() {
        return CLASS_FACTORY;
    }

}
