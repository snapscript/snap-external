package org.snapscript.extend.proxy;

import org.snapscript.core.Context;
import org.snapscript.core.Module;
import org.snapscript.core.Type;
import org.snapscript.core.TypeLoader;
import org.snapscript.core.bind.FunctionResolver;
import org.snapscript.core.define.Instance;

public class ObjectBuilder {
   
   private final ConstructorResolver resolver;
   private final ClassGenerator generator;
   
   public ObjectBuilder(ClassGenerator generator, FunctionResolver resolver) {
      this.resolver = new ConstructorResolver(resolver);
      this.generator = generator;
   }

   public Object create(Instance instance, Class type, Object... arguments) throws Exception {
      Class proxy = generator.generate(instance, type);
      Module module = instance.getModule();
      Context context = module.getContext();
      TypeLoader loader = context.getLoader();
      Type match = loader.loadType(type);
      ConstructorArguments data = resolver.findConstructor(instance, match, arguments);
      Object[] converted = data.getArguments();
      Class[] types = data.getTypes();
      
      return proxy.getDeclaredConstructor(types).newInstance(converted);
   }
}
