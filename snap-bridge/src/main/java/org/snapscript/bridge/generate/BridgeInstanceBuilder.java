package org.snapscript.bridge.generate;

import org.snapscript.core.Module;
import org.snapscript.core.Result;
import org.snapscript.core.ResultType;
import org.snapscript.core.Scope;
import org.snapscript.core.Type;
import org.snapscript.core.bind.FunctionResolver;
import org.snapscript.core.function.Invocation;

public class BridgeInstanceBuilder {

   private final BridgeInstanceConverter converter;
   private final ObjectGenerator generator;
   private final ThreadLocal local;
   
   public BridgeInstanceBuilder(ClassGenerator generator, FunctionResolver resolver, ThreadLocal local) {
      this.generator = new ObjectGenerator(generator, resolver);
      this.converter = new BridgeInstanceConverter();
      this.local = local;
   }
   
   public Invocation createInvocation(Type real, Type base) {
      return new BridgeInvocation(real, base);
   }
   
   private BridgeInstance createInstance(Type real, Type base, Object... arguments) throws Exception {
      BridgeInstance instance = createBridge(real, base, arguments);
      converter.convert(instance);
      return instance;
   }
   
   private BridgeInstance createBridge(Type real, Type base, Object... arguments) throws Exception {
      Class require = base.getType();
      Scope outer = real.getScope();
      Module module = base.getModule();
      BridgeHolder holder = generator.generate(base, require, arguments);
      
      return new BridgeInstance(holder, module, outer, real, base);
      
   }
   
   private class BridgeInvocation implements Invocation {
      
      private final Type real;
      private final Type base;
      
      public BridgeInvocation(Type real, Type base) {
         this.real = real;
         this.base = base;
      }

      @Override
      public Result invoke(Scope scope, Object object, Object... list) throws Exception {
         BridgeInstance instance = createBridge(real, base, list);

         try{
            converter.convert(instance);
            local.set(instance);
            instance.getBridge().setInstance(instance);
         } finally {
            local.set(null);
         }
         return ResultType.getNormal(instance);
      }
      
   }
}