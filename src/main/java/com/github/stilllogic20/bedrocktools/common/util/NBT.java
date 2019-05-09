package com.github.stilllogic20.bedrocktools.common.util;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

@Retention(RetentionPolicy.RUNTIME)
public @interface NBT {

    String key();

    String parent() default "";

}
