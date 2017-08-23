package org.snapscript.bridge;

import java.lang.reflect.Method;
import java.util.concurrent.Callable;

import org.snapscript.core.Context;
import org.snapscript.core.InternalStateException;
import org.snapscript.core.Module;
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

public class InvocationRouter {

   private final MethodComparator comparator;
   private final FunctionResolver resolver;
   private final ThisScopeBinder binder;
   private final BridgeBuilder builder;
   
   public InvocationRouter(BridgeBuilder builder, FunctionResolver resolver) {
      this.comparator = new MethodComparator();
      this.binder = new ThisScopeBinder();
      this.resolver = resolver;
      this.builder = builder;
   }
   
   public Object route(Bridge bridge, Method method, Object[] list) throws Throwable {
      Instance instance = (Instance)bridge.getInstance();
      Class owner = method.getDeclaringClass();
      Scope scope = binder.bind(instance, instance);
      
      if(owner != Bridge.class) {
         Invocation invocation = bind(bridge, instance, method, list);
         Result result = invocation.invoke(scope, bridge, list);
         Module module = scope.getModule();
         Context context = module.getContext();
         ProxyWrapper wrapper = context.getWrapper();
         Object value = result.getValue();
         
         return wrapper.toProxy(value);
      }
      return scope;
   }
   
   private Invocation bind(Bridge bridge, Instance instance, Method method, Object[] list) throws Throwable {
      String name = method.getName();
      Class real = bridge.getClass();
      Type type = instance.getType();
      Scope scope = binder.bind(instance, instance);
      Function function = resolver.resolve(type, name, list); 
      
      if (comparator.isAbstract(function)) {
         throw new InternalStateException("No implementaton of " + method + " for '" + type + "'");
      }
      if (comparator.isEqual(function, method)) {
         return builder.superInvocation(instance, real, method);
      }
      Module module = scope.getModule();
      Context context = module.getContext();
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