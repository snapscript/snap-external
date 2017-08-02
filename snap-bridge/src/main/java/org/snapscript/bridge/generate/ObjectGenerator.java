package org.snapscript.bridge.generate;

import org.snapscript.core.Context;
import org.snapscript.core.Module;
import org.snapscript.core.Scope;
import org.snapscript.core.Type;
import org.snapscript.core.TypeLoader;
import org.snapscript.core.bind.FunctionResolver;

public class ObjectGenerator {
   
   private final ConstructorResolver resolver;
   private final ClassGenerator generator;
   
   public ObjectGenerator(ClassGenerator generator, FunctionResolver resolver) {
      this.resolver = new ConstructorResolver(resolver);
      this.generator = generator;
   }

   public Object generate(Scope scope, Type real, Class base, Object... arguments) throws Exception {
      Class proxy = generator.generate(scope, real, base);
      Module module = scope.getModule();
      Context context = module.getContext();
      TypeLoader loader = context.getLoader();
      Type match = loader.loadType(base);
      ConstructorArguments data = resolver.findConstructor(scope, match, arguments);
      Object[] converted = data.getArguments();
      Class[] types = data.getTypes();
      
      return proxy.getDeclaredConstructor(types).newInstance(converted);
   }
}