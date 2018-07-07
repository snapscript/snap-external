package org.snapscript.platform;

import java.lang.reflect.Method;

import org.snapscript.common.Cache;
import org.snapscript.core.error.InternalStateException;
import org.snapscript.core.function.Invocation;

public class InvocationCache {

   private volatile Cache<Method, Invocation> cache;
   private volatile Class<? extends Cache> type;
   
   public InvocationCache(Class<? extends Cache> type) {
      this.type = type;
   }
   
   public boolean contains(Method key) {
      if(cache != null) {
         return cache.contains(key);
      }
      return false;
   }
   
   public Invocation fetch(Method key) {
      if(cache != null) {
         return cache.fetch(key);
      }
      return null;
   }
   
   public void cache(Method key, Invocation invocation) {
      if(cache == null) {
         try {
            cache = type.newInstance();
         } catch(Exception e) {
            throw new InternalStateException("Could not create cache of " + type, e);
         }
      }
      cache.cache(key, invocation);
   }
}