package org.snapscript.bridge.generate;

import java.lang.reflect.Constructor;
import java.util.concurrent.Callable;

import org.snapscript.core.Context;
import org.snapscript.core.Module;
import org.snapscript.core.Scope;
import org.snapscript.core.Type;
import org.snapscript.core.TypeLoader;
import org.snapscript.core.bind.FunctionResolver;
import org.snapscript.core.bridge.Bridge;

public class ObjectGenerator {
   
   private final ConstructorResolver resolver;
   private final ClassGenerator generator;
   
   public ObjectGenerator(ClassGenerator generator, FunctionResolver resolver) {
      this.resolver = new ConstructorResolver(resolver);
      this.generator = generator;
   }

   public BridgeHolder generate(Scope scope, Type real, Class base, Object... arguments) throws Exception {
      BridgeConstructor builder = create(scope, real, base, arguments);
      return new BridgeHolder(builder);
   }
   
   private BridgeConstructor create(Scope scope, Type real, Class base, Object... arguments) throws Exception {
      Class proxy = generator.generate(scope, real, base);
      return new BridgeConstructor(scope, proxy, base, arguments);
   }
   
   private class BridgeConstructor implements Callable<Bridge> {
      
      private final Object[] arguments;
      private final Scope scope;
      private final Class proxy;
      private final Class base;
      
      public BridgeConstructor(Scope scope, Class proxy, Class base, Object... arguments) {
         this.arguments = arguments;
         this.scope = scope;
         this.proxy = proxy;
         this.base = base;
      }

      @Override
      public Bridge call() throws Exception {
         Module module = scope.getModule();
         Context context = module.getContext();
         TypeLoader loader = context.getLoader();
         Type match = loader.loadType(base);
         ConstructorArguments data = resolver.resolve(scope, match, arguments);
         Object[] converted = data.getArguments();
         Class[] types = data.getTypes();
         Constructor factory = proxy.getDeclaredConstructor(types);
         
         return (Bridge)factory.newInstance(converted);
      }
      
   }
}