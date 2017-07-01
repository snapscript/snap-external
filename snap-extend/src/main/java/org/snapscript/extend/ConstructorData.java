package org.snapscript.extend;

public class ConstructorData {
   private Object[] args;
   private Class[] types;

   public ConstructorData(Class[] types, Object[] args) {
      this.types = types;
      this.args = args;
   }
   
   public Class[] getTypes(){
      return types;
   }
   public Object[] getArguments(){
      return args;
   }
}

