package org.snapscript.bridge.android;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.util.concurrent.Executor;

import org.snapscript.bridge.InvocationRouter;
import org.snapscript.bridge.ThreadLocalHandler;
import org.snapscript.bridge.generate.BridgeInstance;
import org.snapscript.bridge.generate.BridgeInstanceBuilder;
import org.snapscript.common.Cache;
import org.snapscript.common.CopyOnWriteCache;
import org.snapscript.core.Any;
import org.snapscript.core.Bug;
import org.snapscript.core.ContextClassLoader;
import org.snapscript.core.Scope;
import org.snapscript.core.Type;
import org.snapscript.core.bind.FunctionResolver;
import org.snapscript.core.bridge.BridgeBuilder;
import org.snapscript.core.define.Instance;
import org.snapscript.core.function.Invocation;

public class AndroidBridgeBuilder implements BridgeBuilder {

   @Bug("we should not crreate a BridgeBuilder based on type, there should be only one")
   private final static Cache<Object, Invocation> adapters = new CopyOnWriteCache<Object, Invocation>();
   
   private final Cache<Object, Invocation> builders;
   private final ProxyBuilderGenerator generator;
   private final BridgeInstanceBuilder builder;
   private final ProxyBuilderWrapper wrapper;
   private final InvocationHandler handler;
   private final InvocationRouter router;
   private final ClassLoader loader;
   private final ThreadLocal local;
   private final Type type;

   public AndroidBridgeBuilder(FunctionResolver resolver, Executor executor, Type type) {
      this.builders = new CopyOnWriteCache<Object, Invocation>();
      this.loader = new ContextClassLoader(Any.class);
      this.router = new InvocationRouter(this, resolver);
      this.local = new ThreadLocal<BridgeInstance>();
      this.handler = new ThreadLocalHandler(local, router);
      this.generator = new ProxyBuilderGenerator(handler, loader);
      this.builder = new BridgeInstanceBuilder(generator, resolver, type);
      this.wrapper = new ProxyBuilderWrapper(loader, executor);
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
         Invocation invocation = builders.fetch(method);
   
         if (invocation == null) {
            invocation = wrapper.superInvocation(scope, proxy, method);
            builders.cache(method, invocation);
         }
         return invocation;
      } catch (Exception e) {
         throw new IllegalStateException("Could not call super for '" + method + "'", e);
      }
   }

   @Override
   public Invocation thisInvocation(Scope scope, Method method) {
      try {
         Invocation invocation = adapters.fetch(method);
   
         if (invocation == null) {
            invocation = wrapper.thisInvocation(scope, method);
            adapters.cache(method, invocation);
         }
         return invocation;
      } catch (Exception e) {
         throw new IllegalStateException("Could not create adapter for '" + method + "'", e);
      }
   }
   
   @Override
   public Invocation thisInvocation(Scope scope, Constructor constructor) {
      try {
         Invocation invocation = adapters.fetch(constructor);
   
         if (invocation == null) {
            invocation = wrapper.thisInvocation(scope, constructor);
            adapters.cache(constructor, invocation);
         }
         return invocation;
      } catch (Exception e) {
         throw new IllegalStateException("Could not create adapter for '" + constructor + "'", e);
      }
   }
}