package org.snapscript.platform.standard;

import java.lang.reflect.Method;

import org.snapscript.core.InternalStateException;
import org.snapscript.core.Scope;
import org.snapscript.core.function.Invocation;

public class DelegateMethodInvocation implements Invocation {
   
   private final Method method;
   
   public DelegateMethodInvocation(Method method) {
      this.method = method;
   }

   @Override
   public Object invoke(Scope scope, Object value, Object... arguments) {
      try {
         return method.invoke(value, arguments);
      }catch(Throwable e) {
         throw new InternalStateException("Could not invoke " + method, e);
      }
   }
}