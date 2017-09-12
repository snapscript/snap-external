package org.snapscript.platform.generate;

import org.snapscript.core.ArrayTable;
import org.snapscript.core.Index;
import org.snapscript.core.MapState;
import org.snapscript.core.Module;
import org.snapscript.core.Scope;
import org.snapscript.core.StackIndex;
import org.snapscript.core.State;
import org.snapscript.core.Table;
import org.snapscript.core.Type;
import org.snapscript.core.define.Instance;
import org.snapscript.core.platform.Bridge;

public class BridgeInstance implements Instance {

   private final BridgeHolder holder;
   private final Module module;
   private final Index index;
   private final Table table;
   private final State state;
   private final Type real;
   private final Type base;

   public BridgeInstance(BridgeHolder holder, Module module, Scope scope, Type real, Type base) {
      this.state = new MapState(scope);
      this.table = new ArrayTable();
      this.index = new StackIndex();
      this.holder = holder;
      this.module = module;
      this.real = real;
      this.base = base;
   }
   
   public BridgeHolder getHolder() {
      return holder;
   }
   
   @Override
   public Bridge getBridge() {
      return holder.getBridge();
   }
   
   @Override
   public Object getProxy(){
      return null;
   }

   @Override
   public Index getIndex() {
      return index;
   }
   
   @Override
   public Table getTable(){
      return table;
   }
   
   @Override
   public Instance getStack() {
      return this;
   }

   @Override
   public Instance getScope() {
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
   
   public Type getBase() {
      return base;
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
   public Type getHandle() {
      return real;
   }
}