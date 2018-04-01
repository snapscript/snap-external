package org.snapscript.platform;

import org.snapscript.core.type.Type;

public class InvocationCacheTable<T> {

   private volatile InvocationCacheArray array;
   private volatile CacheAllocator allocator;
   
   public InvocationCacheTable() {
      this(10);
   }
   
   public InvocationCacheTable(int capacity) {
      this.array = new InvocationCacheArray(capacity, capacity);
      this.allocator = new CacheAllocator();
   }
   
   public InvocationCache get(Type type) {
      int index = type.getOrder(); // index 0 will cluster anonymous types
      int length = array.length();
      
      if(index >= length) {
         return allocator.allocate(type);
      }
      InvocationCache cache = array.get(index);
      
      if(cache == null) {
         return allocator.allocate(type);
      }
      return cache;
   }
   
   private class CacheAllocator {
      
      public CacheAllocator() {
         super();
      }
      
      public synchronized InvocationCache allocate(Type type) {
         int index = type.getOrder();
         InvocationCacheArray local = array.copy(index);
         InvocationCache cache = local.get(index);
         
         if(cache == null) {
            cache = new InvocationCache();
            local.set(index, cache);
            array = local;
         }
         return cache;
      }
   }
}