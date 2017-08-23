package org.snapscript.bridge;

import java.lang.reflect.Method;

import org.snapscript.core.function.Function;
import org.snapscript.core.function.Signature;

public class MethodComparator {
   
   public MethodComparator() {
      super();
   }
   
   public boolean isAbstract(Function function) {
      if(function != null) {
         Signature signature = function.getSignature();
         
         if(signature != null) {
            return false;
         }
      }
      return true;
   }

   public boolean isEqual(Function function, Method method) {
      if(function != null) {
         Signature signature = function.getSignature();
         Object source = signature.getSource();
         
         if(source != null) {
            return method.equals(source);
         }
      }
      return false;
   }
}