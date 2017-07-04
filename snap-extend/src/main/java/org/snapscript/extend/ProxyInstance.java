package org.snapscript.extend;

import org.snapscript.core.MapState;
import org.snapscript.core.Model;
import org.snapscript.core.Module;
import org.snapscript.core.Scope;
import org.snapscript.core.State;
import org.snapscript.core.Type;
import org.snapscript.core.define.Instance;

public class ProxyInstance implements Instance {

   private State state;
   private Type type;
   private Type real;

   public ProxyInstance(Scope scope, Type type, Type real) {
      this.state = new MapState();
      this.type = type;
      this.real = real;
   }

   @Override
   public Type getType() {
      return real;
   }

   @Override
   public Module getModule() {
      return type.getModule();
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

}