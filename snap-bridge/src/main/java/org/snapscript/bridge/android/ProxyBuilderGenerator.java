package org.snapscript.bridge.android;

import java.lang.reflect.InvocationHandler;

import org.snapscript.bridge.generate.ClassGenerator;
import org.snapscript.core.Type;
import org.snapscript.core.TypeCache;
import org.snapscript.core.bridge.Bridge;
import org.snapscript.core.convert.InterfaceCollector;
import org.snapscript.dx.stock.ProxyBuilder;

public class ProxyBuilderGenerator implements ClassGenerator{

   private final InterfaceCollector collector;
   private final InvocationHandler handler;
   private final TypeCache<Class> cache;
   private final ClassLoader loader;
   
   public ProxyBuilderGenerator(InvocationHandler handler, ClassLoader loader) {
      this.collector = new InterfaceCollector();
      this.cache = new TypeCache<Class>();
      this.handler = handler;
      this.loader = loader;
   }
   
   @Override
   public Class generate(Type type, Class base) {
      Class proxy = cache.fetch(type);
      
      if(proxy == null) {
         proxy = create(type, base);
         cache.cache(type, proxy);
      }
      return proxy;
   }
   
   private Class create(Type type, Class base) {
      try {
         Class[] interfaces = collector.collect(type);
         ProxyBuilder builder = ProxyBuilder.forClass(base);
         
         builder.implementing(interfaces);
         builder.implementingBeans(Bridge.class);
         builder.parentClassLoader(loader);
         builder.handler(handler);
         
         return builder.buildProxyClass();
      }catch(Exception e) {
         throw new IllegalStateException("Type '" + type + "' could not extend "+ base, e);
      }
   }
}