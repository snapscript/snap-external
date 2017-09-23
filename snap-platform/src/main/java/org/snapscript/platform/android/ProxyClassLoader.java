package org.snapscript.platform.android;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;

import org.snapscript.core.Any;
import org.snapscript.core.ContextClassLoader;
import org.snapscript.core.platform.Bridge;
import org.snapscript.dx.stock.ProxyAdapterBuilder;
import org.snapscript.dx.stock.ProxyBuilder;

public class ProxyClassLoader {

   private final InvocationHandler handler;
   private final ClassLoader loader;
   
   public ProxyClassLoader(InvocationHandler handler) {
      this.loader = new ContextClassLoader(Any.class);
      this.handler = handler;
   }

   public synchronized Class loadClass(Class base, Class[] interfaces) {
      try {
         ProxyBuilder builder = ProxyBuilder.forClass(base);
         
         builder.implementing(interfaces);
         builder.implementingBeans(Bridge.class);
         builder.parentClassLoader(loader);
         builder.handler(handler);
         
         return builder.buildProxyClass();
      }catch(Exception e) {
         throw new IllegalStateException("Could not generate proxy for "+ base, e);
      }
   }
   
   public synchronized Class loadClass(Constructor constructor) {
      try {
         ProxyAdapterBuilder builder = ProxyAdapterBuilder.forClass(Object.class);
         
         builder.parentClassLoader(loader);
         
         return builder.buildAccessor(constructor);
      }catch(Exception e) {
         throw new IllegalStateException("Could not generate " + constructor, e);
      }
   }
   
   public synchronized Class loadClass(Method method) {
      try {
         ProxyAdapterBuilder builder = ProxyAdapterBuilder.forClass(Object.class);
         
         builder.parentClassLoader(loader);
         
         return builder.buildAccessor(method);
      }catch(Exception e) {
         throw new IllegalStateException("Could not generate " + method, e);
      }
   }
}