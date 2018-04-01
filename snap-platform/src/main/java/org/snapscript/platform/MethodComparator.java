package org.snapscript.platform;

import java.lang.reflect.Member;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;

import org.snapscript.core.function.Function;
import org.snapscript.core.function.Signature;
import org.snapscript.core.function.search.FunctionPointer;

public class MethodComparator {
   
   public MethodComparator() {
      super();
   }
   
   public boolean isAbstract(FunctionPointer call) {
      if(call != null) {
         Function function = call.getFunction();
         Signature signature = function.getSignature();
         
         if(signature != null) {
            return false;
         }
      }
      return true;
   }

   public boolean isEqual(FunctionPointer call, Method method) {
      if(call != null) {
         Function function = call.getFunction();
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