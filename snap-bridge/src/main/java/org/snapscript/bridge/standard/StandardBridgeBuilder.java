package org.snapscript.bridge.standard;

import java.lang.reflect.Method;
import java.util.concurrent.Executor;

import org.snapscript.bridge.InvocationRouter;
import org.snapscript.bridge.generate.BridgeInstance;
import org.snapscript.bridge.generate.BridgeInstanceBuilder;
import org.snapscript.common.Cache;
import org.snapscript.common.CopyOnWriteCache;
import org.snapscript.core.Scope;
import org.snapscript.core.Type;
import org.snapscript.core.bind.FunctionResolver;
import org.snapscript.core.bridge.BridgeBuilder;
import org.snapscript.core.define.Instance;
import org.snapscript.core.function.Invocation;

public class StandardBridgeBuilder implements BridgeBuilder {
   
   private final Cache<Method, Invocation> invocations;
   private final MethodInterceptorHandler handler;
   private final EnhancerGenerator generator;
   private final BridgeInstanceBuilder builder;
   private final MethodProxyWrapper wrapper;
   private final InvocationRouter router;
   private final ThreadLocal local;
   private final Type type;

   public StandardBridgeBuilder(FunctionResolver resolver, Executor executor, Type type) {
      this.invocations = new CopyOnWriteCache<Method, Invocation>();
      this.router = new InvocationRouter(this, resolver);
      this.local = new ThreadLocal<BridgeInstance>();
      this.handler = new MethodInterceptorHandler(local, router);
      this.generator = new EnhancerGenerator(handler);
      this.builder = new BridgeInstanceBuilder(generator, resolver, type);
      this.wrapper = new MethodProxyWrapper();
      this.type = type;
   }

   @Override
   public Instance superInstance(Scope scope, Type real, Object... list) {
      try {
         BridgeInstance instance = builder.createInstance(scope, real, list);
         
         try{
            local.set(instance);
            instance.getBridge().setInstance(instance);
         } finally {
            local.set(null);
         }
         return instance;
      } catch (Exception e) {
         throw new IllegalStateException("Could not create super for '" + type + "'", e);
      } 
   }

   @Override
   public Invocation superInvocation(Scope scope, Class proxy, Method method) {
      try {
         Invocation invocation = invocations.fetch(method);
   
         if (invocation == null) {
            invocation = wrapper.superInvocation(scope, proxy, method);
            invocations.cache(method, invocation);
         }
         return invocation;
      } catch (Exception e) {
         throw new IllegalStateException("Could not call super for '" + method + "'", e);
      }
   }

   @Override
   public Invocation thisInvocation(Scope scope, Method method) {
      try {
         return wrapper.thisInvocation(scope, method);
      } catch (Exception e) {
         throw new IllegalStateException("Could not create invocation for '" + method + "'", e);
      }
   }
}