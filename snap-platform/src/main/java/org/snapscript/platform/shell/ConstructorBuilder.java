package org.snapscript.platform.shell;

import java.lang.reflect.Constructor;

import org.snapscript.core.Type;

public class ConstructorBuilder implements ShellBuilder {
   
   public ConstructorBuilder() {
      super();
   }
   
   @Override
   public Object create(Type type, Class real) {
      try {
         if(real != null) {            
            Constructor constructor = real.getDeclaredConstructor();
            
            if(constructor != null) {
               constructor.setAccessible(true);
               return constructor.newInstance();
            }
         }
      } catch(Exception e) {
         return null;
      }
      return null;
   }

}