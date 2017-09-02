package org.snapscript.platform.standard;

import java.lang.reflect.Constructor;

import org.snapscript.core.InternalStateException;
import org.snapscript.core.Result;
import org.snapscript.core.ResultType;
import org.snapscript.core.Scope;
import org.snapscript.core.function.Invocation;

public class DelegateConstructorInvocation implements Invocation {
   
   private final Constructor constructor;
   
   public DelegateConstructorInvocation(Constructor constructor) {
      this.constructor = constructor;
   }

   @Override
   public Result invoke(Scope scope, Object value, Object... arguments) {
      try {
         Object result = constructor.newInstance(arguments);
         return ResultType.getNormal(result);
      }catch(Throwable e) {
         throw new InternalStateException("Could not invoke super", e);
      }
   }
}
   