package org.snapscript.extend.standard;

import org.snapscript.cglib.proxy.MethodProxy;
import org.snapscript.core.InternalStateException;
import org.snapscript.core.Result;
import org.snapscript.core.ResultType;
import org.snapscript.core.Scope;
import org.snapscript.core.function.Invocation;

public class MethodProxyInvocation implements Invocation {
   
   private final MethodProxy proxy;
   
   public MethodProxyInvocation(MethodProxy proxy) {
      this.proxy = proxy;
   }

   @Override
   public Result invoke(Scope scope, Object value, Object... arguments) {
      try {
         Object result = proxy.invokeSuper(value, arguments);
         return ResultType.getNormal(result);
      }catch(Throwable e) {
         throw new InternalStateException("Could not invoke super", e);
      }
   }
}