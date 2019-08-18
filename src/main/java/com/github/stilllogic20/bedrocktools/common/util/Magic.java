package com.github.stilllogic20.bedrocktools.common.util;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public final class Magic {

    private static final Logger LOGGER = LogManager.getLogger();
    private static final ClassFactory CLASS_FACTORY = new ClassFactory();

    public static ClassFactory classFactory() {
        return CLASS_FACTORY;
    }
    
}
