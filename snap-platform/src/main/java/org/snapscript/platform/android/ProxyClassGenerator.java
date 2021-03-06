package org.snapscript.platform.android;

import org.snapscript.core.EntityCache;
import org.snapscript.core.type.Type;
import org.snapscript.core.convert.InterfaceCollector;
import org.snapscript.platform.generate.ClassGenerator;

public class ProxyClassGenerator implements ClassGenerator{

   private final ProxyClassLoader generator;
   private final InterfaceCollector collector;
   private final EntityCache<Class> cache;
   
   public ProxyClassGenerator(ProxyClassLoader generator) {
      this.collector = new InterfaceCollector();
      this.cache = new EntityCache<Class>();
      this.generator = generator;
   }
   
   @Override
   public Class generate(Type type, Class base) {
      Class proxy = cache.fetch(type);
      
      if(proxy == null) {
         Class[] interfaces = collector.collect(type);
         
         proxy = generator.loadClass(base, interfaces);
         cache.cache(type, proxy);
      }
      return proxy;
   }
}