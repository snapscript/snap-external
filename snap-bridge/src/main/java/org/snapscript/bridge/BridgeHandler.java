package org.snapscript.bridge;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.util.concurrent.Callable;

import org.snapscript.core.Context;
import org.snapscript.core.Result;
import org.snapscript.core.Scope;
import org.snapscript.core.Type;
import org.snapscript.core.bind.FunctionBinder;
import org.snapscript.core.bind.FunctionResolver;
import org.snapscript.core.bridge.Bridge;
import org.snapscript.core.bridge.BridgeBuilder;
import org.snapscript.core.convert.ProxyWrapper;
import org.snapscript.core.define.Instance;
import org.snapscript.core.function.Function;
import org.snapscript.core.function.Invocation;
import org.snapscript.tree.define.ThisScopeBinder;

public class BridgeHandler implements InvocationHandler {

   private final MethodComparator comparator;
   private final FunctionResolver resolver;
   private final ThisScopeBinder binder;
   private final BridgeBuilder builder;
   private final Instance instance;
   private final Context context;
   
   public BridgeHandler(BridgeBuilder builder, FunctionResolver resolver, Context context, Instance instance) {
      this.comparator = new MethodComparator();
      this.binder = new ThisScopeBinder();
      this.resolver = resolver;
      this.instance = instance;
      this.context = context;
      this.builder = builder;
   }

   @Override
   public Object invoke(Object object, Method method, Object[] list) throws Throwable {
      Class owner = method.getDeclaringClass();
      Scope scope = binder.bind(instance, instance);
      
      if(owner != Bridge.class) {
         Invocation invocation = bind(object, method, list);
         Result result = invocation.invoke(scope, object, list);
         ProxyWrapper wrapper = context.getWrapper();
         Object value = result.getValue();
         
         return wrapper.fromProxy(value);
      }
      return scope;
   }
   
   private Invocation bind(Object object, Method method, Object[] list) throws Throwable {
      String name = method.getName();
      Class real = object.getClass();
      Type type = instance.getType();
      Scope scope = binder.bind(instance, instance);
      Function function = resolver.resolve(type, name, list); 
      
      if (comparator.isEqual(method, function)) {
         return builder.superInvocation(instance, real, method);
      }
      FunctionBinder binder = context.getBinder();
      Callable<Result> call = binder.bind(scope, scope, name, list);
      
      if (call == null) {
         return builder.superInvocation(instance, real, method);
      }
      return new CallableInvocation(call);
   
   }
   
   private static class CallableInvocation implements Invocation {

      private final Callable<Result> call;
      
      public CallableInvocation(Callable<Result> call) {
         this.call = call;
      }
      
      @Override
      public Result invoke(Scope scope, Object object, Object... list) throws Exception {
         return call.call();
      }
      
   }
}