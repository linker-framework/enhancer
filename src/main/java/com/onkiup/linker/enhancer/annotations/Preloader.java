package com.onkiup.linker.enhancer.annotations;

import java.lang.reflect.Field;
import java.lang.reflect.Method;

import com.onkiup.linker.enhancer.Enhancement;
import com.onkiup.linker.util.FieldUtils;

import javassist.ClassMap;
import javassist.ClassPool;
import javassist.CtClass;
import javassist.CtField;
import javassist.CtMethod;
import javassist.CtNewMethod;

/**
 * Annotates preloading enhancements
 * 
 * @author : chedim (chedim@chedim-Surface-Pro-3)
 * @file : PreLoader
 * @created : Wednesday Mar 18, 2020 16:17:06 EDT
 * @param T the type to apply this preloader on
 * @param V the type of preloaded valu
 */
public interface Preloader<T, V> extends Enhancement<T> {
  V preload(String invokee);

  @Override
  default void apply(CtMethod target) {
    try {
      String flagName = "_preloadFlag" + System.identityHashCode(target);
      String preloaderMethodName = "_preloader" + System.identityHashCode(getClass());
      CtClass targetClass = target.getDeclaringClass();

      CtClass me = ClassPool.getDefault().getCtClass(getClass().getCanonicalName());
      CtMethod preloader = me.getDeclaredMethod("preload",
          new CtClass[] { ClassPool.getDefault().getCtClass(Class.class.getCanonicalName()),
              ClassPool.getDefault().getCtClass(Method.class.getCanonicalName()),
              ClassPool.getDefault().getCtClass(Object[].class.getCanonicalName()) });

      CtMethod preloaderCopy = CtNewMethod.copy(preloader, preloaderMethodName, targetClass, new ClassMap());
      targetClass.addField(CtField.make("private boolean " + flagName, targetClass));
      targetClass.addMethod(preloaderCopy);

      target.insertBefore(
          "if (!" + flagName + ") {\n" +
          " Field __preloadTarget = FieldUtils.deduct(getClass(), getClass().getSuperClass(), \"" + target.getName() + "\")" +
          "   .orElseThrow(() -> new RuntimeException(\"Failed to preload\"));" +
          " __preloadTarget.set(this, " + preloaderMethodName + "(\"" + target.getName() + "\"));" +
          " " + flagName + " = true;" +
          "}"
      );
    } catch (Exception e) {
      throw new RuntimeException(e);
    }
  }
}
