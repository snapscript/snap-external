package org.snapscript.platform.standard;

import java.lang.reflect.Constructor;

import org.snapscript.core.InternalStateException;
import org.snapscript.core.Scope;
import org.snapscript.core.function.Invocation;

public class DelegateConstructorInvocation implements Invocation {
   
   private final Constructor constructor;
   
   public DelegateConstructorInvocation(Constructor constructor) {
      this.constructor = constructor;
   }

   @Override
   public Object invoke(Scope scope, Object value, Object... arguments) {
      try {
         return constructor.newInstance(arguments);
      }catch(Throwable e) {
         throw new InternalStateException("Could not invoke super", e);
      }
   }
}
   