package org.snapscript.extend.normal;

import java.lang.reflect.Method;

import org.snapscript.cglib.proxy.MethodInterceptor;
import org.snapscript.cglib.proxy.MethodProxy;
import org.snapscript.core.Bug;
import org.snapscript.core.Context;
import org.snapscript.core.Scope;
import org.snapscript.core.bind.FunctionResolver;
import org.snapscript.core.define.Instance;
import org.snapscript.core.function.Invocation;
import org.snapscript.extend.AbstractHandler;

@Bug("this is a total mess")
public class NormalHandler extends AbstractHandler implements MethodInterceptor  {
   
   public NormalHandler(Instance inst, Scope scope, FunctionResolver matcher, Context c) {
      super(inst, scope, matcher, c);
   }

   @Override
   public Object intercept(Object obj, Method method, Object[] args, MethodProxy sssss) throws Throwable {
      return invoke(obj, method, args);
   }

   @Override
   protected Invocation getSuperCall(Class type, Method method) {
      return NormalProxyResolver.getSuperCall(type, method);
   }
}