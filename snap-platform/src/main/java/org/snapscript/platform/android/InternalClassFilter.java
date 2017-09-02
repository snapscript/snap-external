package org.snapscript.platform.android;

import java.lang.reflect.Constructor;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.lang.reflect.Proxy;

import org.snapscript.core.Compilation;
import org.snapscript.core.Evaluation;
import org.snapscript.core.Statement;
import org.snapscript.core.platform.Bridge;

public class InternalClassFilter {
   
   public InternalClassFilter(){
      super();
   }
   
   public boolean accept(Constructor constructor) {
      Class type = constructor.getDeclaringClass();
      int modifiers = constructor.getModifiers();
      
      if(Bridge.class.isAssignableFrom(type)) {
         return false;
      }
      if(Statement.class.isAssignableFrom(type)) {
         return false;
      }
      if(Evaluation.class.isAssignableFrom(type)) {
         return false;
      }
      if(Compilation.class.isAssignableFrom(type)) {
         return false;
      }
      if(Proxy.class.isAssignableFrom(type)) {
         return false;
      }
      return Modifier.isPublic(modifiers);
   }
   
   public boolean accept(Method method) {
      Class type = method.getDeclaringClass();
      int modifiers = method.getModifiers();
      
      if(Bridge.class.isAssignableFrom(type)) {
         return false;
      }
      if(Statement.class.isAssignableFrom(type)) {
         return false;
      }
      if(Evaluation.class.isAssignableFrom(type)) {
         return false;
      }
      if(Compilation.class.isAssignableFrom(type)) {
         return false;
      }
      if(Proxy.class.isAssignableFrom(type)) {
         return false;
      }
      return Modifier.isPublic(modifiers);
   }

}
