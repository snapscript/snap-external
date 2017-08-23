package org.snapscript.bridge.generate;

import org.snapscript.core.MapState;
import org.snapscript.core.Model;
import org.snapscript.core.Module;
import org.snapscript.core.Scope;
import org.snapscript.core.State;
import org.snapscript.core.Type;
import org.snapscript.core.bridge.Bridge;
import org.snapscript.core.define.Instance;

public class BridgeInstance implements Instance {

   private final BridgeHolder holder;
   private final Module module;
   private final State state;
   private final Type real;

   public BridgeInstance(BridgeHolder holder, Module module, Scope scope, Type real) {
      this.state = new MapState(null, scope);
      this.holder = holder;
      this.module = module;
      this.real = real;
   }
   
   public BridgeHolder getHolder() {
      return holder;
   }
   
   @Override
   public Bridge getBridge() {
      return holder.getBridge();
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
      return real;
   }
}