package org.snapscript.bridge.generate;

import static org.snapscript.core.Reserved.TYPE_THIS;

import java.util.List;

import org.snapscript.core.State;
import org.snapscript.core.Type;
import org.snapscript.core.Value;
import org.snapscript.core.ValueType;
import org.snapscript.core.define.Instance;
import org.snapscript.core.property.Property;
import org.snapscript.core.property.PropertyValue;

public class BridgeInstanceConverter {
   
   private final Type type;
   
   public BridgeInstanceConverter(Type type) {
      this.type = type;
   }

   public void convert(Instance instance) {
      Value self = ValueType.getReference(instance);
      List<Type> types = type.getTypes();
      State state = instance.getState();
      Object bridge = instance.getBridge();
      
      update(state, bridge, type);
      
      for(Type type : types) {
         update(state, bridge, type);
      }
      state.add(TYPE_THIS, self);
   }

   private void update(State state, Object object, Type type) {
      List<Property> properties = type.getProperties();
      
      for(Property property : properties) {
         String name = property.getName();

         if(!name.equals(TYPE_THIS)) {
            Object current = state.get(name);
            
            if(current == null) {
               Value value = new PropertyValue(property, object, name);
               state.add(name, value);
            }
         }
      }
   }
}