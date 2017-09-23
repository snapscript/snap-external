package org.snapscript.platform;

import org.snapscript.common.Cache;
import org.snapscript.common.CopyOnWriteCache;
import org.snapscript.core.function.Invocation;

public class InvocationCache {

   private final Cache<Object, Invocation> cache;
   
   public InvocationCache() {
      this.cache = new CopyOnWriteCache<Object, Invocation>();
   }
   
   public boolean contains(Object key) {
      return cache.contains(key);
   }
   
   public Invocation fetch(Object key) {
      return cache.fetch(key);
   }
   
   public void cache(Object key, Invocation invocation) {
      cache.cache(key, invocation);
   }
}