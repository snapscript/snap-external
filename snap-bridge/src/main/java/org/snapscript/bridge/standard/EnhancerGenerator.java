package org.snapscript.bridge.standard;

import org.snapscript.bridge.proxy.ClassGenerator;
import org.snapscript.cglib.proxy.Enhancer;
import org.snapscript.common.Cache;
import org.snapscript.common.CopyOnWriteCache;
import org.snapscript.core.Scope;
import org.snapscript.core.convert.InterfaceCollector;

public class EnhancerGenerator implements ClassGenerator {

   private final InterfaceCollector collector;
   private final Cache<Class, Class> cache;
   
   public EnhancerGenerator(Class... interfaces) {
      this.collector = new InterfaceCollector(interfaces);
      this.cache = new CopyOnWriteCache<Class, Class>();      
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
      Class[] types = collector.collect(scope);
      
      try {
         Class[] handlers = new Class[] {MethodInterceptorHandler.class};
         Enhancer enhancer = new Enhancer();
         
         enhancer.setSuperclass(type);
         enhancer.setInterfaces(types); // ensure we can convert from object to Instance
         enhancer.setCallbackTypes(handlers);
         
         return enhancer.createClass();
      } catch(Exception e) {
         throw new IllegalStateException("Could not create proxy for " + type, e);
      }
   }
}
