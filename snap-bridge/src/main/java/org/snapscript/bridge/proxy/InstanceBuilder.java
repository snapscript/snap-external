package org.snapscript.bridge.proxy;

import org.snapscript.bridge.BridgeInstance;
import org.snapscript.core.Module;
import org.snapscript.core.Scope;
import org.snapscript.core.Type;
import org.snapscript.core.bind.FunctionResolver;
import org.snapscript.core.define.Instance;

public class InstanceBuilder {

   private final InstanceConverter converter;
   private final ObjectGenerator builder;
   private final Type type;
   
   public InstanceBuilder(ClassGenerator generator, FunctionResolver resolver, Type type) {
      this.builder = new ObjectGenerator(generator, resolver);
      this.converter = new InstanceConverter(type);
      this.type = type;
   }
   
   public Instance createInstance(Scope scope, Type real, Object... arguments) throws Exception {
      Instance instance = getInstance(scope, real, arguments);
      converter.convert(instance);
      return instance;
   }
   
   private Instance getInstance(Scope scope, Type real, Object... arguments) throws Exception {
      Module module = scope.getModule();
      Class typeToMock = type.getType();
      Object object = builder.createObject(scope, typeToMock, arguments);
      
      return new BridgeInstance(module, object, type, real);
      
   }
}
