package org.snapscript.bridge.generate;

import static org.snapscript.core.Reserved.TYPE_THIS;

import java.util.List;

import org.snapscript.core.State;
import org.snapscript.core.Type;
import org.snapscript.core.Value;
import org.snapscript.core.ValueType;
import org.snapscript.core.property.Property;

public class BridgeInstanceConverter {
   
   private final Type type;
   
   public BridgeInstanceConverter(Type type) {
      this.type = type;
   }

   public void convert(BridgeInstance instance) {
      Value self = ValueType.getReference(instance);
      List<Type> types = type.getTypes();
      State state = instance.getState();
      
      update(instance, state, type);
      
      for(Type type : types) {
         update(instance, state, type);
      }
      state.add(TYPE_THIS, self);
   }

   private void update(BridgeInstance instance, State state, Type type) {
      List<Property> properties = type.getProperties();
      
      for(Property property : properties) {
         String name = property.getName();

         if(!name.equals(TYPE_THIS)) {
            Object current = state.get(name);
            
            if(current == null) {
               Value value = new BridgeValue(instance, property, name);
               state.add(name, value);
            }
         }
      }
   }
}