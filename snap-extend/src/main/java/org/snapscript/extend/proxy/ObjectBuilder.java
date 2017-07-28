package org.snapscript.extend.proxy;

import org.snapscript.core.Context;
import org.snapscript.core.Module;
import org.snapscript.core.Scope;
import org.snapscript.core.Type;
import org.snapscript.core.TypeLoader;
import org.snapscript.core.bind.FunctionResolver;

public class ObjectBuilder {
   
   private final ConstructorResolver resolver;
   private final ClassGenerator generator;
   
   public ObjectBuilder(ClassGenerator generator, FunctionResolver resolver) {
      this.resolver = new ConstructorResolver(resolver);
      this.generator = generator;
   }

   public Object create(Scope scope, Class type, Object... arguments) throws Exception {
      Class proxy = generator.generate(scope, type);
      Module module = scope.getModule();
      Context context = module.getContext();
      TypeLoader loader = context.getLoader();
      Type match = loader.loadType(type);
      ConstructorArguments data = resolver.findConstructor(scope, match, arguments);
      Object[] converted = data.getArguments();
      Class[] types = data.getTypes();
      
      return proxy.getDeclaredConstructor(types).newInstance(converted);
   }
}
