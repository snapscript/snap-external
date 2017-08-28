package org.snapscript.bridge.android;

import java.lang.reflect.Constructor;
import java.lang.reflect.Method;

import org.snapscript.dx.stock.ProxyAdapter;
import org.snapscript.dx.stock.ProxyBuilder;

public class ProxyAdapterGenerator {
   
   private final ClassLoader loader;
   
   public ProxyAdapterGenerator(ClassLoader loader) {
      this.loader = loader;
   }

   public ProxyAdapter generate(Method method) {
      Class adapter = create(method);
      
      try {
         return (ProxyAdapter)adapter.newInstance();
      }catch(Exception e) {
         throw new IllegalStateException("Could not create adapter " + adapter, e);
      }
   }
   
   public ProxyAdapter generate(Constructor constructor) {
      Class adapter = create(constructor);
      
      try {
         return (ProxyAdapter)adapter.newInstance();
      }catch(Exception e) {
         throw new IllegalStateException("Could not create adapter " + adapter, e);
      }
   }
   
   private Class create(Method method) {
      try {
         ProxyBuilder builder = ProxyBuilder.forClass(Object.class);
         
         builder.parentClassLoader(loader);
         builder.implementing(ProxyAdapter.class);
         
         return builder.buildMethodAccessor(method);
      }catch(Exception e) {
         throw new IllegalStateException("Could not generate " + method, e);
      }
   }
   
   private Class create(Constructor constructor) {
      try {
         ProxyBuilder builder = ProxyBuilder.forClass(Object.class);
         
         builder.parentClassLoader(loader);
         builder.implementing(ProxyAdapter.class);
         
         return builder.buildConstructorAccessor(constructor);
      }catch(Exception e) {
         throw new IllegalStateException("Could not generate " + constructor, e);
      }
   }

}
