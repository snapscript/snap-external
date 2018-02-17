package org.snapscript.platform.shell;

import java.util.List;

import org.snapscript.core.Module;
import org.snapscript.core.Reserved;
import org.snapscript.core.Scope;
import org.snapscript.core.State;
import org.snapscript.core.Type;
import org.snapscript.core.Value;
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
         Scope outer = scope.getStack();
         State state = outer.getState();
         List<Property> properties = type.getProperties();
         Value value = Value.getTransient(outer, type);
         
         for(Property property : properties) {
            String name = property.getName();
            
            if(!name.equals(Reserved.TYPE_THIS)) {
               Type constraint = property.getType();
               Value field = Value.getReference(null, constraint);
            
               state.add(name, field);
            }
         }
         state.add(Reserved.TYPE_THIS, value);
         return outer;
      }catch(Exception e) {
         e.printStackTrace();
         return null;
      }
   }

}
