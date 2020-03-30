package com.onkiup.linker.enhancer.annotations;

import com.onkiup.linker.enhancer.Enhanced;
import com.onkiup.linker.enhancer.Enhancer;

import org.junit.Assert;
import org.junit.Test;

import javassist.CtMethod;

/**
 * @author : chedim (chedim@chedim-Surface-Pro-3)
 * @file : PreloaderTest
 * @created : Saturday Mar 21, 2020 22:17:31 EDT
 */

public class PreloaderTest {

  public static class TestClass {
    private String value;

    public String value() {
      return value;
    }
  } 

  public static class TestPreloader implements Preloader<PreloaderTest, String> {

    @Override
    public boolean applicable(CtMethod test) {
      return true;
    }

    @Override
    public String preload(String invokee) {
      throw new RuntimeException("P");
      //return "Hello";
    }

  }

  @Test
  public void testPreloaderInjection() throws Exception {
    Enhancer.registerEnhancement(TestPreloader.class);
    Class<? extends TestClass> enhanced = Enhancer.apply(TestClass.class);
   TestClass result = enhanced.newInstance();
    Assert.assertTrue(result instanceof Enhanced);
    Assert.assertEquals("Hello", result.value());
  }
}
