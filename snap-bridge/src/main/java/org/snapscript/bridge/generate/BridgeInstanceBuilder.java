package org.snapscript.bridge.generate;

import org.snapscript.core.Module;
import org.snapscript.core.Scope;
import org.snapscript.core.Type;
import org.snapscript.core.bind.FunctionResolver;
import org.snapscript.core.define.Instance;

public class BridgeInstanceBuilder {

   private final BridgeInstanceConverter converter;
   private final ObjectGenerator generator;
   private final Type type;
   
   public BridgeInstanceBuilder(ClassGenerator generator, FunctionResolver resolver, Type type) {
      this.generator = new ObjectGenerator(generator, resolver);
      this.converter = new BridgeInstanceConverter(type);
      this.type = type;
   }
   
   public Instance createInstance(Scope scope, Type real, Object... arguments) throws Exception {
      Instance instance = createBridge(scope, real, arguments);
      converter.convert(instance);
      return instance;
   }
   
   private Instance createBridge(Scope scope, Type real, Object... arguments) throws Exception {
      Class base = type.getType();
      Module module = scope.getModule();
      Object object = generator.generate(scope, real, base, arguments);
      
      return new BridgeInstance(module, object, type, real);
      
   }
}