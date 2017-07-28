package org.snapscript.extend;

import org.snapscript.core.MapState;
import org.snapscript.core.Model;
import org.snapscript.core.Module;
import org.snapscript.core.State;
import org.snapscript.core.Type;
import org.snapscript.core.define.Instance;

public class ProxyInstance implements Instance {

   private final Module module;
   private final Object object;
   private final State state;
   private final Type type;
   private final Type real;

   public ProxyInstance(Module module, Object object, Type type, Type real) {
      this.state = new MapState();
      this.object = object;
      this.module = module;
      this.type = type;
      this.real = real;
   }

   @Override
   public Instance getInner() {
      return this;
   }

   @Override
   public Instance getOuter() {
      return this;
   }

   @Override
   public Instance getSuper() {
      return this;
   }
   
   @Override
   public Object getObject() {
      return object;
   }

   @Override
   public Type getType() {
      return real;
   }

   @Override
   public Module getModule() {
      return module;
   }

   @Override
   public State getState() {
      return state;
   }

   @Override
   public Model getModel() {
      return null;
   }

   @Override
   public Type getHandle() {
      return type;
   }
}