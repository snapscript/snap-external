package org.snapscript.extend;

import java.util.HashMap;
import java.util.Map;

import org.snapscript.core.Bug;
import org.snapscript.core.Reserved;
import org.snapscript.core.Type;
import org.snapscript.core.ValueType;
import org.snapscript.core.define.Instance;
import org.snapscript.core.property.Property;
import org.snapscript.core.property.PropertyValue;

public class InstanceConverter {

   @Bug("The converter shoud cache the data to be created")
   public static void convert(final Type t, Instance inst, Object obj) {
      Map<String, Property> properties = new HashMap<String, Property>();
      inst.getState().add("real", ValueType.getReference(obj, t));
      inst.getState().add(Reserved.TYPE_THIS, ValueType.getReference(inst));
      for(Property prop : t.getProperties()) {
         String name = prop.getName();
         if(!properties.containsKey(name)) {
            properties.put(name, prop);
            try {
               inst.getState().add(name, new PropertyValue(prop, obj, name));
            }catch(Exception e){}
         }
      }
      for(Type x : t.getTypes()) {
         for(Property prop : x.getProperties()) {
            String name = prop.getName();
            if(!properties.containsKey(name)) {
               properties.put(name, prop);
               try {
                  inst.getState().add(name, new PropertyValue(prop, obj, name));
               }catch(Exception e){}
            }
         }
      }
   }
}
