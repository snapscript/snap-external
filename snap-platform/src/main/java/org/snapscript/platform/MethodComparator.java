package org.snapscript.platform;

import java.lang.reflect.Member;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;

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
         Member source = signature.getSource();
         
         if(source != null) {
            int modifiers = source.getModifiers();
            
            if(!Modifier.isAbstract(modifiers)) {
               return method.equals(source);
            }
         }
      }
      return false;
   }
}