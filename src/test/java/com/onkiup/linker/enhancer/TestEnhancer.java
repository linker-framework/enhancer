package com.onkiup.linker.enhancer;

import com.onkiup.linker.enhancer.annotations.Preloader;

import org.junit.Test;

import javassist.CtMethod;

/**
 * @author : chedim (chedim@chedim-Surface-Pro-3)
 * @file : TestEnhancer
 * @created : Wednesday Mar 18, 2020 19:29:53 EDT
 */

public class TestEnhancer {
  public static class TestTarget {
    public String message;

    public String message() {
      return message;
    }
  }

  public static class TestPreloader implements Preloader<TestTarget, String> {

    @Override
    public boolean applicable(CtMethod test) {
      return "message".equals(test.getName());
    }

    @Override
    public String preload(String invokee) {
      return "Hello, World!";
    }

  }

  @Test
  public void testEnhancing() {
  }
}
