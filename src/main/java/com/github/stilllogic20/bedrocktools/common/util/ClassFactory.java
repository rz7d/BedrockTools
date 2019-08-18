package com.github.stilllogic20.bedrocktools.common.util;

import org.objectweb.asm.ClassWriter;
import org.objectweb.asm.MethodVisitor;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.Type;

import javax.annotation.Nonnull;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.Map;
import java.util.Objects;
import java.util.WeakHashMap;
import java.util.concurrent.atomic.AtomicLong;

import static org.objectweb.asm.Opcodes.*;

public final class ClassFactory {

    private static InternalError error(Class<?> type) {
        Objects.requireNonNull(type);
        return new InternalError(type + " is not known or not available primitive type! but isPrimitive is " + type.isPrimitive());
    }

    private static int loadOp(Class<?> type) {
        if (type.isPrimitive()) {
            if (type == boolean.class) return ILOAD;
            if (type == byte.class) return ILOAD;
            if (type == char.class) return ILOAD;
            if (type == short.class) return ILOAD;
            if (type == int.class) return ILOAD;
            if (type == long.class) return LLOAD;
            if (type == float.class) return FLOAD;
            if (type == double.class) return DLOAD;
            throw error(type);
        }
        return Opcodes.ALOAD;
    }

    private static int returnOp(Class<?> type) {
        if (type.isPrimitive()) {
            if (type == boolean.class) return IRETURN;
            if (type == byte.class) return IRETURN;
            if (type == char.class) return IRETURN;
            if (type == short.class) return IRETURN;
            if (type == int.class) return IRETURN;
            if (type == long.class) return LRETURN;
            if (type == float.class) return FRETURN;
            if (type == double.class) return DRETURN;
            throw error(type);
        }
        return ARETURN;
    }


    private final AtomicLong id = new AtomicLong();
    private final Map<Method, Object> cache = new WeakHashMap<>();
    private final FactoryClassLoader loader = new FactoryClassLoader();

    public <T> T bridge(@Nonnull Class<T> itf, @Nonnull Method callback) {
        caching:
        if (cache.containsKey(callback)) {
            @SuppressWarnings("unchecked")
            T caller = (T) cache.get(callback);
            if (caller == null)
                break caching;
            return caller;
        }

        final int modifiers = callback.getModifiers();
        if (!Modifier.isStatic(modifiers)) {
            throw new IllegalArgumentException("caller method " + callback + " must be static method.");
        }

        if (!itf.isInterface() || itf.getMethods().length != 1) {
            throw new IllegalArgumentException("itf must be SAM interface.");
        }
        Method sam = itf.getMethods()[0];

        String desc = Type.getMethodDescriptor(Type.getReturnType(callback), Type.getArgumentTypes(callback));

        String name = makeIdentifier(callback, desc);
        String clazz = name.replace('.', '/');


        ClassWriter cw = new ClassWriter(ClassWriter.COMPUTE_FRAMES | ClassWriter.COMPUTE_MAXS);
        cw.visit(V1_8, ACC_PUBLIC | ACC_FINAL, clazz,
            null, "java/lang/Object",
            new String[] { Type.getInternalName(itf) });

        cw.visitSource(null, null);

        MethodVisitor constructor = cw.visitMethod(ACC_PUBLIC,
            "<init>", "()V", null, null);
        constructor.visitCode();
        constructor.visitVarInsn(ALOAD, 0);
        constructor.visitMethodInsn(INVOKESPECIAL, "java/lang/Object", "<init>", "()V", false);
        constructor.visitInsn(RETURN);
        constructor.visitMaxs(0, 0);
        constructor.visitEnd();

        MethodVisitor caller = cw.visitMethod(ACC_PUBLIC,
            sam.getName(), desc, null, null);
        caller.visitCode();
        caller.visitVarInsn(ALOAD, 0);

        Class<?>[] params = callback.getParameterTypes();
        for (int i = 0; i < callback.getParameterCount(); ++i) {
            caller.visitVarInsn(loadOp(params[i]), i + 1);
        }
        caller.visitMethodInsn(INVOKESTATIC,
            Type.getInternalName(callback.getDeclaringClass()),
            callback.getName(), desc, false);
        caller.visitInsn(returnOp(callback.getReturnType()));
        caller.visitMaxs(0, 0);
        caller.visitEnd();
        cw.visitEnd();

        Class<?> defined = loader.define(name, cw.toByteArray());
        try {
            @SuppressWarnings("unchecked")
            T t = (T) defined.getConstructor().newInstance();
            cache.put(callback, t);
            return t;
        } catch (InstantiationException | IllegalAccessException | InvocationTargetException | NoSuchMethodException exception) {
            throw new InternalError(exception);
        }
    }

    private String makeIdentifier(Method callback, String desc) {
        return String.format("%s_Accessor_%d_%s_%s",
            getClass().getName(),
            id.getAndIncrement(),
            callback.getDeclaringClass().getName().replace('.', '_'),
            callback.getName()
        );
    }

    private static final class FactoryClassLoader extends ClassLoader {

        FactoryClassLoader() {
            super(FactoryClassLoader.class.getClassLoader());
        }

        public Class<?> define(String name, byte[] bytecode) {
            return defineClass(name, bytecode, 0, bytecode.length);
        }

    }

}
