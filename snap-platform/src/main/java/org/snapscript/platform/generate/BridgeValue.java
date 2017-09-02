package org.snapscript.platform.generate;

import org.snapscript.core.Value;
import org.snapscript.core.platform.Bridge;
import org.snapscript.core.property.Property;

public class BridgeValue extends Value {

   private final BridgeInstance instance;   
   private final Property property;
   private final String name;

   public BridgeValue(BridgeInstance instance, Property property, String name) {
      this.instance = instance;
      this.property = property;
      this.name = name;
   }
   
   @Override
   public boolean isProperty() {
      return true;
   }
   
   @Override
   public <T> T getValue() {
      try {
         BridgeHolder holder = instance.getHolder();
         Bridge object = holder.getBridge();
         
         return (T)property.getValue(object);
      } catch(Exception e) {
         throw new IllegalStateException("Could not get '" + name + "'", e);
      }
   }

   @Override
   public void setValue(Object value) {
      try {
         BridgeHolder holder = instance.getHolder();
         Bridge object = holder.getBridge();
         
         property.setValue(object, value);
      }catch(Exception e) {
         throw new IllegalStateException("Could not set '" + name + "'", e);
      }
   }
   
   @Override
   public int getModifiers() {
      return property.getModifiers();
   }

   public String getName(){
      return name;
   }
   
   
   @Override
   public String toString() {
      return String.valueOf(property);
   }
}