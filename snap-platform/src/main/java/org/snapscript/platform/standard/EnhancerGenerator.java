package org.snapscript.platform.standard;

import org.snapscript.cglib.proxy.Callback;
import org.snapscript.cglib.proxy.Enhancer;
import org.snapscript.cglib.proxy.MethodInterceptor;
import org.snapscript.core.Any;
import org.snapscript.core.ContextClassLoader;
import org.snapscript.core.Type;
import org.snapscript.core.TypeCache;
import org.snapscript.core.convert.InterfaceCollector;
import org.snapscript.core.platform.Bridge;
import org.snapscript.platform.generate.ClassGenerator;

public class EnhancerGenerator implements ClassGenerator{

   private final InterfaceCollector collector;
   private final TypeCache<Class> cache;
   private final Callback[] interceptors;
   private final ClassLoader loader;
   
   public EnhancerGenerator(MethodInterceptor interceptor) {
      this.interceptors = new Callback[] {interceptor};
      this.loader = new ContextClassLoader(Any.class);
      this.collector = new InterfaceCollector();
      this.cache = new TypeCache<Class>();      
   }
   
   @Override
   public Class generate(Type type, Class base) {
      Class proxy = cache.fetch(type);
      
      if(proxy == null) {
         proxy = create(type, base);
         Enhancer.registerCallbacks(proxy, interceptors);
         cache.cache(type, proxy);
      }
      return proxy;
   }
   
   private Class create(Type type, Class base) {
      Class[] interfaces = collector.collect(type);
      
      try {
         Enhancer enhancer = new Enhancer();
         Class[] types = new Class[] {MethodInterceptor.class};
         
         enhancer.setClassLoader(loader);
         enhancer.setSuperclass(base);
         enhancer.setInterceptDuringConstruction(true);
         enhancer.setBeanInterfaces(Bridge.class);
         enhancer.setInterfaces(interfaces); // ensure we can convert from object to Instance
         enhancer.setCallbackTypes(types);
         
         return enhancer.createClass();
      } catch(Exception e) {
         throw new IllegalStateException("Could not create proxy for " + type, e);
      }
   }
}