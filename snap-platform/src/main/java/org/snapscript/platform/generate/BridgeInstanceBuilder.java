package org.snapscript.platform.generate;

import org.snapscript.core.Module;
import org.snapscript.core.Scope;
import org.snapscript.core.Type;
import org.snapscript.core.bind.FunctionResolver;

public class BridgeInstanceBuilder {

   private final ObjectGenerator generator;
   
   public BridgeInstanceBuilder(ClassGenerator generator, FunctionResolver resolver) {
      this.generator = new ObjectGenerator(generator, resolver);
   }
   
   public BridgeInstance createInstance(Type real, Type base, Object... arguments) throws Exception {
      Class require = base.getType();
      Scope outer = real.getScope();
      Module module = base.getModule();
      BridgeHolder holder = generator.generate(base, require, arguments);
      
      return new BridgeInstance(holder, module, outer, real, base);  
   }
}
