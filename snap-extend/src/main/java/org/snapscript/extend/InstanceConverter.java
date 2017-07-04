package org.snapscript.extend;

import static org.snapscript.core.Reserved.TYPE_THIS;

import java.util.List;

import org.snapscript.core.Bug;
import org.snapscript.core.State;
import org.snapscript.core.Type;
import org.snapscript.core.Value;
import org.snapscript.core.ValueType;
import org.snapscript.core.define.Instance;
import org.snapscript.core.property.Property;
import org.snapscript.core.property.PropertyValue;

public class InstanceConverter {

   @Bug("The converter shoud cache the data to be created")
   public static void convert(Instance instance, Object object, Type type) {
      Value real = ValueType.getReference(object, type);
      Value self = ValueType.getReference(instance);
      List<Type> types = type.getTypes();
      State state = instance.getState();
      
      instance.getState().add("real", real);
      instance.getState().add(TYPE_THIS, self);
      
      update(state, object, type);
      
      for(Type x : types) {
         update(state, object, x);
      }
   }

   private static void update(State state, Object object, Type type) {
      List<Property> properties = type.getProperties();
      
      for(Property prop : properties) {
         String name = prop.getName();
         Object current = state.get(name);
         
         if(current == null) {
            Value value = new PropertyValue(prop, object, name);
            state.add(name, value);
         }
      }
   }
}
