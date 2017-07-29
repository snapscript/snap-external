package org.snapscript.bridge;

import java.lang.reflect.Method;
import java.util.concurrent.Callable;

import org.snapscript.core.Context;
import org.snapscript.core.Reserved;
import org.snapscript.core.Result;
import org.snapscript.core.Scope;
import org.snapscript.core.bind.FunctionBinder;
import org.snapscript.core.bind.FunctionResolver;
import org.snapscript.core.bridge.BridgeBuilder;
import org.snapscript.core.define.Instance;
import org.snapscript.core.function.Function;
import org.snapscript.core.function.Invocation;

public abstract class AbstractHandler {

   protected final FunctionResolver matcher;
   protected final BridgeBuilder extender;
   protected final Instance instance;
   protected final Scope scope;
   
   public AbstractHandler(FunctionResolver matcher, BridgeBuilder extender, Instance instance, Scope scope) {
      this.instance = instance;
      this.extender = extender;
      this.matcher = matcher;
      this.scope = scope;
   }

   public Object invoke(Object obj, Method method, Object[] args) throws Throwable {
      Object o = instance.getState().get(Reserved.TYPE_THIS).getValue();
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
      Context context = scope.getModule().getContext();
      Result result = call.call();
      Object res = result.getValue();
      
      return context.getWrapper().fromProxy(res);
   }
   
   private Invocation getSuperCall(Class type, Method method) {
      return extender.createInvocation(scope, type, method);
   }
}
