package org.snapscript.platform.generate;

import org.snapscript.core.Entity;
import org.snapscript.core.convert.proxy.ProxyWrapper;
import org.snapscript.core.platform.Bridge;
import org.snapscript.core.property.Property;
import org.snapscript.core.variable.Data;
import org.snapscript.core.variable.DataMapper;
import org.snapscript.core.variable.Value;

public class BridgeValue extends Value {

   private final BridgeInstance instance;   
   private final ProxyWrapper wrapper;
   private final Property property;
   private final String name;

   public BridgeValue(BridgeInstance instance, ProxyWrapper wrapper, Property property, String name) {
      this.instance = instance;
      this.property = property;
      this.wrapper = wrapper;
      this.name = name;
   }
   
   @Override
   public boolean isProperty() {
      return true;
   }
   
   @Override
   public Data getData() {
      return DataMapper.toData(this);
   }   
   
   @Override
   public Entity getSource() {
      return instance.getType();
   }
   
   @Override
   public <T> T getValue() {
      try {
         BridgeHolder holder = instance.getHolder();
         Bridge bridge = holder.getBridge();         
         Object object = property.getValue(bridge);
         
         return (T)wrapper.fromProxy(object);
      } catch(Exception e) {
         throw new IllegalStateException("Could not get '" + name + "'", e);
      }
   }

   @Override
   public void setData(Data value) {
      try {
         Object object = value.getValue();
         BridgeHolder holder = instance.getHolder();
         Bridge bridge = holder.getBridge();
         Object proxy = wrapper.toProxy(object);

         property.setValue(bridge, proxy);
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