package org.snapscript.extend;

import java.lang.reflect.Method;
import java.util.concurrent.Callable;

import org.snapscript.core.Context;
import org.snapscript.core.Reserved;
import org.snapscript.core.Result;
import org.snapscript.core.Scope;
import org.snapscript.core.bind.FunctionBinder;
import org.snapscript.core.bind.FunctionResolver;
import org.snapscript.core.define.Instance;
import org.snapscript.core.function.Function;
import org.snapscript.core.function.Invocation;

public abstract class AbstractHandler {

   private final Instance inst;
   private final Scope scope;
   private final FunctionResolver matcher;
   private final Context c;

   public AbstractHandler(Instance inst, Scope scope, FunctionResolver matcher, Context c) {
      this.inst = inst;
      this.scope = scope;
      this.matcher = matcher;
      this.c = c;
   }

   public Object invoke(Object obj, Method method, Object[] args) throws Throwable {
      Object o = inst.getState().get(Reserved.TYPE_THIS).getValue();
      Instance real = (Instance) o;
      
      if(method.getName().equals("extract") && method.getReturnType().equals(Object.class) && method.getParameterTypes().length == 0) {
         return real;
      }
      String name = method.getName();
      Function func = matcher.resolve(real.getType(), name, args);
      if (func != null && func.getSignature() != null && func.getSignature().getSource() != null && func.getSignature().getSource().equals(method)) {
         return getSuperCall(obj.getClass(), method).invoke(real, obj, args).getValue();
      }
      FunctionBinder binder = scope.getModule().getContext().getBinder();
      Callable<Result> call = binder.bind(real, real, name, args);
      if (call == null) {
         return getSuperCall(obj.getClass(), method).invoke(real, obj, args).getValue(); // here the ScopeDispatcher needs to say SuperInstance::getObject --> MethodProxy::ivokeSuper
      }
      Result result = call.call();
      Object res = result.getValue();
      return c.getWrapper().fromProxy(res);
   }
   
   protected abstract Invocation getSuperCall(Class type, Method method);
}
