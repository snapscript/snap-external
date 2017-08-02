package org.snapscript.bridge.standard;

import org.snapscript.bridge.generate.ClassGenerator;
import org.snapscript.cglib.proxy.Enhancer;
import org.snapscript.cglib.proxy.MethodInterceptor;
import org.snapscript.core.Any;
import org.snapscript.core.ContextClassLoader;
import org.snapscript.core.Scope;
import org.snapscript.core.Type;
import org.snapscript.core.TypeCache;
import org.snapscript.core.convert.InterfaceCollector;

public class EnhancerGenerator implements ClassGenerator {

   private final InterfaceCollector collector;
   private final TypeCache<Class> cache;
   private final ClassLoader loader;
   
   public EnhancerGenerator(Class... interfaces) {
      this.collector = new InterfaceCollector(interfaces);
      this.loader = new ContextClassLoader(Any.class);
      this.cache = new TypeCache<Class>();      
   }
   
   @Override
   public Class generate(Scope scope, Type type, Class base) {
      Class proxy = cache.fetch(type);
      
      if(proxy == null) {
         proxy = create(scope, type, base);
         cache.cache(type, proxy);
      }
      return proxy;
   }
   
   private Class create(Scope scope, Type type, Class base) {
      Class[] types = collector.collect(type);
      
      try {
         Class[] handlers = new Class[] {MethodInterceptor.class};
         Enhancer enhancer = new Enhancer();
         
         enhancer.setClassLoader(loader);
         enhancer.setSuperclass(base);
         enhancer.setInterfaces(types); // ensure we can convert from object to Instance
         enhancer.setCallbackTypes(handlers);
         
         return enhancer.createClass();
      } catch(Exception e) {
         throw new IllegalStateException("Could not create proxy for " + type, e);
      }
   }
}
