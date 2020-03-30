package com.onkiup.linker.enhancer;

import java.util.Collection;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.Map;
import java.util.WeakHashMap;
import java.util.concurrent.atomic.AtomicInteger;

import com.onkiup.linker.util.TypeUtils;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javassist.ClassPool;
import javassist.CtClass;

/**
 * @author : chedim (chedim@chedim-Surface-Pro-3)
 * @file : Enhancer
 * @created : Wednesday Mar 18, 2020 15:26:22 EDT
 */

public class Enhancer {

  private static final ClassPool classPool = ClassPool.getDefault();
  private static final CtClass enhancedInterface;
  private static final Map<Class, Class> enhancements = new WeakHashMap<>();
  private static final Map<Class, Collection<Enhancement>> enhancers = new HashMap<>();
  private static final Logger LOGGER = LoggerFactory.getLogger(Enhancer.class);

  static {
    try {
      enhancedInterface = classPool.getCtClass(Enhanced.class.getCanonicalName());
    } catch (Exception e) {
      throw new RuntimeException("Failed to initialize enhancements", e);
    }

    AtomicInteger count = new AtomicInteger();
    TypeUtils.subClasses(Enhancement.class).forEach(enhancement -> {
      registerEnhancement(enhancement);
      count.incrementAndGet();
    });

    LOGGER.info("Loaded {} enhancers.", count.get());
  }

  public static void registerEnhancement(Class<? extends Enhancement> enhancement) {
      try {
        LOGGER.debug("Processing enhancement: {}", enhancement);
        Class target = TypeUtils.typeParameter(enhancement, Enhancement.class, 0);
        Enhancement<?> instance = enhancement.newInstance();
        if (!enhancers.containsKey(target)) {
          enhancers.put(target, new LinkedList<>());
        }
        enhancers.get(target).add(instance);
      } catch (Exception e) {
        throw new RuntimeException("Failed to load enhancement '" + enhancement + "'", e);
      }
  }

  public static <T, R extends T> Class<R> apply(Class<T> type) {
    if (!enhancements.containsKey(type)) {
      enhancements.put(type, enhance(type));
    }
    return enhancements.get(type);
  }

  protected static <T, R extends T> Class<R> enhance(Class<T> type) {
    try {
      if (!enhancers.containsKey(type)) {
        // no enhancements for this class
        // returning the same class
        return (Class<R>) type;
      }

      String name = type.getCanonicalName() + "$enhanced";
      CtClass source = classPool.getCtClass(type.getCanonicalName());
      CtClass target = classPool.makeClass(name, source);
      target.addInterface(enhancedInterface);

      enhancers.get(type).forEach(enhancer -> enhancer.apply(target));
      return (Class<R>) target.toClass();
    } catch (Exception e) {
      throw new RuntimeException("Failed to enhance '" + type + "'", e);
    }
  }
}
