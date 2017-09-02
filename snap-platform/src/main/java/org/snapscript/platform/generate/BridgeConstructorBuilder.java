package org.snapscript.platform.generate;

import org.snapscript.core.Result;
import org.snapscript.core.ResultType;
import org.snapscript.core.Scope;
import org.snapscript.core.Type;
import org.snapscript.core.bind.FunctionResolver;
import org.snapscript.core.function.Invocation;

public class BridgeConstructorBuilder {

   private final BridgeInstanceConverter converter;
   private final BridgeInstanceBuilder builder;
   private final ThreadLocal local;
   
   public BridgeConstructorBuilder(ClassGenerator generator, FunctionResolver resolver, ThreadLocal local) {
      this.builder = new BridgeInstanceBuilder(generator, resolver);
      this.converter = new BridgeInstanceConverter();
      this.local = local;
   }
   
   public Invocation createSuperConstructor(Type real, Type base) {
      return new BridgeInvocation(real, base);
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
         BridgeInstance instance = builder.createInstance(real, base, list);

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