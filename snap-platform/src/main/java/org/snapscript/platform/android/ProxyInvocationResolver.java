package org.snapscript.platform.android;

import java.lang.reflect.Constructor;
import java.lang.reflect.Method;

import org.snapscript.common.Cache;
import org.snapscript.common.IdentityCache;
import org.snapscript.core.function.Invocation;
import org.snapscript.core.type.Type;
import org.snapscript.platform.InvocationCache;
import org.snapscript.platform.InvocationCacheTable;

public class ProxyInvocationResolver {
   
   private final Cache<Object, Invocation> adapters;
   private final ProxyInvocationBuilder builder;
   private final InvocationCacheTable table;

   public ProxyInvocationResolver(ProxyClassLoader generator) {
      this.table = new InvocationCacheTable(IdentityCache.class);
      this.adapters = new IdentityCache<Object, Invocation>();
      this.builder = new ProxyInvocationBuilder(generator);
   }

   public Invocation resolveSuperMethod(Type real, Method method) {
      InvocationCache cache = table.get(real);
      Invocation invocation = cache.fetch(method);
      
      if(invocation == null) {
         invocation = builder.createSuperMethod(method);
         cache.cache(method, invocation);
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