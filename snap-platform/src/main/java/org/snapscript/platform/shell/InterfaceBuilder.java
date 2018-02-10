package org.snapscript.platform.shell;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.snapscript.core.Any;
import org.snapscript.core.ContextClassLoader;
import org.snapscript.core.Type;

public class InterfaceBuilder implements ShellBuilder {

   private final InvocationHandler handler;
   private final ClassLoader loader;
   
   public InterfaceBuilder() {
      this.loader = new ContextClassLoader(Any.class);
      this.handler = new EmptyHandler();
   }
   
   @Override
   public Object create(Type type, Class real) {
      try {
         if(real != null) {
            Class[] types = new Class[]{real};
            
            if(Map.class.isAssignableFrom(real)) {
               return Collections.emptyMap();
            }
            if(Set.class.isAssignableFrom(real)) {
               return Collections.emptySet();
            }
            if(List.class.isAssignableFrom(real)) {
               return Collections.emptyList();
            }
            if(real.isInterface()) {
               return Proxy.newProxyInstance(loader, types, handler);
            }
         }
      } catch(Exception e) {
         return null;
      }
      return null;
   }
   
   private static class EmptyHandler implements InvocationHandler {

      @Override
      public Object invoke(Object proxy, Method method, Object[] list) {
         return null;
      }
      
   }
}