package org.snapscript.platform.android;

import java.lang.reflect.Constructor;
import java.lang.reflect.Method;

import org.snapscript.common.Cache;
import org.snapscript.common.CopyOnWriteCache;
import org.snapscript.core.Type;
import org.snapscript.core.function.Invocation;
import org.snapscript.platform.InvocationCache;
import org.snapscript.platform.InvocationCacheTable;

public class ProxyBuilderWrapper {
   
   private final Cache<Object, Invocation> adapters;
   private final InvocationCacheTable table;
   private final ProxyMethodBuilder builder;

   public ProxyBuilderWrapper(ClassLoader loader) {
      this.adapters = new CopyOnWriteCache<Object, Invocation>();
      this.builder = new ProxyMethodBuilder(loader);
      this.table = new InvocationCacheTable();
   }

   public Invocation superInvocation(Type real, Method method) {
      InvocationCache cache = table.get(real);
      Invocation invocation = cache.fetch(method);
      
      if(invocation == null) {
         invocation = builder.superInvocation(method);
         cache.cache(real, invocation);
      }
      return invocation;
   }
   
   public Invocation thisInvocation(Method method) {
      Invocation invocation = adapters.fetch(method);
      
      if (invocation == null) {
         invocation = builder.thisInvocation(method);
         adapters.cache(method, invocation);
      }
      return invocation;
   }
   
   public Invocation thisInvocation(Constructor constructor) {
      Invocation invocation = adapters.fetch(constructor);
      
      if (invocation == null) {
         invocation = builder.thisInvocation(constructor);
         adapters.cache(constructor, invocation);
      }
      return invocation;
   }
}