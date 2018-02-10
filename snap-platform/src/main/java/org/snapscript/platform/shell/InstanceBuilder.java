package org.snapscript.platform.shell;

import java.util.List;

import org.snapscript.core.Module;
import org.snapscript.core.Reserved;
import org.snapscript.core.Scope;
import org.snapscript.core.State;
import org.snapscript.core.Type;
import org.snapscript.core.Value;
import org.snapscript.core.define.Instance;
import org.snapscript.core.define.PrimitiveInstance;
import org.snapscript.core.property.Property;

public class InstanceBuilder implements ShellBuilder {
   
   public InstanceBuilder() {
      super();
   }

   @Override
   public Object create(Type type, Class real) {
      Module module = type.getModule();
      Scope scope = type.getScope();
      
      try {
         Instance instance = new PrimitiveInstance(module, scope, type);
         List<Property> properties = type.getProperties();
         State state = instance.getState();
         Value value = Value.getTransient(instance);
         
         for(Property property : properties) {
            String name = property.getName();
            
            if(!name.equals(Reserved.TYPE_THIS)) {
               Type constraint = property.getType();
               Value field = Value.getReference(null, constraint);
            
               state.add(name, field);
            }
         }
         state.add(Reserved.TYPE_THIS, value);
         return instance;
      }catch(Exception e) {
         e.printStackTrace();
         return null;
      }
   }

}
