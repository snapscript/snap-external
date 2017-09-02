package org.snapscript.bridge.android;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;

import org.snapscript.bridge.InvocationRouter;
import org.snapscript.bridge.ThreadLocalHandler;
import org.snapscript.bridge.generate.BridgeInstance;
import org.snapscript.bridge.generate.BridgeInstanceBuilder;
import org.snapscript.core.Any;
import org.snapscript.core.ContextClassLoader;
import org.snapscript.core.Type;
import org.snapscript.core.bind.FunctionResolver;
import org.snapscript.core.bridge.BridgeBuilder;
import org.snapscript.core.function.Invocation;

public class AndroidBridgeBuilder implements BridgeBuilder {

   private final ProxyBuilderGenerator generator;   
   private final BridgeInstanceBuilder builder;
   private final ProxyBuilderWrapper wrapper;
   private final InvocationHandler handler;
   private final InvocationRouter router;
   private final ClassLoader loader;
   private final ThreadLocal local;

   public AndroidBridgeBuilder(FunctionResolver resolver) {
      this.loader = new ContextClassLoader(Any.class);
      this.router = new InvocationRouter(this, resolver);
      this.local = new ThreadLocal<BridgeInstance>();
      this.handler = new ThreadLocalHandler(local, router);
      this.generator = new ProxyBuilderGenerator(handler, loader);
      this.builder = new BridgeInstanceBuilder(generator, resolver, local);
      this.wrapper = new ProxyBuilderWrapper(loader);
   }

   @Override
   public Invocation superConstructor(Type real, Type base) {
      try {
         return builder.createInvocation(real, base);
      } catch (Exception e) {
         throw new IllegalStateException("Could not create super for '" + base + "'", e);
      } 
   }

   @Override
   public Invocation superMethod(Type real, Method method) {
      try {
         return wrapper.superInvocation(real, method);
      } catch (Exception e) {
         throw new IllegalStateException("Could not call super for '" + method + "'", e);
      }
   }

   @Override
   public Invocation thisMethod(Type real, Method method) {
      try {
         return wrapper.thisInvocation(method);
      } catch (Exception e) {
         throw new IllegalStateException("Could not create adapter for '" + method + "'", e);
      }
   }
   
   @Override
   public Invocation thisConstructor(Type real, Constructor constructor) {
      try {
         return wrapper.thisInvocation(constructor);
      } catch (Exception e) {
         throw new IllegalStateException("Could not create adapter for '" + constructor + "'", e);
      }
   }
}