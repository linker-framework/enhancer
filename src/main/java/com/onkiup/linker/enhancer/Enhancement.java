package com.onkiup.linker.enhancer;

import java.util.Arrays;

import javassist.CtClass;
import javassist.CtMethod;

/**
 * @author : chedim (chedim@chedim-Surface-Pro-3)
 * @file : Enhancement
 * @created : Wednesday Mar 18, 2020 16:52:47 EDT
 */

/**
 * An interface that marks enhancement classes Static methods in enhancement
 * classes can be used to generate overloaded version of T
 */
public interface Enhancement<T> {
  default void apply(CtClass target) {
    try {
      CtClass source = target.getSuperclass();
      Arrays.stream(source.getMethods()).filter(this::applicable).forEach(this::apply);
    } catch (Exception e) {
      throw new RuntimeException(e);
    }
  }

  void apply(CtMethod target);

  boolean applicable(CtMethod test);
}
