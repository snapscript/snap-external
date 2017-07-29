package org.snapscript.bridge;

import java.lang.reflect.Method;
import java.util.concurrent.Callable;

import org.snapscript.core.Context;
import org.snapscript.core.Module;
import org.snapscript.core.Result;
import org.snapscript.core.Scope;
import org.snapscript.core.Type;
import org.snapscript.core.bind.FunctionBinder;
import org.snapscript.core.bind.FunctionResolver;
import org.snapscript.core.bridge.BridgeBuilder;
import org.snapscript.core.convert.ProxyWrapper;
import org.snapscript.core.define.Instance;
import org.snapscript.core.function.Function;
import org.snapscript.core.function.Invocation;
import org.snapscript.tree.define.ThisScopeBinder;

public abstract class BridgeHandler {

   protected final FunctionResolver matcher;
   protected final ThisScopeBinder binder;
   protected final BridgeBuilder extender;
   protected final Instance instance;
   
   public BridgeHandler(BridgeBuilder extender, FunctionResolver matcher, Instance instance) {
      this.binder = new ThisScopeBinder();
      this.instance = instance;
      this.extender = extender;
      this.matcher = matcher;
   }

   public Object invoke(Object object, Method method, Object[] list) throws Throwable {
      String name = method.getName();
      Class real = object.getClass();
      Scope scope = binder.bind(instance, instance);
      
      if(method.getName().equals("extract") && method.getReturnType().equals(Object.class) && method.getParameterTypes().length == 0) {
         return scope;
      }
      Type type = scope.getType();
      Module module = scope.getModule();
      Context context = module.getContext();
      Function function = matcher.resolve(type, name, list); // this is saying if 
      
      if (function != null && function.getSignature() != null && function.getSignature().getSource() != null && function.getSignature().getSource().equals(method)) {
         return getSuperCall(real, method).invoke(scope, object, list).getValue();
      }
      FunctionBinder binder = context.getBinder();
      Callable<Result> call = binder.bind(scope, scope, name, list);
      
      if (call == null) {
         return getSuperCall(real, method).invoke(scope, object, list).getValue(); // here the ScopeDispatcher needs to say SuperInstance::getObject --> MethodProxy::ivokeSuper
      }
      ProxyWrapper wrapper = context.getWrapper();
      Result result = call.call();
      Object value = result.getValue();
      
      return wrapper.fromProxy(value);
   }
   
   private Invocation getSuperCall(Class type, Method method) {
      return extender.superInvocation(instance, type, method);
   }
}
