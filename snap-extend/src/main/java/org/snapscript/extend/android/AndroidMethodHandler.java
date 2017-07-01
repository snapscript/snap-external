package org.snapscript.extend.android;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.util.concurrent.Callable;

import org.snapscript.core.Bug;
import org.snapscript.core.Context;
import org.snapscript.core.Reserved;
import org.snapscript.core.Result;
import org.snapscript.core.Scope;
import org.snapscript.core.bind.FunctionBinder;
import org.snapscript.core.bind.ObjectFunctionMatcher;
import org.snapscript.core.define.Instance;
import org.snapscript.core.function.Function;
import org.snapscript.dx.stock.ProxyBuilder;

@Bug("this is a total mess")
public class AndroidMethodHandler implements InvocationHandler  {
   private final Instance inst;
   private final Scope scope;
   private final ObjectFunctionMatcher matcher;
   private final Context c;

   public AndroidMethodHandler(Instance inst, Scope scope, ObjectFunctionMatcher matcher, Context c) {
      this.inst = inst;
      this.scope = scope;
      this.matcher = matcher;
      this.c = c;
   }

   @Override
   public Object invoke(Object obj, Method method, Object[] args) throws Throwable {
      Object o = inst.getState().get(Reserved.TYPE_THIS).getValue();
      Instance real = (Instance) o;
      
      if(method.getName().equals("extract") && method.getReturnType().equals(Object.class) && method.getParameterTypes().length == 0) {
         return real;
      }
      String name = method.getName();
      Function func = matcher.resolve(real.getType(), name, args);
      if (func != null && func.getSignature() != null && func.getSignature().getSource() != null && func.getSignature().getSource().equals(method)) {
         return ProxyBuilder.callSuper(obj, method, args);
         //return MethodProxyResolver.getSuperCall(obj.getClass(), name, method.getParameterTypes()).invoke(obj, args);
      }
      FunctionBinder binder = scope.getModule().getContext().getBinder();
      Callable<Result> call = binder.bind(real, real, name, args);
      if (call == null) {
         return ProxyBuilder.callSuper(obj, method, args);
         //return MethodProxyResolver.getSuperCall(obj.getClass(), name, method.getParameterTypes()).invoke(obj, args); // here the ScopeDispatcher needs to say SuperInstance::getObject --> MethodProxy::ivokeSuper
      }
      Result result = call.call();
      Object res = result.getValue();
      return c.getWrapper().fromProxy(res);
   }
}