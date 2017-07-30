package org.snapscript.bridge.android;

import org.snapscript.bridge.generate.ClassGenerator;
import org.snapscript.common.Cache;
import org.snapscript.common.CopyOnWriteCache;
import org.snapscript.core.Scope;
import org.snapscript.dx.stock.ProxyBuilder;

public class ProxyBuilderGenerator implements ClassGenerator {

   private final Cache<Class, Class> cache;
   private final Class[] interfaces;
   
   public ProxyBuilderGenerator(Class... interfaces) {
      this.cache = new CopyOnWriteCache<Class, Class>();
      this.interfaces = interfaces;
   }
   
   @Override
   public Class generate(Scope scope, Class type) {
      Class proxy = cache.fetch(type);
      
      if(proxy == null) {
         proxy = create(scope, type);
         cache.cache(type, proxy);
      }
      return proxy;
   }
   
   private Class create(Scope scope, Class type) {
      try {
         return ProxyBuilder.forClass(type).implementing(interfaces).buildProxyClass();
      }catch(Exception e) {
         throw new IllegalStateException("Could not create proxy for " + type, e);
      }
   }
}
