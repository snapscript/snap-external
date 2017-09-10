package org.snapscript.platform.android;

import java.lang.reflect.Constructor;
import java.lang.reflect.Method;

import org.snapscript.common.Cache;
import org.snapscript.common.CopyOnWriteCache;
import org.snapscript.core.Type;
import org.snapscript.core.function.Invocation;
import org.snapscript.platform.InvocationCache;
import org.snapscript.platform.InvocationCacheTable;

public class ProxyInvocationResolver {
   
   private final Cache<Object, Invocation> adapters;
   private final InvocationCacheTable table;
   private final ProxyInvocationBuilder builder;

   public ProxyInvocationResolver(ProxyClassLoader generator) {
      this.adapters = new CopyOnWriteCache<Object, Invocation>();
      this.builder = new ProxyInvocationBuilder(generator);
      this.table = new InvocationCacheTable();
   }

   public Invocation resolveSuperMethod(Type real, Method method) {
      InvocationCache cache = table.get(real);
      Invocation invocation = cache.fetch(method);
      
      if(invocation == null) {
         invocation = builder.createSuperMethod(method);
         cache.cache(real, invocation);
      }
      return invocation;
   }
   
   public Invocation resolveMethod(Method method) {
      Invocation invocation = adapters.fetch(method);
      
      if (invocation == null) {
         invocation = builder.createMethod(method);
         adapters.cache(method, invocation);
      }
      return invocation;
   }
   
   public Invocation resolveConstructor(Constructor constructor) {
      Invocation invocation = adapters.fetch(constructor);
      
      if (invocation == null) {
         invocation = builder.createConstructor(constructor);
         adapters.cache(constructor, invocation);
      }
      return invocation;
   }
}