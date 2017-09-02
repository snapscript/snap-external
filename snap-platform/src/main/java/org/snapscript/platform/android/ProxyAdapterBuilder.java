package org.snapscript.platform.android;

import java.lang.reflect.Constructor;
import java.lang.reflect.Method;

import org.snapscript.dx.stock.ProxyAdapter;

public class ProxyAdapterBuilder {
   
   private final ProxyClassLoader loader;
   
   public ProxyAdapterBuilder(ProxyClassLoader loader) {
      this.loader = loader;
   }

   public ProxyAdapter generate(Method method) {
      Class adapter = loader.loadClass(method);
      
      try {
         return (ProxyAdapter)adapter.newInstance();
      }catch(Exception e) {
         throw new IllegalStateException("Could not create adapter " + adapter, e);
      }
   }
   
   public ProxyAdapter generate(Constructor constructor) {
      Class adapter = loader.loadClass(constructor);
      
      try {
         return (ProxyAdapter)adapter.newInstance();
      }catch(Exception e) {
         throw new IllegalStateException("Could not create adapter " + adapter, e);
      }
   }
}
