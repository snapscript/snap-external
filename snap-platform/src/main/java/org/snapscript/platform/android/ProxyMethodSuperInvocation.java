package org.snapscript.platform.android;

import java.lang.reflect.Method;

import org.snapscript.core.error.InternalStateException;
import org.snapscript.core.function.Invocation;
import org.snapscript.core.scope.Scope;
import org.snapscript.dx.stock.ProxyBuilder;

public class ProxyMethodSuperInvocation implements Invocation {
   
   private final Method method;
   
   public ProxyMethodSuperInvocation(Method method) {
      this.method = method;
   }

   @Override
   public Object invoke(Scope scope, Object value, Object... arguments) {
      try {
         return ProxyBuilder.callSuper(value, method, arguments);
      }catch(Throwable e) {
         throw new InternalStateException("Could not invoke " + method, e);
      }
   }
}