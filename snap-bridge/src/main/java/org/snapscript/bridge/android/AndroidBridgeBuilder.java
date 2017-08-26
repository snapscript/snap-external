package org.snapscript.bridge.android;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;

import org.snapscript.bridge.InvocationRouter;
import org.snapscript.bridge.ThreadLocalHandler;
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

public class AndroidBridgeBuilder implements BridgeBuilder {

   private final Cache<Method, Invocation> invocations;
   private final ProxyBuilderGenerator generator;
   private final BridgeInstanceBuilder builder;
   private final ProxyBuilderWrapper wrapper;
   private final InvocationHandler handler;
   private final InvocationRouter router;
   private final ThreadLocal local;
   private final Type type;

   public AndroidBridgeBuilder(FunctionResolver resolver, Type type) {
      this.invocations = new CopyOnWriteCache<Method, Invocation>();
      this.router = new InvocationRouter(this, resolver);
      this.local = new ThreadLocal<BridgeInstance>();
      this.handler = new ThreadLocalHandler(local, router);
      this.generator = new ProxyBuilderGenerator(handler);
      this.builder = new BridgeInstanceBuilder(generator, resolver, type);
      this.wrapper = new ProxyBuilderWrapper();
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
            invocation = wrapper.createInvocation(scope, proxy, method);
            invocations.cache(method, invocation);
         }
         return invocation;
      } catch (Exception e) {
         throw new IllegalStateException("Could not call super for '" + method + "'", e);
      }
   }
}