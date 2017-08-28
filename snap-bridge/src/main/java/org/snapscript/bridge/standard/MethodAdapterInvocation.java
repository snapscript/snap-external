package org.snapscript.bridge.standard;

import java.lang.reflect.Method;

import org.snapscript.core.InternalStateException;
import org.snapscript.core.Result;
import org.snapscript.core.ResultType;
import org.snapscript.core.Scope;
import org.snapscript.core.function.Invocation;

public class MethodAdapterInvocation implements Invocation {
   
   private final Method method;
   
   public MethodAdapterInvocation(Method method) {
      this.method = method;
   }

   @Override
   public Result invoke(Scope scope, Object value, Object... arguments) {
      try {
         Object result = method.invoke(value, arguments);
         return ResultType.getNormal(result);
      }catch(Throwable e) {
         throw new InternalStateException("Could not invoke super", e);
      }
   }
}
   