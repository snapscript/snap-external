package org.snapscript.bridge.android;

import org.snapscript.bridge.generate.ClassGenerator;
import org.snapscript.core.Any;
import org.snapscript.core.ContextClassLoader;
import org.snapscript.core.Scope;
import org.snapscript.core.Type;
import org.snapscript.core.TypeCache;
import org.snapscript.core.convert.InterfaceCollector;
import org.snapscript.dx.stock.ProxyBuilder;

public class ProxyBuilderGenerator implements ClassGenerator {

   private final InterfaceCollector collector;
   private final TypeCache<Class> cache;
   private final ClassLoader loader;
   
   public ProxyBuilderGenerator(Class... interfaces) {
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
      try {
         Class[] interfaces = collector.collect(type);
         ProxyBuilder builder = ProxyBuilder.forClass(base);
         
         return builder.implementing(interfaces).parentClassLoader(loader).buildProxyClass();
      }catch(Exception e) {
         throw new IllegalStateException("Type '" + type + "' could not extend "+ base, e);
      }
   }
}