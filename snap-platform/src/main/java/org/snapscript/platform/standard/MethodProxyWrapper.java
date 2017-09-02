package org.snapscript.platform.standard;

import java.lang.reflect.Constructor;
import java.lang.reflect.Method;

import org.snapscript.core.Type;
import org.snapscript.core.function.Invocation;
import org.snapscript.platform.InvocationCache;
import org.snapscript.platform.InvocationCacheTable;

public class MethodProxyWrapper {

   private final InvocationCacheTable table;
   private final MethodProxyBuilder builder;

   public MethodProxyWrapper() {
      this.table = new InvocationCacheTable();
      this.builder = new MethodProxyBuilder();
   }

   public Invocation superInvocation(Type real, Method method) {
      InvocationCache cache = table.get(real);
      Invocation invocation = cache.fetch(method);
      
      if(invocation == null) {
         invocation = builder.createInvocation(method);
         cache.cache(real, invocation);
      }
      return invocation;
   }
   
   public Invocation thisInvocation(Method method) {
      return new MethodAdapterInvocation(method);
   }
   
   public Invocation thisInvocation(Constructor constructor) {
      return new ConstructorAdapterInvocation(constructor);
   }
}